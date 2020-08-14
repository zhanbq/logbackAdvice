package com.rmxc.utils.logcollector.loadbalancer;

/**
 * @author zhanbq
 */
public abstract class AbstractLoadBalancerRule implements Rule{

    private LoadBalancer logLoadBalancer;

    @Override
    public LoadBalancer getLogLoadBalancer() {
        return logLoadBalancer;
    }

    @Override
    public void setLogLoadBalancer(LoadBalancer logLoadBalancer) {
        this.logLoadBalancer = logLoadBalancer;
    }
}
