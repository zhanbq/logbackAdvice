package com.rmxc.utils.logcollector.request.bean;

import ch.qos.logback.classic.Level;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanbq
 */
public class LogRecord<T> implements Serializable {


    private final T logContent;

    private final Long timestamp;

    private final String serverName;
    private final String appid;

    private final String securityKey;

    private final String logLevel;

    public LogRecord(T logContent, Long timestamp,String serverName , String appid, String securityKey,String logLevel) {
        this.timestamp = timestamp;
        this.appid = appid;
        this.securityKey = securityKey;
        this.logContent = logContent;
        this.serverName = serverName;
        this.logLevel = logLevel;
    }
    public Map<String, String> buildParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("timestamp", String.valueOf(this.timestamp));
        params.put("appid", this.appid);
        params.put("securityKey", this.securityKey);
        return params;
    }

    public T getLogContent() {
        return logContent;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getServerName() {
        return serverName;
    }

    public String getAppid() {
        return appid;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public String getLogLevel() {
        return logLevel;
    }
}
