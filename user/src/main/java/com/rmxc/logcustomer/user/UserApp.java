package com.rmxc.logcustomer.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.logcustomer.enumeration.LogLevelEnum;
import com.rmxc.logcustomer.utils.DingTalkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@RestController
@Slf4j
public class UserApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(UserApp.class).run(args);

        log.info("test2222222");
    }

    @PostMapping("/testlog")
    public String testPost(@RequestBody JSONObject json, HttpServletRequest request) throws IOException {
        if(null == json){
            return "no message";
        }
        log.info("json:{}",JSON.toJSONString(json));
        byte[] base64EncoderEventsBytes = json.getBytes("value");
        String base64Encoder = new String(base64EncoderEventsBytes);
        String appid = json.getString("appid");
        log.info("base64Encoder:{}",base64Encoder);
        if(base64Encoder.contains(LogLevelEnum.ERROR)){
            try {
                DingTalkUtils.sendMessage(appid,appid,base64Encoder);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return JSON.toJSONString(json);
    }


}
