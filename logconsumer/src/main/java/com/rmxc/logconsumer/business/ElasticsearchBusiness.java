package com.rmxc.logconsumer.business;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Cancellable;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ElasticsearchBusiness {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    @Resource
    @Qualifier("restClient")
    private RestClient restClient;


    /**
     * 为索引添加别名 ,, 通用接口不内置索引是否存在的验证,调用之前必须先验证索引是否存在
     *
     * @param index
     * @param alias
     * @throws IOException
     */
    public AcknowledgedResponse addAlias(String index, String alias) throws IOException {

        IndicesAliasesRequest request = new IndicesAliasesRequest();
        IndicesAliasesRequest.AliasActions aliasAction =
                new IndicesAliasesRequest.AliasActions(IndicesAliasesRequest.AliasActions.Type.ADD)
                        .index(index)
                        .alias(alias);
        request.addAliasAction(aliasAction);
        AcknowledgedResponse acknowledgedResponse = client.indices().updateAliases(request, RequestOptions.DEFAULT);
        return acknowledgedResponse;
    }

    /**
     * 创建索引
     *
     * @param index
     * @param alias
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String index, String alias) throws IOException {
        // 1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);
        Map<String, Object> dateType = new HashMap<>();
        dateType.put("type", "date");
        dateType.put("format","yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS");
        Map<String, Object> properties = new HashMap<>();
        properties.put("logTime", dateType);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(mapping);
        // 2、客户端执行请求 IndicesClient,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse;
    }

    public AcknowledgedResponse putDateTypeMapping(String index, String alias) throws IOException {
        HashMap<String, HashMap<String, Map<String, String>>> mappings = new HashMap<>();
        HashMap<String, Map<String, String>> properties = new HashMap<>();
        HashMap<String, String> dateTye = new HashMap<>();
        dateTye.put("type", "date");
        dateTye.put("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd||epoch_millis");
        properties.put("logTime", dateTye);
//        properties.put("logTimeStamp", dateTye);
        mappings.put("properties", properties);
//        HashMap<String, Object> setting = new HashMap<>();
//        setting.put("mappings",mappings);
        System.out.println("setting ==>" + JSON.toJSONString(mappings));
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//        builder.startObject();
//        {
//            builder.startObject("properties");
//            {
//                builder.startObject("logTime");
//                {
//                    builder.field("type", "date");
//                    builder.field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd||epoch_millis");
//                }
//                builder.endObject();
//            }
//            builder.endObject();
//        }
//        builder.endObject();
        PutMappingRequest putMappingRequest = new PutMappingRequest();
        putMappingRequest.source(mappings);

        AcknowledgedResponse acknowledgedResponse = null;
        try {
            acknowledgedResponse = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("索引设置失败,{}", e);
            throw e;
        }
        return acknowledgedResponse;
    }


    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     * @throws IOException
     */
    public boolean isExistIndex(String index) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        return exists;
    }

    /**
     * 删除索引
     *
     * @param index
     * @return
     * @throws IOException
     */
    public AcknowledgedResponse deleteIndex(String index) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse deleteResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        return deleteResponse;
    }

    /**
     * 添加文档
     *
     * @param index
     * @param data
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> IndexResponse addDocument(String index, T data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(JSON.toJSONString(data), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse;
    }

    /**
     * 新增文档异步操作
     *
     * @param index
     * @param data
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> void addDocumentAsync(String index, T data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index);
        indexRequest.source(JSON.toJSONString(data));

        Cancellable cancellable = client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    public GetResponse queryByIndexAndId(String index, String id) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse;
    }

    public GetResponse queryByIndexAndId(String index, String id, String... includesFields) throws IOException {
        GetRequest getRequest = new GetRequest(index, id);
        String[] includes = includesFields;
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext =
                new FetchSourceContext(true, includes, excludes);
        getRequest.fetchSourceContext(fetchSourceContext);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse;
    }

    public <T> UpdateResponse update(String index, String id, T data) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(JSON.toJSONString(data), XContentType.JSON);
        UpdateResponse response = this.client.update(updateRequest, RequestOptions.DEFAULT);
        return response;
    }

}
