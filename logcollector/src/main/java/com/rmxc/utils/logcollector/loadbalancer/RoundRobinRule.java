package com.rmxc.utils.logcollector.loadbalancer;

import com.rmxc.utils.logcollector.exception.BaseLogCollectorException;
import com.rmxc.utils.logcollector.exception.LogCollectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhanbq
 */
public class RoundRobinRule extends AbstractLoadBalancerRule{
    private static Logger log = LoggerFactory.getLogger(RoundRobinRule.class);
    private AtomicInteger nextServerCyclicCounter;

    public RoundRobinRule() {
        nextServerCyclicCounter = new AtomicInteger(0);
    }

    public LogServer choose(LoadBalancer lb) {
        if (lb == null) {
            log.warn("no load balancer");
            return null;
        }

        LogServer server = null;
        int count = 0;

        //尝试10次,还是拿不到server就结束
        while (server == null && count++ < 10) {
            List<LogServer> reachableServers = lb.getReachableServers();
            List<LogServer> allServers = lb.getAllServers();
            int upCount = reachableServers.size();
            int serverCount = allServers.size();

            if ((upCount == 0) || (serverCount == 0)) {
                log.warn("No up servers available from load balancer: " + lb);
                return null;
            }

            int nextServerIndex = incrementAndGetModulo(serverCount);
            server = allServers.get(nextServerIndex);

            if (server == null) {
                /* Transient. */
                Thread.yield();
                continue;
            }

            if (server.isAlive() && (server.isReadyToServe())) {
                return (server);
            }

            // Next.
            server = null;
        }

        if (count >= 10) {
            //没有找到可用服务
            log.warn("No available alive servers after 10 tries from load balancer: "
                    + lb);
        }
        //返回null
        if(server == null){
            throw new LogCollectorException("all server is not ready to serve");
        }
        return server ;
    }
    /**
     * Inspired by the implementation of {@link AtomicInteger#incrementAndGet()}.
     *
     * @param modulo The modulo to bound the value of the counter.
     * @return The next value.
     */
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextServerCyclicCounter.get();
            int next = (current + 1) % modulo;
            if (nextServerCyclicCounter.compareAndSet(current, next))
                return next;
        }
    }
    @Override
    public LogServer choose(Object key) {
        return choose(getLogLoadBalancer());
    }

    @Override
    public LogServer choose() {
        if(null == getLogLoadBalancer()){
            return null;
        }
        return choose(getLogLoadBalancer());
    }
}
