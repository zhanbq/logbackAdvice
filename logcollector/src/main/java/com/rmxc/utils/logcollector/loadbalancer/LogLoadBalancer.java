package com.rmxc.utils.logcollector.loadbalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogLoadBalancer implements LoadBalancer {

    List<LogServer> logServers;

    private LogLoadBalancer() {

    }

    @Override
    public List<LogServer> getReachableServers() {
        return logServers;
    }

    @Override
    public List<LogServer> getAllServers() {
        return logServers;
    }


    public void setLogServers(String hosts, String path) {
        String http = "http://";
        if (null == hosts || "".equals(hosts)) {
            logServers = new ArrayList<>();
            return;
        }
        String[] hostArray = hosts.split(",");
        logServers = new ArrayList<>(hostArray.length);
        if(path==null || path ==""){
            for (String host : hostArray) {
                LogServer logServer = new LogServer();
                logServer.setHost(http + host);
                logServers.add(logServer);
            }
        }else{

            for (String host : hostArray) {
                LogServer logServer = new LogServer();
                logServer.setHost(http + host + path);
                logServers.add(logServer);
            }
        }
    }

    private static class InnerSingletion {

        private static LogLoadBalancer instance = new LogLoadBalancer();

    }

    public static LogLoadBalancer getSingletonLB(String hosts, String path) {
        InnerSingletion.instance.setLogServers(hosts, path);
        return InnerSingletion.instance;
    }
    public static LogLoadBalancer getSingletonLB(String hosts) {
        InnerSingletion.instance.setLogServers(hosts,null);
        return InnerSingletion.instance;
    }
}
