package com.rmxc.utils.logcollector.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LogRecord<T> implements Serializable {


    private final T value;

    private final Long timestamp;

    private final String appid;

    private final String securityKey;

    public LogRecord(T value, Long timestamp, String appid, String securityKey) {
        this.timestamp = timestamp;
        this.appid = appid;
        this.securityKey = securityKey;
        this.value = value;
    }
    public Map<String, String> buildParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("timestamp", String.valueOf(this.timestamp));
        params.put("appid", this.appid);
        params.put("securityKey", this.securityKey);
        return params;
    }

    public T getValue() {
        return value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getAppid() {
        return appid;
    }

    public String getSecurityKey() {
        return securityKey;
    }
}
