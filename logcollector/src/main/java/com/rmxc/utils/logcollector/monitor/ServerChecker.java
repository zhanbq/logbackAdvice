package com.rmxc.utils.logcollector.monitor;

import com.rmxc.utils.logcollector.loadbalancer.LogServer;

import java.util.List;

/**
 * @author zhanbq
 */
public interface ServerChecker {

    /**
     * ping 服务是否可用

     * @return
     */
    public void ping();


    /**
     * ping 心跳检测
     * @return
     */
    public void isAlive();

}
