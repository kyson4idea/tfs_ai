package com.smy.tfs.biz.service;


import com.smy.framework.base.IPage;
import com.smy.tfs.api.dbo.ESTicketData;
import com.smy.tfs.api.dto.BusiTicketDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.common.core.page.TableDataInfo;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.util.List;
import java.util.Map;

public interface TicketDataESService {

    public TableDataInfo<FuzzyQueryRspDto> fuzzyQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount);

    public Response<Long> fuzzyQueryCount(String index, QueryBuilder queryBuilder);

    public void create(String index, ESTicketData entity);

    public void update(String index, ESTicketData entity);

    public void delete(String index, String id);

    public ESTicketData getById(String index, String id);

    Long count(String index, QueryBuilder queryBuilder);

    Map<String, Long> getTermsAggBucketsToMap(String index, String aggName, Integer size, QueryBuilder queryBuilder, TermsAggregationBuilder aggregationBuilder);

    /**
     *
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount 是否需要统计总数
     * @return
     */
    public TableDataInfo<SuperAdminQueryRspDto> superAdminQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount);

    /**
     *
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount 是否需要统计总数
     * @return
     */
    public IPage<BusiAdminQueryRspDto> busiAdminQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount);


    public IPage<OwnQueryRspDto> ownQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount);

    /**
     *
     * @param index
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount
     * @param sorts
     * @param orders
     * @return
     */
    public IPage<BusiQueryRspDto> busiQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders);

    /**
     *
     * @param index
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount
     * @param sorts
     * @param orders
     * @return
     */
    public IPage<SlaTagsQueryRspDto> slaTagsQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders);

    /**
     *
     * @param index
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount
     * @param sorts
     * @param orders
     * @return
     */
    public IPage<BusiCommonESQueryRspDto> busiCommonESQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders);


    /**
     *
     * @param index
     * @param id
     * @param json
     */
    public void update(String index, String id, String json);

    /**
     *
     * 给催收业务用
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @param needCount 是否需要统计总数
     * @return
     */
    public IPage<BusiTicketDataDto> ncsESQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount);



}
