package com.rmxc.logconsumer.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Data
public class LogBean {

    public Long logTimestamp;

    public String serverName;

    public String content;

    public String logLevel;

    public String logTime;

}
