package com.rmxc.utils.logcollector.logback.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.rmxc.utils.logcollector.loadbalancer.LogLoadBalancer;
import com.rmxc.utils.logcollector.loadbalancer.RoundRobinRule;
import com.rmxc.utils.logcollector.loadbalancer.Rule;
import com.rmxc.utils.logcollector.request.OkHttpRequest;
import com.rmxc.utils.logcollector.request.Request;
import com.rmxc.utils.logcollector.request.RequestFactory;

public abstract class LogbackCustomAppenderConfig<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

    protected boolean appendTimestamp = true;
    protected Request request;

    protected Encoder<E> encoder = null;
    protected Rule loadbalanceRule = null;

    protected String requestStrategy;
    protected String hosts;
    protected String path;
    protected String appid;
    protected String securityKey;

    protected boolean checkPrerequisites() {
        boolean errorFree = true;

        if (hosts == null) {
            addError("No hosts set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }
        if (encoder == null) {
            addError("No encoder set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }
        if (appid == null) {
            addInfo("No appid set for the appender named [\"" + name + "\"].");
        }

        if (securityKey == null) {
            addInfo("No securityKey set for the appender named [\""+name+"\"].");
        }
        if (requestStrategy == null) {
            addInfo("No requestStrategy set for the appender named [\""+name+"\"]. Using default asynchronous strategy:OkHttpRequest.");
            request = OkHttpRequest.getInstance();
        }else{
            request = RequestFactory.getSingletonInstance(requestStrategy);
        }

        if(loadbalanceRule == null){
            addInfo("No loadbalanceRule set for the appender named [\""+name+"\"]. Using default loadbalanceRule:RoundRobinRule.");
            loadbalanceRule = new RoundRobinRule();
        }
        loadbalanceRule.setLogLoadBalancer(LogLoadBalancer.getSingletonLB(hosts,path));
        return errorFree;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    protected Long getTimestamp(E e) {
        if (e instanceof ILoggingEvent) {
            return ((ILoggingEvent) e).getTimeStamp();
        } else {
            return System.currentTimeMillis();
        }
    }
    public boolean isAppendTimestamp() {
        return appendTimestamp;
    }

    public void setLoadbalanceRule(Rule loadbalanceRule) {
        this.loadbalanceRule = loadbalanceRule;
    }

    public void setRequestStrategy(String requestStrategy) {
        this.requestStrategy = requestStrategy;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
