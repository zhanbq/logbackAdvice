package com.rmxc.utils.logcollector.monitor;

import com.rmxc.utils.logcollector.loadbalancer.LogServer;
import sun.rmi.runtime.Log;

import java.util.List;

/**
 * @author zhanbq
 */
public class LogServerChecker4Logback extends BaseLogServerChecker{

    public LogServerChecker4Logback(List<LogServer> logServers) {
        super.logServers = logServers;
    }
}
