package com.rmxc.logconsumer.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DingTalkUtils {
    private static OkHttpClient client = new OkHttpClient();
    private static String DingTalkUrl = "https://oapi.dingtalk.com/robot/send?access_token=ddc4b0b357b874bd4fd9d52a50d8adc9836f655948ccc94a7d4b29352ab25061";

//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//        boolean dsa = sendToDingDing("系统报警", "alarm : 系统报警测试,请忽略!", "https://oapi.dingtalk.com/robot/send?access_token=ddc4b0b357b874bd4fd9d52a50d8adc9836f655948ccc94a7d4b29352ab25061");
//    }

    /**
     * 发送钉钉消息
     *
     * @param title   标题
     * @param message 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return
     */
    private static boolean sendToDingDing(String title, String message, String webhook) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
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
            Request request = new Request.Builder().url(webhook+getSign())
                    .addHeader("Content-Type", type)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String string = response.body().string();
            System.out.println(String.format("send ding message:%s", string));
            //logger.info("send ding message:{}", string);
            JSONObject res = JSONObject.parseObject(string);
            return res.getIntValue("errcode") == 0;
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error("发送钉钉消息错误！ ", e);
            return false;
        }


    }
    private static String getSign() throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Long timestamp = System.currentTimeMillis();
        String secret = "SEC620717e7dba35f987b4f1e9886eb8a720c6bb1d3343b2c43bddaff369896d29d";

        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");

        System.out.println(sign);
        String res = "&timestamp="+timestamp+"&sign="+sign;
        return res;
    }

    private static String buildMessage(String key,String appid,String message){
        return "alarm :["+key+"] 服务告警 \r\n"
                +"[ 服务名 ] : " +appid+ "\r\n"
                +message;
    }

    public static void sendMessage(String key,String appid,String message) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        sendToDingDing(key, buildMessage(key,appid,message), DingTalkUrl);
        return ;
    }
}