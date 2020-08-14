package com.rmxc.utils.logcollector.request;

import com.rmxc.utils.logcollector.enumeration.RequestStrategyEnum;

/**
 * 客户端单例  静态内部类是最好的单例模式,不建议doublecheck实现
 * @author zhanbq
 */
public class RequestFactory {

    public static Request getSingletonInstance(String requestType){

        if(isEmpty(requestType)){
            return OkHttpRequest.getInstance();
        }
        if(RequestStrategyEnum.OK_HTTP_3.equalsIgnoreCase(requestType)){
            return OkHttpRequest.getInstance();
        }
        return OkHttpRequest.getInstance();
    }

    private static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
    private static boolean notEmpty(Object str) {
        return str != null && !"".equals(str);
    }
}
