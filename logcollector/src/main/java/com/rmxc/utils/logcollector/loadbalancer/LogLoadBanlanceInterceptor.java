package com.rmxc.utils.logcollector.loadbalancer;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class LogLoadBanlanceInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = new Request.Builder()
                .url("")
                .build();
        chain.proceed(request);

        return null;
    }
}
