package com.rmxc.logconsumer.signature.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Signature {
    String ORDER_SORT = "ORDER_SORT";//按照order值排序
    String ALPHA_SORT = "ALPHA_SORT";//字典序排序
    boolean resubmit() default true;//允许重复请求
    String sort() default Signature.ALPHA_SORT;
}