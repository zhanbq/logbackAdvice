package com.rmxc.utils.logcollector.request;

import com.rmxc.utils.logcollector.loadbalancer.LogServer;
import com.rmxc.utils.logcollector.loadbalancer.Rule;
import com.rmxc.utils.logcollector.request.bean.LogRecord;

public interface Request {


    <T,E> void request(Rule loadbalanceRule, LogRecord<T> record, E e, FailedRequestCallback<E> failedDeliveryCallback);

    <T,E> void request(Rule loadbalanceRule, byte[] payload, LogRecord<T> record, E e, String requestPath, FailedRequestCallback<E> failedDeliveryCallback);

    <E> Boolean  registIndex(Rule loadbalanceRule,String requestPath, String index, String alias);

    public Boolean ping(LogServer logServer);

}
