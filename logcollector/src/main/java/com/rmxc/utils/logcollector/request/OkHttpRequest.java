package com.rmxc.utils.logcollector.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.utils.logcollector.enumeration.LogCollectorDefaultConfig;
import com.rmxc.utils.logcollector.loadbalancer.LogServer;
import com.rmxc.utils.logcollector.loadbalancer.Rule;
import com.rmxc.utils.logcollector.request.bean.LogRecord;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OKHttp版本传输请求的代码实现,
 * 如果需要apache版本实现 重新实现接口即可,
 * 二者性能基本无差距
 *  okhttp更加灵活 扩展性更强
 * @author zhanbq
 */
public class OkHttpRequest implements Request {

    /**
     * log初始化完毕 ,这里可以使用log
     */
    private static final Logger log = LoggerFactory.getLogger(OkHttpRequest.class);

    OkHttpClient okHttpClient;



    private OkHttpRequest() {
        if(null == okHttpClient){
            this.okHttpClient = new OkHttpClient();
        }
        okHttpClient.newBuilder()
                .connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();
    }


    @Override
    public <T,E> void request(Rule loadbalanceRule, byte[] payload, LogRecord<T> record, E event, String requestPath, FailedRequestCallback<E> failedDeliveryCallback) {
        if(null == okHttpClient || null == loadbalanceRule){
            return;
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(record));
        //获取负载算法 筛选的host
        String url = loadbalanceRule.choose().getHost()+requestPath;

        url = getUrlWithQueryString(url,record.buildParams());
        okhttp3.Request postRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(postRequest);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("发送日志失败: {}",e);
                failedDeliveryCallback.onFailedRequest(event,e);
//                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(null != response){

                    response.body().close();
                }
            }
        });
    }

    @Override
    public <E> Boolean registIndex(Rule loadbalanceRule,String requestPath,String index, String alias) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject registIndexBody = new JSONObject();
        registIndexBody.put("index",index);
        registIndexBody.put("alias",alias);
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(registIndexBody));
        String url = loadbalanceRule.choose().getHost() + requestPath;
        okhttp3.Request postRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(postRequest);
        Response response = null;
        try {
            response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }finally {
            if(null != response){

                response.body().close();
            }
        }
    }

    @Override
    public <T,E> void request(Rule loadbalanceRule, LogRecord<T> record, E e, FailedRequestCallback<E> failedDeliveryCallback) {

    }

    /**
     * 组装表单请求 参数
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            /*
            过滤空的key
             */
            if (value == null) {
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }
    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }


    /**
     * 单例
     */
    private static class SingletonRequest {
        /* private */
        static final Request instance = new OkHttpRequest();
    }
    public static Request getInstance() {
        // 外围类能直接访问内部类（不管是否是静态的）的私有变量
        return SingletonRequest.instance;
    }

    @Override
    public Boolean ping(LogServer logServer){
        String pingPath = "/serv/ping";
        String url = logServer.getHost()+ LogCollectorDefaultConfig.HEARTBEAT_DETECTION_PATH;
        okhttp3.Request getRequest = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(getRequest);
        Response response = null;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            String result = String.valueOf(body);
            if(!response.isSuccessful()){
                logServer.setAlive(false);
                logServer.setReadyToServe(false);
            }
            System.out.println("ping server:{"+logServer.getIp()+"} port:{"+logServer.getPort()+"} result:{"+result+"}");
            log.info("ping server:{} port:{} result:{}",logServer.getIp(),logServer.getPort(),result);
            return response.isSuccessful();
        } catch (IOException e) {
            log.error("ping server IOException 网络IO异常, serverInfo,",logServer);
            logServer.setAlive(false);
            logServer.setReadyToServe(false);
            return false;
        }finally {
            if(null != response){

                response.body().close();
            }
        }
    }

}
