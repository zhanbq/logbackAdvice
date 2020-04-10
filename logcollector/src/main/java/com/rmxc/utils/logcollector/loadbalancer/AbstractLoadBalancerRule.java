package com.rmxc.utils.logcollector.loadbalancer;

public abstract class AbstractLoadBalancerRule implements Rule{

    private LoadBalancer logLoadBalancer;

    public LoadBalancer getLogLoadBalancer() {
        return logLoadBalancer;
    }

    public void setLogLoadBalancer(LoadBalancer logLoadBalancer) {
        this.logLoadBalancer = logLoadBalancer;
    }
}
