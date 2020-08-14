package com.rmxc.utils.logcollector.monitor;

import com.rmxc.utils.logcollector.enumeration.LogCollectorDefaultConfig;
import com.rmxc.utils.logcollector.loadbalancer.LogServer;
import com.rmxc.utils.logcollector.request.LogServerCheckRequestThreadPool;
import com.rmxc.utils.logcollector.request.OkHttpRequest;
import com.rmxc.utils.logcollector.request.Request;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * @author zhanbq
 */
public abstract class BaseLogServerChecker implements ServerChecker{

    protected List<LogServer> logServers;

    public List<LogServer> getLogServers() {
        return logServers;
    }

    public void setLogServers(List<LogServer> logServers) {
        this.logServers = logServers;
    }

    /**
     * 每五秒 心跳检查
     */
    @Override
    public void isAlive() {
        ScheduledExecutorService  logServerCheckThreadPool = LogServerCheckRequestThreadPool.getInstance().getLogServerCheckThreadPool();
        Request pingRequest = OkHttpRequest.getInstance();
        logServerCheckThreadPool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){

                for (LogServer logServer : logServers) {
                    pingRequest.ping(logServer);
                }
            }
        },0, LogCollectorDefaultConfig.HEARTBEAT_DETECTION_RATE,TimeUnit.SECONDS);

    }

    @Override
    public void ping() {
        for (LogServer logServer : this.logServers) {
            this.ping(logServer);
        }
    }


    public void ping(LogServer logServer) {
        //超时应该在3钞以上
        int  timeOut =  3000 ;
        try {
            boolean status = InetAddress.getByName(logServer.getIp()).isReachable(timeOut);
            logServer.setReadyToServe(status);
            logServer.setAlive(status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
