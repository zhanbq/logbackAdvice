package com.rmxc.utils.logcollector.loadbalancer;

import java.util.ArrayList;
import java.util.List;

/**
 * 负载 单例
 * @author Administrator
 */
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


    private void setLogServers(String hosts, String path) {
        String protocol = "http://";
        if (null == hosts || "".equals(hosts)) {
            logServers = new ArrayList<>();
            return;
        }
        String[] hostArray = hosts.split(",");
        logServers = new ArrayList<>(hostArray.length);
        if(path==null || path ==""){
            for (String host : hostArray) {
                LogServer logServer = new LogServer();
                logServer.setHost(protocol + host);
                String[] ipAndPort = host.split(":");
                if(ipAndPort != null || ipAndPort.length > 0){
                    logServer.setIp(ipAndPort[0]);
                    logServer.setPort(ipAndPort[1]);
                }

                logServers.add(logServer);
            }
        }else{

            for (String host : hostArray) {
                LogServer logServer = new LogServer();
                logServer.setHost(protocol + host + path);
                String[] ipAndPort = host.split(":");
                if(ipAndPort != null || ipAndPort.length > 0){
                    logServer.setIp(ipAndPort[0]);
                    logServer.setPort(ipAndPort[1]);
                }
                logServers.add(logServer);
            }
        }
    }

    private static class InnerSingleton {

        private static LogLoadBalancer instance = new LogLoadBalancer();

    }

    public static LogLoadBalancer getSingletonLB(String hosts, String path) {
        InnerSingleton.instance.setLogServers(hosts, path);
        return InnerSingleton.instance;
    }
    public static LogLoadBalancer getSingletonLB(String hosts) {
        List<LogServer> allServers = InnerSingleton.instance.getAllServers();
        if(allServers!=null && allServers.size()!=0){
            return InnerSingleton.instance;
        }
        InnerSingleton.instance.setLogServers(hosts,null);
        return InnerSingleton.instance;
    }
    public static LogLoadBalancer getSingletonLB() {
        return InnerSingleton.instance;
    }
}
