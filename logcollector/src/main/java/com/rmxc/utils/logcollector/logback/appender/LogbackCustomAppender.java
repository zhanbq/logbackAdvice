package com.rmxc.utils.logcollector.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.rmxc.utils.logcollector.logback.config.LogbackCustomAppenderConfig;
import com.rmxc.utils.logcollector.request.FailedRequestCallback;
import com.rmxc.utils.logcollector.request.LogRecord;
import sun.misc.BASE64Encoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogbackCustomAppender<E> extends LogbackCustomAppenderConfig<E> {

    private final ConcurrentLinkedQueue<E> queue = new ConcurrentLinkedQueue<E>();
    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();
    /**
     * 日志异步发送请求 异常回调
     */
    private final FailedRequestCallback<E> failedDeliveryCallback = new FailedRequestCallback<E>() {
        @Override
        public void onFailedRequest(E evt, Throwable throwable) {
            aai.appendLoopOnAppenders(evt);
        }
    };

    @Override
    protected void append(E e) {
        final byte[] payload = encoder.encode(e);
        final Long timestamp = isAppendTimestamp() ? getTimestamp(e) : null;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
//        String ttp = simpleDateFormat.format(timestamp);
//        String curr = simpleDateFormat.format(System.currentTimeMillis());
//        System.out.println("ttp === "+ttp+" || curr === "+curr);

        LogRecord<byte[]> record = new LogRecord<>(payload,timestamp,serverName, appid, securityKey,((LoggingEvent) e).getLevel().levelStr);
        if (request != null) {
            request.request(loadbalanceRule,payload,record, e,logSavePath, failedDeliveryCallback);
        } else {
            failedDeliveryCallback.onFailedRequest(e, null);
        }
    }

    @Override
    public void addAppender(Appender newAppender) {
        aai.addAppender(newAppender);
    }

    @Override
    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    @Override
    public Appender getAppender(String name) {
        return aai.getAppender(name);
    }

    @Override
    public boolean isAttached(Appender appender) {
        return aai.isAttached(appender);
    }

    @Override
    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    @Override
    public boolean detachAppender(Appender appender) {
        return aai.detachAppender(appender);
    }

    @Override
    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }

    // drains queue events to super
    private void ensureDeferredAppends() {
        E event;

        while ((event = queue.poll()) != null) {
            super.doAppend(event);
        }
    }

    @Override
    public void start(){
        // only error free appenders should be activated
        if (!checkPrerequisites()) {
            return;
        }
//        System.out.println("TimeZone === before"+TimeZone.getDefault()); //输出当前默认时区
//        final TimeZone zone = TimeZone.getTimeZone("GMT+8"); //获取中国时区
//        TimeZone.setDefault(zone); //设置时区
//        System.out.println("TimeZone === after"+TimeZone.getDefault()); //输出验证


        if(!regist(serverName,indexRegistPath)){
            addError("索引创建失败 create index failed");
            return;
        }
        super.start();
    }
    public boolean regist(String serverName,String requestPath){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String month = simpleDateFormat.format(new Date());
        String index = serverName.toLowerCase() +"-"+ month;
        String alias = serverName.toLowerCase();
        return request.registIndex(loadbalanceRule,requestPath,index,alias);
    }
    @Override
    public void stop() {
        super.stop();
    }
}
