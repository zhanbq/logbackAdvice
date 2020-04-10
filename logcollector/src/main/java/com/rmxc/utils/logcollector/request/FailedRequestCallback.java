package com.rmxc.utils.logcollector.request;

public interface FailedRequestCallback<E> {
    void onFailedRequest(E evt, Throwable throwable);
}
