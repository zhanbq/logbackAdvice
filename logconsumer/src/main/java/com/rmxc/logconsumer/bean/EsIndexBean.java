package com.rmxc.logconsumer.bean;

import lombok.Data;

@Data
public class EsIndexBean {

    /**
     * 索引名称
     */
    String index;

    /**
     * 索引别名
     */
    String alias;
}
