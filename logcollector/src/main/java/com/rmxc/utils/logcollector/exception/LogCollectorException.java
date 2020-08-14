package com.rmxc.utils.logcollector.exception;

/**
 * @author zhanbq
 */
public class LogCollectorException extends BaseLogCollectorException{


    public LogCollectorException() {
    }

    public LogCollectorException(String message) {
        super(message);
    }

    public LogCollectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogCollectorException(Throwable cause) {
        super(cause);
    }

    public LogCollectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
