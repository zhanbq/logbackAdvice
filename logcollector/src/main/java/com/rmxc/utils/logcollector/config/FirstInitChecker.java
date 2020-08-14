package com.rmxc.utils.logcollector.config;

/**
 *
 * spring boot start logging 和 sl4j 会先后 初始化共两次 所有的appender
 *
 * 这个类用来检查 是否已经是 初始化过了
 *
 * @author Administrator
 */
public class FirstInitChecker {
    private static final String lock = "lock";
    private Boolean isFristInit = true;

    public void setFristInit(Boolean fristInit) {
        synchronized (lock){
            isFristInit = fristInit;
        }
    }

    public Boolean getFristInit() {
        return isFristInit;
    }

    private FirstInitChecker() {
    }

    private static class Singleton{
        static FirstInitChecker firstInitChecker = null;
        static {
            firstInitChecker = new FirstInitChecker();
        }

        public static  FirstInitChecker getInstance(){
            return firstInitChecker;
        }
    }

    public static FirstInitChecker getChecker(){
        return Singleton.getInstance();
    }
}
