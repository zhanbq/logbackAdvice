package com.rmxc.logconsumer.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


@Slf4j
public class DingTalkUtils {
    private static OkHttpClient client = new OkHttpClient();
    private static RestTemplate restTemplate = new RestTemplate();
    private static String DingTalkUrl = "https://oapi.dingtalk.com/robot/send?access_token=ddc4b0b357b874bd4fd9d52a50d8adc9836f655948ccc94a7d4b29352ab25061";
    private static final String lineSeparator = System.getProperty("line.separator", "\n");

    private static final String buildShortenUrl = "http://47.92.198.89:4601/api/shorten_url";
    private static final String getIndexPatternUrl = "http://47.92.198.89:4601/api/saved_objects/_find?fields=title&fields=type&per_page=10000&type=index-pattern";
    /**
     * 发送钉钉消息
     *
     * @param title   标题
     * @param message 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return
     */
    private static boolean sendMarkdownToDingDing(String title, String message, String webhook) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Response response = null;
        try {
            JSONObject text = new JSONObject();
            text.put("text", message);
            text.put("title", title);

            JSONObject json = new JSONObject();
            json.put("msgtype", "markdown");
            json.put("markdown", text);
            String jsonString = json.toJSONString();

            String type = "application/json; charset=utf-8";
            RequestBody body = RequestBody.create(MediaType.parse(type), jsonString);
            Request request = new Request.Builder().url(webhook + getSign())
                    .addHeader("Content-Type", type)
                    .post(body)
                    .build();

            response = client.newCall(request).execute();
            String string = response.body().string();
            System.out.println(String.format("send ding message:%s", string));
            //logger.info("send ding message:{}", string);
            JSONObject res = JSONObject.parseObject(string);
            return res.getIntValue("errcode") == 0;
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("发送钉钉消息错误！ ", e);
            return false;
        }finally {
            if(null != response){

                response.body().close();
            }
        }


    }

    private static String getSign() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Long timestamp = System.currentTimeMillis();
        String secret = "SEC620717e7dba35f987b4f1e9886eb8a720c6bb1d3343b2c43bddaff369896d29d";

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");

        System.out.println(sign);
        String res = "&timestamp=" + timestamp + "&sign=" + sign;
        return res;
    }

    private static String buildMessage(String key, String serverName, String message) {

        String[] logs = message.split(lineSeparator);
        if (logs.length > 3) {
            message = logs[0] + lineSeparator + logs[1] + lineSeparator + logs[2];
        }
        return "alarm 服务:[" + key + "] 服务告警 \r\n"
                + " appid [ : " + serverName + "]\r\n"
                + message;
    }

    private static String buildMessage(String appid, String serverName, String docId, String kibanaUrl, String message) {

        String[] logs = message.split(lineSeparator);
        if (logs.length > 3) {
            message = logs[0] + lineSeparator + logs[1] + lineSeparator + logs[2];
        }
        if(StringUtils.isEmpty(kibanaUrl)){
            return "#### alarm 服务名:[" + serverName + "] 告警 \n"
                    + "> appid: [" + appid + "]\n"
                    + "> logId:[" + docId + "]\n"
                    + "> content : "+ message + "\n";
        }
        return "#### alarm 服务名:[" + serverName + "] 告警 \n"
                + "> appid: [" + appid + "]\n"
                + "> logId:[" + docId + "]\n"
                + "> content : "+ message + "\n"
                + "[点击详情]("+kibanaUrl+")\n";
    }

    public static String buildKibanaUrl(RestTemplate restTemplate, String index, String docId) {
        ResponseEntity<JSONObject> responseEntity = restTemplate.getForEntity(getIndexPatternUrl, JSONObject.class);
        JSONObject indexPatterns = responseEntity.getBody();
        if (CollectionUtils.isEmpty(indexPatterns)) {
            return "";
        }
        JSONArray savedObjects = indexPatterns.getJSONArray("saved_objects");
        String kibanaLogDetailUrL = "";
        String kibanaLogDetailUrI = "";
        String kibanaHost = "http://47.92.198.89:4601";
        for (Object savedObject : savedObjects) {
            JSONObject jsonObject = JSONObject.toJavaObject((JSONObject) savedObject, JSONObject.class);
            String indexTitle = jsonObject.getJSONObject("attributes").getString("title");
            String indexPatternId = jsonObject.getString("id");
            if (index.contains(indexTitle)) {
                kibanaLogDetailUrI = "/app/kibana#/discover?_g=()&_a=(columns%3a!(_source),index%3a'" + indexPatternId + "',interval%3aauto,query%3a(language:kuery,query%3a'_id : " + docId + "'),sort:!(!(_score,desc)))";
            }
        }
        if(StringUtils.isEmpty(kibanaLogDetailUrI)){
            return kibanaLogDetailUrL;
        }
        JSONObject requestParam = new JSONObject();
        requestParam.put("url",kibanaLogDetailUrI);
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        ArrayList<org.springframework.http.MediaType> accpets = new ArrayList<>();
        accpets.add(org.springframework.http.MediaType.ALL);
        headers.setAccept(accpets);
        headers.set("kbn-version","7.6.2");
        //将请求头部和参数合成一个请求
//        HttpEntity<JSONObject> requestEntity = new HttpEntity<>(requestParam, headers);
//        ResponseEntity<JSONObject> shortenUrlRes = restTemplate.exchange(buildShortenUrl,method,requestEntity,JSONObject.class);
//        JSONObject shortenUrlBody = shortenUrlRes.getBody();
//        String urlId = shortenUrlBody.getString("urlId");
//        kibanaLogDetailUrI = "/goto/"+urlId;
//        kibanaLogDetailUrL = kibanaHost+kibanaLogDetailUrI;


        String type = "application/json; charset=utf-8";
        RequestBody body = RequestBody.create(MediaType.parse(type), requestParam.toJSONString());
        Request request = new Request.Builder().url(buildShortenUrl)
                .addHeader("Content-Type", type)
                .addHeader("kbn-version", "7.6.2")
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            String string = response.body().string();
            JSONObject res = JSONObject.parseObject(string);
            String urlId = res.getString("urlId");
            kibanaLogDetailUrI = "/goto/"+urlId;
            kibanaLogDetailUrL = kibanaHost+kibanaLogDetailUrI;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != response){
                response.body().close();
            }
        }


        return kibanaLogDetailUrL;
    }


    public static void sendMessage(String appid, String serverName, String message) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        sendMarkdownToDingDing(serverName, buildMessage(appid, serverName, message), DingTalkUrl);
        return;
    }

    public static void sendMessage(String appid, String serverName,String indexName, String docId, String message) throws NoSuchAlgorithmException, InvalidKeyException, IOException {

        String kibanaUrl = buildKibanaUrl(restTemplate, indexName, docId);

        sendMarkdownToDingDing(serverName, buildMessage(appid, serverName, docId,kibanaUrl, message), DingTalkUrl);
        return;
    }


}