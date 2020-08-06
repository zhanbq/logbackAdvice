package com.rmxc.logconsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ElasticSearchConfig {

    @Value("${es.host}")
    String esHost;
    @Value("${es.port}")
    Integer esPort;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        log.info("es restHighLevelClient init host:{}, port:{}",esHost,esPort);
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(esHost, esPort, "http")
        );
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        return restHighLevelClient;
    }

//    @Bean
//    public RestClient restClient(){
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200, "http")).build();
//        return restClient;
//    }

}
