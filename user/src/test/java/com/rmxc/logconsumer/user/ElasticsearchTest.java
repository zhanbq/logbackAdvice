package com.rmxc.logconsumer.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rmxc.logconsumer.business.ElasticsearchBusiness;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@Slf4j
public class ElasticsearchTest {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Autowired
    private ElasticsearchBusiness elasticsearchBusiness;

    @Test
    void testCreateIndex() throws IOException {
        // 1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("rmxc_index");
        // 2、客户端执行请求 IndicesClient,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    @Test
    void addAlias() throws IOException {
        AcknowledgedResponse acknowledgedResponse = elasticsearchBusiness.addAlias("rmxc_index", "zzz");
        log.info(JSON.toJSONString(acknowledgedResponse));
    }

    @Test
    void addAoc() throws IOException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","wanghuanjie");
        jsonObject.put("age",44);
        jsonObject.put("sex",2);
        IndexResponse indexResponse = elasticsearchBusiness.addDocument("rmxc_index", jsonObject);
        log.info(JSON.toJSONString(indexResponse));
    }

}
