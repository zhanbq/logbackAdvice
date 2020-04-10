package com.rmxc.utils.logcollector.loadbalancer;

import java.util.List;

public interface LoadBalancer {
    List<LogServer> getReachableServers();

    List<LogServer> getAllServers();
}
