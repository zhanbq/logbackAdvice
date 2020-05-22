package com.rmxc.logconsumer.business;

import com.alibaba.fastjson.JSON;
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
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ElasticsearchBusiness {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

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
        // 2、客户端执行请求 IndicesClient,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse;
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
        indexRequest.source(JSON.toJSONString(data),XContentType.JSON);
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

    public <T> UpdateResponse update(String index,String id, T data) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(JSON.toJSONString(data),XContentType.JSON);
        UpdateResponse response = this.client.update(updateRequest, RequestOptions.DEFAULT);
        return response;
    }

}
