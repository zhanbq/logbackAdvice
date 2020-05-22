package com.rmxc.logconsumer.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class LogBean {

    public String logTimestamp;

    public String serverName;

    public String content;

    public String logLevel;
}
