package com.rmxc.utils.logcollector.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import com.rmxc.utils.logcollector.config.FirstInitChecker;
import com.rmxc.utils.logcollector.loadbalancer.LoadBalancer;
import com.rmxc.utils.logcollector.loadbalancer.LogLoadBalancer;
import com.rmxc.utils.logcollector.logback.config.AbstractLogbackCustomAppenderConfig;
import com.rmxc.utils.logcollector.monitor.LogServerChecker4Logback;
import com.rmxc.utils.logcollector.request.FailedRequestCallback;
import com.rmxc.utils.logcollector.request.LogServerCheckRequestThreadPool;
import com.rmxc.utils.logcollector.request.bean.LogRecord;
import com.taobao.arthas.agent.attach.ArthasAgent;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.slf4j.MDC;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author zhanbq
 */
public class LogbackCustomAppender<E> extends AbstractLogbackCustomAppenderConfig<E> {
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
        //获取日志打印的时间戳
        final Long timestamp = isAppendTimestamp() ? getTimestamp(e) : null;
        LogRecord<byte[]> record = new LogRecord<>(payload, timestamp, serverName, appid, securityKey, ((LoggingEvent) e).getLevel().levelStr);
        if (request != null) {
            request.request(loadbalanceRule, payload, record, e, logSavePath, failedDeliveryCallback);
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

    /**
     * drains queue events to super
     */
    private void ensureDeferredAppends() {
        E event;

        while ((event = queue.poll()) != null) {
            super.doAppend(event);
        }
    }

    @Override
    public void start() {
        // only error free appenders should be activated
        // 检查所有的配置项是否正确,如果失败 报错 阻止项目启动
        if (!checkPrerequisites()) {
            return;
        }
        if(FirstInitChecker.getChecker().getFristInit()){

            //开启日志心跳检测
            LogServerChecker4Logback logServerChecker4Logback = new LogServerChecker4Logback(LogLoadBalancer.getSingletonLB().getAllServers());
            logServerChecker4Logback.isAlive();
            logServerChecker4Logback.ping();

            //开启本地服务监控
//            webMonitoringAttach();


            FirstInitChecker.getChecker().setFristInit(false);
        }

        if (!regist(serverName, indexRegistPath)) {
            //关闭心跳检测
            addInfo("关闭心跳检测");
            LogServerCheckRequestThreadPool.getInstance().getLogServerCheckThreadPool().shutdown();
            addError("索引创建失败 create index failed,可能是服务期宕机,服务进程程shutdown,或服务内部异常");
            return;
        }

        super.start();
    }

    /**
     * 时区设置 , 用户日志打印时的时间获取 和 系统时区不一致 导致日期错乱
     * 有必要时 使用 可尝试GMT+8 或 UTC
     */
    private void correctionTimeZone() {
        /**
         * 获取中国时区
         */
        final TimeZone zone = TimeZone.getTimeZone("GMT+8");
        /**
         * 设置时区
         */
        TimeZone.setDefault(zone);
    }

    private void webMonitoringAttach(){
        Map<String, String> configMap = new HashMap<>();
        configMap.put("port","8899");
        ArthasAgent arthasAgent = new ArthasAgent(configMap,null,true, ByteBuddyAgent.install());

        arthasAgent.init();

    }


    /**
     * 向日志消费端注册索引, 首次启动会注册索引, 非首次或校验索引,不存在则自动创建索引
     * @param serverName
     * @param requestPath
     * @return
     */
    public boolean regist(String serverName, String requestPath) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String month = simpleDateFormat.format(new Date());
        String index = serverName.toLowerCase() + "-" + month;
        String alias = serverName.toLowerCase();
        return request.registIndex(loadbalanceRule, requestPath, index, alias);
    }

    @Override
    public void stop() {
        super.stop();
    }
}
