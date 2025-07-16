package com.smy.tfs.biz.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.smy.framework.base.IPage;
import com.smy.framework.core.exception.ServiceException;
import com.smy.framework.core.page.PageHelper;
import com.smy.framework.elasticsearch.factory.ClusterEsClientFactory;
import com.smy.tfs.api.dbo.TicketFormItemData;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;

@Slf4j
public class TfsESClient {
    private SearchResponse queryList(String index, QueryBuilder queryBuilder, List<String> sorts, List<String> orders, int from, int size, String[] fields, TimeValue timeout) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.size(10000);
        if (fields != null && fields.length > 0) {
            searchSourceBuilder.fetchSource(fields, Strings.EMPTY_ARRAY);
        }

        if (from > 0) {
            searchSourceBuilder.from(from);
        }

        if (size > 0) {
            searchSourceBuilder.size(size);
        }

        if (sorts != null && orders != null && sorts.size() > 0 && sorts.size() == orders.size()) {
            for(int i = 0; i <= sorts.size() - 1; ++i) {
                String order = orders.get(i);
                if ("desc".equalsIgnoreCase(order)) {
                    searchSourceBuilder.sort(sorts.get(i), SortOrder.DESC);
                } else if ("asc".equalsIgnoreCase(order)) {
                    searchSourceBuilder.sort(sorts.get(i), SortOrder.ASC);
                }
            }
        }
        if (timeout != null) {
            searchSourceBuilder.timeout(timeout);
        }
        log.info("分页查询searchSourceBuilder:{}",searchSourceBuilder);
        return this.queryList(index, searchSourceBuilder);
    }
    SearchResponse queryList(String index, SearchSourceBuilder searchSourceBuilder) throws IOException {
        RestHighLevelClient client = ClusterEsClientFactory.getClient(index);
        SearchRequest searchRequest = new SearchRequest(new String[]{index});
        searchRequest.source(searchSourceBuilder);
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    public  IPage<Map<String,Object>> pageQueryAndCount(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, List<String> sorts, List<String> orders) throws IOException {
        Long count = this.count(index, queryBuilder);
        PageHelper<Map<String,Object>> pageHelper = new PageHelper(pageNo, pageSize, count.intValue());
        pageNo = pageNo <= 0 ? 1 : pageNo;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        int from = (pageNo - 1) * pageSize;
        long start = System.currentTimeMillis();
        SearchResponse response = this.queryList(index, queryBuilder, sorts, orders, from, pageSize, null, null);
        long end = System.currentTimeMillis();
        log.info("es查询耗时 {}ms, index: {}", end - start, index);
        List<Map<String,Object>> results = new ArrayList<>();
        if (Objects.nonNull(response) && Objects.nonNull(response.getHits())) {
            SearchHit[] searchHits = response.getHits().getHits();
            results = this.buildMapList(index, searchHits);
        }
        return pageHelper.buildPageEntity(results);
    }

    /**
     * 根据条件计算数量
     *
     * @author liuchsh
     * @date 2021/10/22 15:51
     */
    private Long count(String index, QueryBuilder queryBuilder) throws IOException {
        RestHighLevelClient client = ClusterEsClientFactory.getClient(index);
        CountRequest countRequest = new CountRequest(index);
        countRequest.query(queryBuilder);
        CountResponse response = client.count(countRequest, RequestOptions.DEFAULT);
        return response.getCount();
    }

    public  IPage<Map<String,Object>> pageQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, List<String> sorts, List<String> orders) throws IOException {
        PageHelper<Map<String,Object>> pageHelper = new PageHelper(pageNo, pageSize, 0);
        pageNo = pageNo <= 0 ? 1 : pageNo;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        int from = (pageNo - 1) * pageSize;
        long start = System.currentTimeMillis();
        SearchResponse response = this.queryList(index, queryBuilder, sorts, orders, from, pageSize, null, null);
        long end = System.currentTimeMillis();
        log.info("es查询耗时 {}ms, index: {}", end - start, index);
        List<Map<String,Object>> results = new ArrayList<>();
        if (Objects.nonNull(response) && Objects.nonNull(response.getHits())) {
            SearchHit[] searchHits = response.getHits().getHits();
            results = this.buildMapList(index, searchHits);
        }
        return pageHelper.buildPageEntity(results);
    }

    private List<Map<String,Object>> buildMapList(String index, SearchHit[] searchHits) {
        long start = System.currentTimeMillis();
        List<Map<String,Object>> results = new ArrayList();
        try {
            if (searchHits != null && searchHits.length > 0) {
                int length = searchHits.length;
                for(int i = 0; i < length; ++i) {
                    SearchHit searchHit = searchHits[i];
                    Map<String, Object> result;
                    if (searchHit.getSourceAsMap() != null) {
                        result = searchHit.getSourceAsMap();
                    } else {
                        result = new HashMap();
                        Iterator iterator = searchHit.getFields().values().iterator();
                        while(iterator.hasNext()) {
                            DocumentField internalSearchHitField = (DocumentField)iterator.next();
                            result.put(internalSearchHitField.getName(), internalSearchHitField.getValue());
                        }
                    }
                    results.add(result);
                }
            }
        } catch (Exception e) {
            log.error("构建对象异常:{}", e);
        }
        long end = System.currentTimeMillis();
        log.info("构建对象耗时 {}ms, index: {}, result.size: {}", new Object[]{end - start, index, results.size()});
        return results;
    }

    public void update(String index, String id, String json) {
        log.info("update: index={}, id={}, source={}", index, id, json);
        try {
            RestHighLevelClient client = ClusterEsClientFactory.getClient(index);
            UpdateRequest updateRequest = new UpdateRequest(index, id);
            updateRequest.doc(json, XContentType.JSON);
            client.update(updateRequest, RequestOptions.DEFAULT);
        } catch (Exception ex) {
            log.error("update error: index={}, id={}", new Object[]{index, id, ex});
            throw new ServiceException("es更新异常", ex.getMessage());
        }

    }


}
