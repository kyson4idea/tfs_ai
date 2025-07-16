package com.smy.tfs.biz.service.impl;


import cn.hutool.core.util.StrUtil;
import com.smy.framework.base.IPage;
import com.smy.framework.core.page.PageHelper;
import com.smy.framework.elasticsearch.dao.ESBasicCrudSupport;
import com.smy.framework.elasticsearch.factory.ClusterEsClientFactory;
import com.smy.tfs.api.dbo.ESTicketData;
import com.smy.tfs.api.dto.BusiTicketDataDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.biz.client.TfsESClient;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.service.TicketDataESService;
import com.smy.tfs.biz.util.DateConverter;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Slf4j
public class TicketDataESServiceImpl implements TicketDataESService {

    private ESBasicCrudSupport<ESTicketData> eSBasicCrudSupport = new ESBasicCrudSupport<>(ESTicketData.class);

    private TfsESClient tfsESClient = new TfsESClient();

    @Resource
    private AccountReturnComponent accountReturnComponent;

    @Override
    public TableDataInfo<FuzzyQueryRspDto> fuzzyQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount) {
        TableDataInfo<FuzzyQueryRspDto> tableDataInfo = new TableDataInfo<>();
        List<FuzzyQueryRspDto> fuzzyQueryRspDtoList = new ArrayList<>();
        int totalRecordCount = 0;
        long start = System.currentTimeMillis();
        try {
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> iPage;
            if (needCount) {
                iPage = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            } else {
                iPage = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            }
            totalRecordCount = iPage.getTotalRecordCount();
            for (Map<String,Object> map : iPage.getData()) {
                FuzzyQueryRspDto fuzzyQueryRspDto = new FuzzyQueryRspDto(map);
                fuzzyQueryRspDto.setApplyUser(accountReturnComponent.getAccountName(fuzzyQueryRspDto.getApplyUser()));
                fuzzyQueryRspDtoList.add(fuzzyQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
            return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),String.format("分页查询异常：%s",e.getMessage()));
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            tableDataInfo.setTotal(totalRecordCount);
            tableDataInfo.setRows(fuzzyQueryRspDtoList);
            return tableDataInfo;
        }
    }

    @Override
    public Response<Long> fuzzyQueryCount(String index, QueryBuilder queryBuilder) {
        Long count = 0l;
        try {
            count = eSBasicCrudSupport.count(index, queryBuilder);
        } catch (Exception e) {
            log.error("统计总数异常：{}",e);
            return Response.error(BizResponseEnums.QUERY_ERROR,String.format("统计总数异常：%s",e.getMessage()));
        } finally {
            return Response.success(count);
        }
    }

    @Override
    public void create(String index, ESTicketData entity) {
        entity.setIndex(index);
        eSBasicCrudSupport.create(entity);
    }

    @Override
    public void update(String index, ESTicketData entity) {
        entity.setIndex(index);
        eSBasicCrudSupport.update(entity);
    }

    @Override
    public void delete(String index, String id) {
        ESTicketData entity = new ESTicketData(id);
        entity.setIndex(index);
        eSBasicCrudSupport.delete(entity);
    }

    @Override
    public ESTicketData getById(String index, String id) {
        return eSBasicCrudSupport.findResponseById(index, id);
    }

    @Override
    public Long count (String index, QueryBuilder queryBuilder) {
        Long count = 0l;
        try {
            RestHighLevelClient client = ClusterEsClientFactory.getClient(index);
            CountRequest countRequest = new CountRequest(index);
            countRequest.query(queryBuilder);
            CountResponse response = client.count(countRequest, RequestOptions.DEFAULT);
            return response.getCount();
        } catch (Exception e) {
            log.error("统计当日总数异常：{}",e);
        }
        return count;
    }

    @Override
    public Map<String, Long> getTermsAggBucketsToMap (String index, String aggName, Integer size, QueryBuilder queryBuilder, TermsAggregationBuilder aggregationBuilder) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .size(0)
                .timeout(new TimeValue(60, TimeUnit.SECONDS)) // 添加超时时间
                .query(queryBuilder)
                .aggregation(aggregationBuilder);

        RestHighLevelClient client = ClusterEsClientFactory.getClient(index);
        SearchRequest searchRequest = new SearchRequest(new String[]{index});
        searchRequest.source(searchSourceBuilder);
        log.info("searchSourceBuilder:{}", searchSourceBuilder.toString());
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            // 解析聚合结果
            Terms aggRes = response.getAggregations().get(aggName);
            log.info("聚合查询 getTermsAggBucketsToMap 返回的查询结果{}",aggRes.getBuckets());

            Map<String, Long> resultMap = aggRes.getBuckets().stream()
                    // 过滤 null 和 docCount = 0
                    .filter(bucket -> bucket != null && !StringUtils.isEmpty(bucket.getKeyAsString()))
                    .collect(Collectors.toMap(
                            Terms.Bucket::getKeyAsString, // Map 的 key
                            Terms.Bucket::getDocCount // Map 的 value
                    ));

            return resultMap;

        } catch (IOException e) {
            log.error("getTermsAggBucketsToMap 异常：{}",e);
        }
        return Collections.emptyMap();
    }


    @Override
    public TableDataInfo<SuperAdminQueryRspDto> superAdminQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount) {
        TableDataInfo<SuperAdminQueryRspDto> tableDataInfo = new TableDataInfo<>();
        List<SuperAdminQueryRspDto> superAdminQueryRspDtoList = new ArrayList<>();
        int totalRecordCount = 0;
        long start = System.currentTimeMillis();
        try {
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> iPage;
            if (needCount) {
                iPage = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            } else {
                iPage = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            }
            totalRecordCount = iPage.getTotalRecordCount();
            for (Map<String,Object> map : iPage.getData()) {
                SuperAdminQueryRspDto superAdminQueryRspDto = new SuperAdminQueryRspDto(map);
                superAdminQueryRspDto.setApplyUser(accountReturnComponent.getAccountName(superAdminQueryRspDto.getApplyUser()));
                superAdminQueryRspDto.setCurrentDealUsers(accountReturnComponent.getAccountName(superAdminQueryRspDto.getCurrentDealUsers()));
                if (superAdminQueryRspDto.getTicketStatus() == TicketDataStatusEnum.APPLYING.getCode() && superAdminQueryRspDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM.getCode()) {
                    superAdminQueryRspDto.setShowReminderButton(Boolean.TRUE);
                    superAdminQueryRspDto.setShowFollowButton(StrUtil.isBlank(superAdminQueryRspDto.getWxChatGroupId()));
                }
                superAdminQueryRspDtoList.add(superAdminQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
            return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),String.format("分页查询异常：%s",e.getMessage()));
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            tableDataInfo.setTotal(totalRecordCount);
            tableDataInfo.setRows(superAdminQueryRspDtoList);
            return tableDataInfo;
        }

    }

    @Override
    public IPage<BusiAdminQueryRspDto> busiAdminQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount) {
        IPage<BusiAdminQueryRspDto> busiAdminQueryPage;
        PageHelper<BusiAdminQueryRspDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<BusiAdminQueryRspDto> busiAdminQueryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            }
            for (Map<String,Object> map : page.getData()) {
                BusiAdminQueryRspDto busiAdminQueryRspDto = new BusiAdminQueryRspDto(map);
                busiAdminQueryRspDto.setApplyUser(accountReturnComponent.getAccountName(busiAdminQueryRspDto.getApplyUser()));
                busiAdminQueryRspDto.setCurrentDealUsers(accountReturnComponent.getAccountName(busiAdminQueryRspDto.getCurrentDealUsers()));
                if (busiAdminQueryRspDto.getTicketStatus() == TicketDataStatusEnum.APPLYING.getCode() && busiAdminQueryRspDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM.getCode()) {
                    busiAdminQueryRspDto.setShowReminderButton(Boolean.TRUE);
                    busiAdminQueryRspDto.setShowFollowButton(StrUtil.isBlank(busiAdminQueryRspDto.getWxChatGroupId()));
                }
                busiAdminQueryRspDtoList.add(busiAdminQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            busiAdminQueryPage = pageHelper.buildPageEntity(busiAdminQueryRspDtoList);
            return busiAdminQueryPage;
        }

    }

    @Override
    public IPage<OwnQueryRspDto> ownQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount) {
        IPage<OwnQueryRspDto> ownQueryPage;
        PageHelper<OwnQueryRspDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<OwnQueryRspDto> ownQueryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            }
            for (Map<String,Object> map : page.getData()) {
                OwnQueryRspDto ownQueryRspDto = new OwnQueryRspDto(map);
                ownQueryRspDto.setApplyUser(accountReturnComponent.getAccountName(ownQueryRspDto.getApplyUser()));
                ownQueryRspDto.setCurrentDealUsers(accountReturnComponent.getAccountName(ownQueryRspDto.getCurrentDealUsers()));
                if (ownQueryRspDto.getTicketStatus() == TicketDataStatusEnum.APPLYING.getCode() && ownQueryRspDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM.getCode()) {
                    ownQueryRspDto.setShowReminderButton(Boolean.TRUE);
                    ownQueryRspDto.setShowFollowButton(StrUtil.isBlank(ownQueryRspDto.getWxChatGroupId()));
                }
                ownQueryRspDtoList.add(ownQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            ownQueryPage = pageHelper.buildPageEntity(ownQueryRspDtoList);
            return ownQueryPage;
        }

    }

    @Override
    public IPage<BusiQueryRspDto> busiQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders) {
        IPage<BusiQueryRspDto> busiQueryPage;
        PageHelper<BusiQueryRspDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<BusiQueryRspDto> busiQueryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            if (CollectionUtils.isEmpty(sorts)) {
                sorts = Arrays.asList("update_time");
            }
            if (CollectionUtils.isEmpty(orders)) {
                orders = Arrays.asList("DESC");
            }
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize, sorts, orders);
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize, sorts, orders);
            }
            for (Map<String,Object> map : page.getData()) {
                BusiQueryRspDto busiQueryRspDto = new BusiQueryRspDto(map);
                busiQueryRspDto.setApplyUser(accountReturnComponent.getAccountName(busiQueryRspDto.getApplyUser()));
                busiQueryRspDto.setCurrentDealUsers(accountReturnComponent.getAccountName(busiQueryRspDto.getCurrentDealUsers()));
                busiQueryRspDtoList.add(busiQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            busiQueryPage = pageHelper.buildPageEntity(busiQueryRspDtoList);
            return busiQueryPage;
        }
    }

    @Override
    public IPage<SlaTagsQueryRspDto> slaTagsQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders) {
        IPage<SlaTagsQueryRspDto> queryPage;
        PageHelper<SlaTagsQueryRspDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<SlaTagsQueryRspDto> queryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            if (CollectionUtils.isEmpty(sorts)) {
                sorts = Arrays.asList("update_time");
            }
            if (CollectionUtils.isEmpty(orders)) {
                orders = Arrays.asList("DESC");
            }
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize, sorts, orders);
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize, sorts, orders);
            }
            for (Map<String,Object> map : page.getData()) {
                SlaTagsQueryRspDto busiQueryRspDto = new SlaTagsQueryRspDto(map);
                queryRspDtoList.add(busiQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            queryPage = pageHelper.buildPageEntity(queryRspDtoList);
            return queryPage;
        }
    }

    @Override
    public IPage<BusiCommonESQueryRspDto> busiCommonESQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount, List<String> sorts, List<String> orders) {
        IPage<BusiCommonESQueryRspDto> busiQueryPage;
        PageHelper<BusiCommonESQueryRspDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<BusiCommonESQueryRspDto> busiCommonESQueryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            if (CollectionUtils.isEmpty(sorts)) {
                sorts = Arrays.asList("update_time");
            }
            if (CollectionUtils.isEmpty(orders)) {
                orders = Arrays.asList("DESC");
            }
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize, sorts, orders);
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize, sorts, orders);
            }
            for (Map<String,Object> map : page.getData()) {
                BusiCommonESQueryRspDto busiCommonESQueryRspDto = new BusiCommonESQueryRspDto(map);
                busiCommonESQueryRspDtoList.add(busiCommonESQueryRspDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            busiQueryPage = pageHelper.buildPageEntity(busiCommonESQueryRspDtoList);
            return busiQueryPage;
        }
    }

    @Override
    public void update(String index, String id, String json) {
        tfsESClient.update(index, id, json);
    }

    @Override
    public IPage<BusiTicketDataDto> ncsESQuery(String index, QueryBuilder queryBuilder, int pageNo, int pageSize, boolean needCount) {
        IPage<BusiTicketDataDto> busiTicketDataQueryPage;
        PageHelper<BusiTicketDataDto> pageHelper = new PageHelper(pageNo, pageSize, 0);
        List<BusiTicketDataDto> busiTicketDataQueryRspDtoList = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            ConvertUtils.register(new DateConverter(), Date.class);
            IPage<Map<String, Object>> page;
            if (needCount) {
                page = tfsESClient.pageQueryAndCount(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
                pageHelper = new PageHelper(pageNo, pageSize, page.getTotalRecordCount());
            } else {
                page = tfsESClient.pageQuery(index, queryBuilder, pageNo, pageSize,
                        Arrays.asList("update_time"),
                        Arrays.asList("DESC"));
            }
            for (Map<String,Object> map : page.getData()) {
                BusiTicketDataDto busiTicketDataDto = new BusiTicketDataDto(map);
                busiTicketDataQueryRspDtoList.add(busiTicketDataDto);
            }
        } catch (Exception e) {
            log.error("分页查询异常：{}",e);
        } finally {
            long end = System.currentTimeMillis();
            log.info("es分页查询总耗时 {}ms, index: {}", end - start, index);
            busiTicketDataQueryPage = pageHelper.buildPageEntity(busiTicketDataQueryRspDtoList);
            return busiTicketDataQueryPage;
        }
    }

}
