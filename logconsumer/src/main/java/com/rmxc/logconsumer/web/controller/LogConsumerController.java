package com.rmxc.logconsumer.web.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.logconsumer.bean.LogBean;
import com.rmxc.logconsumer.bean.LogRecord;
import com.rmxc.logconsumer.business.ElasticsearchBusiness;
import com.rmxc.logconsumer.enumeration.LogLevelEnum;
import com.rmxc.logconsumer.utils.DingTalkUtils;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/logs/consumer")
public class LogConsumerController extends LogConsumerBaseController{

    @Autowired
    ElasticsearchBusiness esBusiness;

    @PostMapping("/logback")
    public ApiBaseResult logback(@RequestBody LogRecord<byte[]> logRecord, HttpServletRequest request) throws IOException {
        if(null == logRecord){

            return failed();
        }
        byte[] base64EncoderEventsBytes = logRecord.getLogContent();
        String base64Encoder = new String(base64EncoderEventsBytes,"UTF-8");
        log.info("日志输入:serverName:{}\n content:{}",logRecord.getServerName(),base64Encoder);

        SimpleDateFormat ymFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat hsmFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm.SSS");
        String mounth = ymFormat.format(new Date(logRecord.getTimestamp()));
        String hms = hsmFormat.format(logRecord.getTimestamp());


        String index = logRecord.getServerName()+"-"+mounth;
        index = index.toLowerCase();
        String alias = logRecord.getServerName();
        alias=alias.toLowerCase();
        boolean existIndex = esBusiness.isExistIndex(index);
        if(!existIndex){
            return failed();
        }
        LogBean logBean = new LogBean();
        logBean.setServerName(logRecord.getServerName());
        logBean.setContent(base64Encoder);
        logBean.setLogTimestamp(logRecord.getTimestamp());
        logBean.setLogLevel(logRecord.getLogLevel());
        logBean.setLogTime(hms);
        IndexResponse indexResponse = esBusiness.addDocument(index, logBean);
        if(logRecord.getLogLevel().equalsIgnoreCase(Level.ERROR.levelStr)){
//            try {
//                DingTalkUtils.sendMessage(logRecord.getAppid(),logRecord.getServerName(),indexResponse.getIndex(),indexResponse.getId(),base64Encoder);
//            } catch (NoSuchAlgorithmException e) {
//                log.debug("发送钉钉失败 NoSuchAlgorithmException ",e);
//            } catch (InvalidKeyException e) {
//                log.debug("发送钉钉失败 InvalidKeyException ",e);
//            }
        }
//        log.debug(JSON.toJSONString(indexResponse));
        return success();
    }
}
