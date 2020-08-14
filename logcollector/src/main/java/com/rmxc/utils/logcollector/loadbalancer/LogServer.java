package com.rmxc.utils.logcollector.loadbalancer;

/**
 * @author Administrator
 */
public class LogServer {

    private String ip;

    private String port;

    private String host;



    /**
     * 服务存活判断
     */
    private volatile boolean isAliveFlag = false;
    /**
     * 服务是否可用判断
     */
    private volatile boolean readyToServe = false;

    public boolean isReadyToServe() {
        return readyToServe;
    }

    public boolean isAlive() {
        return isAliveFlag;
    }
    public void setAlive(boolean isAliveFlag) {
        this.isAliveFlag = isAliveFlag;
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public final void setReadyToServe(boolean readyToServe) {
        this.readyToServe = readyToServe;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
