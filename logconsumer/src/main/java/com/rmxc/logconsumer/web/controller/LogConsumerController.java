package com.rmxc.logconsumer.web.controller;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.logconsumer.bean.LogBean;
import com.rmxc.logconsumer.bean.LogRecord;
import com.rmxc.logconsumer.business.ElasticsearchBusiness;
import com.rmxc.logconsumer.enumeration.LogLevelEnum;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
        log.info("base64Encoder===={}",JSON.toJSONString(logRecord));


//        if(base64Encoder.contains(LogLevelEnum.ERROR)){
//            try {
//                DingTalkUtils.sendMessage(appid,appid,base64Encoder);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (InvalidKeyException e) {
//                e.printStackTrace();
//            }
//        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String format = simpleDateFormat.format(new Date(logRecord.getTimestamp()));
        String index = logRecord.getServerName()+"-"+format;
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
        logBean.setLogTimestamp(logRecord.getTimestamp().toString());
        logBean.setLogLevel(logRecord.getLogLevel());
        IndexResponse indexResponse = esBusiness.addDocument(index, logBean);
        log.info(JSON.toJSONString(indexResponse));
        return success();
    }
}
