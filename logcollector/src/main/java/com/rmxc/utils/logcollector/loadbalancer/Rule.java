package com.rmxc.utils.logcollector.loadbalancer;

public interface Rule {

    public LogServer choose(Object key);
    public LogServer choose();
    public void setLogLoadBalancer(LoadBalancer lb);

    public LoadBalancer getLogLoadBalancer();

}
