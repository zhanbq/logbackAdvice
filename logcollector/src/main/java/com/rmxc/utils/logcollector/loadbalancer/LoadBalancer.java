package com.rmxc.utils.logcollector.loadbalancer;

import java.util.List;

/**
 *
 * @author zhanbq
 */
public interface LoadBalancer {
    List<LogServer> getReachableServers();

    List<LogServer> getAllServers();
}
