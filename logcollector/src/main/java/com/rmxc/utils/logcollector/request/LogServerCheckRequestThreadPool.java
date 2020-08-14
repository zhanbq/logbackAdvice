package com.rmxc.utils.logcollector.request;

import java.util.concurrent.*;

/**
 * @author Administrator
 */
public class LogServerCheckRequestThreadPool {

    private ScheduledExecutorService  logServerCheckThreadPool = Executors.newScheduledThreadPool(10);


    public ScheduledExecutorService  getLogServerCheckThreadPool() {
        return logServerCheckThreadPool;
    }

    /**
     * 获得单例
     * @return
     */
    public static LogServerCheckRequestThreadPool getInstance(){
        return SingleTon.getInstance();
    }

    /**
     * 快速初始化
     */
    public static void init(){
        getInstance();
    }

    private static class SingleTon{
        private static LogServerCheckRequestThreadPool instance;

        static {
            instance = new LogServerCheckRequestThreadPool();
        }

        public static LogServerCheckRequestThreadPool getInstance(){
            return instance;
        }

    }

}
