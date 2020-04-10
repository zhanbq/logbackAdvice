package com.rmxc.utils.logcollector.loadbalancer;

public class LogServer {

    private String host;



    public boolean isReadyToServe() {
        return true;
    }

    public boolean isAlive() {
        return true;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
