package com.rmxc.logconsumer.bean;

import ch.qos.logback.classic.Level;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class LogRecord<T> implements Serializable {


    private T logContent;

    private Long timestamp;

    private String serverName;
    private String appid;

    private String securityKey;

    private String logLevel;

}
