package com.rmxc.utils.logcollector.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.utils.logcollector.loadbalancer.Rule;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpRequest implements Request {

    OkHttpClient okHttpClient;



    private OkHttpRequest() {
        this.okHttpClient = new OkHttpClient()
                .newBuilder()
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000, TimeUnit.MILLISECONDS)
                .build();
    }


    @Override
    public <T,E> void request(Rule loadbalanceRule, byte[] payload, LogRecord<T> record, E event, String requestPath,FailedRequestCallback<E> failedDeliveryCallback) {
        if(null == okHttpClient || null == loadbalanceRule){
            return;
        }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, JSON.toJSONString(record));
        String url = loadbalanceRule.choose().getHost()+requestPath;
        url = getUrlWithQueryString(url,record.buildParams());
        okhttp3.Request postRequest = new okhttp3.Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(postRequest);
//        System.out.println("loadbalanceRule = [" + loadbalanceRule + "], record = [" + record + "], e = [" + event + "], failedDeliveryCallback = [" + failedDeliveryCallback + "]");
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedDeliveryCallback.onFailedRequest(event,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //do nothing
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
//        System.out.println("loadbalanceRule = [" + loadbalanceRule + "], record = [" + record + "], e = [" + event + "], failedDeliveryCallback = [" + failedDeliveryCallback + "]");
        try {
            Response response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            return false;
        }finally {
            //单例 不关闭
        }
    }

    @Override
    public <T,E> void request(Rule loadbalanceRule, LogRecord<T> record, E e, FailedRequestCallback<E> failedDeliveryCallback) {

    }

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
            if (value == null) { // 过滤空的key
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



    private static class SingletonRequest {
        /* private */
        static Request instance = new OkHttpRequest();
    }
    public static Request getInstance() {
        // 外围类能直接访问内部类（不管是否是静态的）的私有变量
        return SingletonRequest.instance;
    }
}
