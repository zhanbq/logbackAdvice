package com.rmxc.logconsumer.web.controller;

import com.rmxc.logconsumer.bean.EsIndexBean;
import com.rmxc.logconsumer.business.ElasticsearchBusiness;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiBaseResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 索引操作
 */
@Slf4j
@RestController
@RequestMapping("/logs/index")
public class EsIndexController extends LogConsumerBaseController{

    @Autowired
    ElasticsearchBusiness esBusiness;

    /**
     * 注册 索引 , 日志框架收集日志初始化前 创建所在服务的索引
     */
    @PostMapping("/regist")
    public ApiBaseResult indexRegist(@RequestBody EsIndexBean esIndexBean) throws IOException {
        if(null == esIndexBean){
            return failed();
        }
        if(StringUtils.isEmpty(esIndexBean.getIndex())){
            return success();

        }
        try {
            boolean existIndex = esBusiness.isExistIndex(esIndexBean.getIndex().toLowerCase());
            if(!existIndex){
                CreateIndexResponse index = esBusiness.createIndex(esIndexBean.getIndex(), esIndexBean.getAlias());
            }
        } catch (IOException e) {
            log.error("索引创建失败 ,{}",e);
            return failed();
        }
//        try {
//            AcknowledgedResponse acknowledgedResponse = esBusiness.putDateTypeMapping(esIndexBean.getIndex(), esIndexBean.getAlias());
//        } catch (Exception e) {
//            log.error("索引时间设置失败 ,{}",e);
//            throw e;
//        }
        return success();
    }

}
