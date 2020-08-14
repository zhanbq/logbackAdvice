package com.rmxc.utils.logcollector.exception;

/**
 * @author Administrator
 */
public abstract class BaseLogCollectorException extends RuntimeException{

    protected Integer code;


    public BaseLogCollectorException() {
    }

    public BaseLogCollectorException(String message) {
        super(message);
    }

    public BaseLogCollectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseLogCollectorException(Throwable cause) {
        super(cause);
    }

    public BaseLogCollectorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
