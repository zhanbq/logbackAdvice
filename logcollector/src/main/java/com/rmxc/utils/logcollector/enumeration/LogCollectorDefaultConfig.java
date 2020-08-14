package com.rmxc.utils.logcollector.enumeration;

import okhttp3.OkHttpClient;

/**
 * @author Administrator
 */
public interface LogCollectorDefaultConfig {

    public static final String HEARTBEAT_DETECTION_PATH = "/serv/ping";

    public static final Integer HEARTBEAT_DETECTION_RATE = 5;

}
