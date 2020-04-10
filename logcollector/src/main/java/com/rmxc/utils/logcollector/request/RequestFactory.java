package com.rmxc.utils.logcollector.request;

import com.rmxc.utils.logcollector.enumeration.RequestStrategyEnum;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

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

    private static boolean isEmpty(@Nullable Object str) {
        return str == null || "".equals(str);
    }
    private static boolean notEmpty(@Nullable Object str) {
        return str != null && !"".equals(str);
    }
}
