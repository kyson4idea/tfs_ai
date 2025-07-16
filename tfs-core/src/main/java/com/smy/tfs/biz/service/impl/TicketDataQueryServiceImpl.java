package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.smy.framework.base.IPage;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.DownloadTicketDataReqDto;
import com.smy.tfs.api.dto.DownloadTicketDataRespDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.mapper.TicketDataMapper;
import com.smy.tfs.biz.service.TicketDataESService;
import com.smy.tfs.common.constant.HttpStatus;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-12-23
 */
@Slf4j
@Component("ticketDataQueryServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单数据查询服务", apiInterface = ITicketDataQueryService.class)
public class TicketDataQueryServiceImpl extends ServiceImpl<TicketDataMapper, TicketData> implements ITicketDataQueryService {

    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    ITicketTemplateService ticketTemplateService;
    @Resource
    private AccountReturnComponent accountReturnComponent;
    @Resource
    TicketDataMapper ticketDataMapper;
    @Resource
    private TicketDataESService ticketDataESService;
    @Resource
    private ITicketAccountService ticketAccountService;

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Value("${es.ticket_data_info.index}")
    private String index;


    @Override
    public Response<List<DownloadTicketDataRespDto>> queryDownloadTicketDataList(DownloadTicketDataReqDto downloadTicketDataReqDto) {
        List<String> ticketDataIdList = downloadTicketDataReqDto.getTicketDataIdList();
        LambdaQueryWrapper<TicketData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(TicketData::getDeleteTime);
        if (CollectionUtils.isNotEmpty(ticketDataIdList)) {
            lambdaQueryWrapper.in(TicketData::getId, ticketDataIdList);
        }
        //查询数据
        PageInfo<TicketData> ticketDataPageInfo = PageHelper.<TicketData>startPage(downloadTicketDataReqDto.getPageNum(), downloadTicketDataReqDto.getPageSize())
                .doSelectPageInfo(() -> ticketDataMapper.selectList(lambdaQueryWrapper));
        return new Response<>().success(getDownloadTicketDataDtoList(ticketDataPageInfo.getList()));
    }

    @Override
    public TableDataInfo<FuzzyQueryRspDto> fuzzyQuery(FuzzyQueryReqDto fuzzyQueryReqDto, String sameOriginId, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        if (Objects.isNull(fuzzyQueryReqDto.getUserDealType())) {
            return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()),"处理类型不能为空");
        }
        //根据查询条件构造es的BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String appId = fuzzyQueryReqDto.getAppId();
        if (StringUtils.isNotEmpty(appId)) {
            BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
            appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
            appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
            boolQueryBuilder.filter(appIdBoolQueryBuilder);
        }
        //delete_time IS NULL
        boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
        //订单状态 ticket_status  not in ('INIT', 'INIT')
        boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
        //组装查询条件
        String searchValue =  fuzzyQueryReqDto.getSearchValue();
        if (StringUtils.isNotEmpty(searchValue)) {
            //模糊匹配
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
        } else {
            Date updateStartTime = DateUtil.offsetDay(new Date(), -30);
            Date updateEndTime = new Date();
            String updateStartTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, updateStartTime);
            String updateEndTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, updateEndTime);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("update_time")
                    .from(updateStartTimeStr).to(updateEndTimeStr);
            //时间范围
            boolQueryBuilder.must(rangeQuery);
        }
        UserDealTypeEnum userDealType = fuzzyQueryReqDto.getUserDealType();
        String currentSameOriginId = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
        switch (userDealType) {
            case MY_DEAL_ALL:
                BoolQueryBuilder myDealAllBoolQueryBuilder = QueryBuilders.boolQuery();
                myDealAllBoolQueryBuilder.should(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.filter(myDealAllBoolQueryBuilder);
                break;
            case MY_DEAL_WAITING_HANDLE:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_DEAL_HANDLED:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_DEAL_HAS_CC:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_APPLY_ALL:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_APPLY_APPLYING:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                break;
            case MY_APPLY_PASS:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", TicketDataStatusEnum.APPLY_END.getCode()));
                break;
            case MY_APPLY_REJECT:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", TicketDataStatusEnum.REJECT.getCode()));
                break;
            case ALL_ALL:
                break;
            case ALL_APPLYING:
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                break;
            case ALL_APPLYEND:
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLY_END.getCode()));
                break;
            default:
                return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()),String.format("无效的处理类型:%s",userDealType));
        }
        log.info("boolQueryBuilder:{}", boolQueryBuilder);
        TableDataInfo<FuzzyQueryRspDto> tableDataInfo = ticketDataESService.fuzzyQuery(index, boolQueryBuilder, fuzzyQueryReqDto.getPageNum(), fuzzyQueryReqDto.getPageSize(), fuzzyQueryReqDto.isNeedCount());
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return tableDataInfo;
    }

    @Override
    public Response<Long> fuzzyQueryCount(FuzzyQueryReqDto fuzzyQueryReqDto, String sameOriginId, String userType, String userId, String userName) {
        if (Objects.isNull(fuzzyQueryReqDto.getUserDealType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"处理类型不能为空");
        }
        //根据查询条件构造es的BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String appId = fuzzyQueryReqDto.getAppId();
        if (StringUtils.isNotEmpty(appId)) {
            BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
            appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
            appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
            boolQueryBuilder.filter(appIdBoolQueryBuilder);
        }
        //delete_time IS NULL
        boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
        //订单状态 ticket_status  not in ('INIT', 'INIT')
        boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
        //组装查询条件
        String searchValue =  fuzzyQueryReqDto.getSearchValue();
        if (StringUtils.isNotEmpty(searchValue)) {
            //模糊匹配
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
        } else {
            Date updateStartTime = DateUtil.offsetDay(new Date(), -30);
            Date updateEndTime = new Date();
            String updateStartTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, updateStartTime);
            String updateEndTimeStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, updateEndTime);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("update_time")
                    .from(updateStartTimeStr).to(updateEndTimeStr);
            //时间范围
            boolQueryBuilder.must(rangeQuery);
        }
        UserDealTypeEnum userDealType = fuzzyQueryReqDto.getUserDealType();
        String currentSameOriginId = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
        switch (userDealType) {
            case MY_DEAL_ALL:
                BoolQueryBuilder myDealAllBoolQueryBuilder = QueryBuilders.boolQuery();
                myDealAllBoolQueryBuilder.should(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND))
                        .should(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.filter(myDealAllBoolQueryBuilder);
                break;
            case MY_DEAL_WAITING_HANDLE:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_DEAL_HANDLED:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_DEAL_HAS_CC:
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_APPLY_ALL:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                break;
            case MY_APPLY_APPLYING:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                break;
            case MY_APPLY_PASS:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", TicketDataStatusEnum.APPLY_END.getCode()));
                break;
            case MY_APPLY_REJECT:
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", TicketDataStatusEnum.REJECT.getCode()));
                break;
            case ALL_ALL:
                break;
            case ALL_APPLYING:
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                break;
            case ALL_APPLYEND:
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLY_END.getCode()));
                break;
            default:
                throw new RuntimeException(String.format("无效的处理类型:%s",userDealType));
        }
        log.info("boolQueryBuilder:{}", boolQueryBuilder);
        return ticketDataESService.fuzzyQueryCount(index, boolQueryBuilder);
    }

    @Override
    public TableDataInfo accurateQuery(AccurateQueryReqDto accurateQueryReqDto) {
        return null;
    }


    /**
     * @param superAdminQueryReqDto
     * @return
     */
    @Override
    public TableDataInfo superAdminQuery(SuperAdminQueryReqDto superAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        String extendFieldsStr = null;
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getAppIdList()) && 1 == superAdminQueryReqDto.getAppIdList().size()) {
            String appId = superAdminQueryReqDto.getAppIdList().get(0);
            TicketApp ticketApp = ticketAppService.getById(appId);
            if (Objects.isNull(ticketApp)) {
                String errorMsg = String.format("不存在的业务（id:%s）", appId);
                return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), errorMsg);
            }
            extendFieldsStr = ticketApp.getExtendFields();
        };
        //参数检验
        Response response = TicketDataQueryServiceInner.checkSuperAdminQuery(superAdminQueryReqDto, sameOriginId, userType, userId, userName, extendFieldsStr);
        if (!response.isSuccess()) {
            return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), response.getMsg());
        }
        //根据查询条件构造es的BoolQueryBuilder
        long boolQueryBuilderStart = System.currentTimeMillis();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //delete_time IS NULL
        boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
        //订单状态 ticket_status  not in ('INIT', 'DRAFT')
        boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
        //模糊查询字段
        String searchValue =  superAdminQueryReqDto.getSearchValue();
        if (StringUtils.isNotEmpty(searchValue)) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
        }
        //业务ID
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getAppIdList())) {
            BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
            appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", superAdminQueryReqDto.getAppIdList()));
            for (String appId : superAdminQueryReqDto.getAppIdList()) {
                appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
            }
            boolQueryBuilder.filter(appIdBoolQueryBuilder);
        }
        //模版id
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getTemplateIdList())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("template_id.keyword", superAdminQueryReqDto.getTemplateIdList()));
        }
        //模版分类
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getCategoryIdList())) {
            List<Integer> categoryIdList = superAdminQueryReqDto.getCategoryIdList();
            List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
            boolQueryBuilder.filter(QueryBuilders.termsQuery("beyond_category_id.keyword", categoryIdStrList));
        }
        //工单状态
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getTicketStatusList())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", superAdminQueryReqDto.getTicketStatusList()));
        }
        //创建时间
        if (Objects.nonNull(superAdminQueryReqDto.getCreateStartTime()) && Objects.nonNull(superAdminQueryReqDto.getCreateEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").from(superAdminQueryReqDto.getCreateStartTime()).to(superAdminQueryReqDto.getCreateEndTime()));
        }
        //结单时间
        if (Objects.nonNull(superAdminQueryReqDto.getFinishStartTime()) && Objects.nonNull(superAdminQueryReqDto.getFinishEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("ticket_finish_time").from(superAdminQueryReqDto.getFinishStartTime()).to(superAdminQueryReqDto.getFinishEndTime()));
        }
        //申请人
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getApplyUser())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", superAdminQueryReqDto.getApplyUser()).operator(Operator.AND));
        }
        //受理人
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getCurrentDealUser())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", superAdminQueryReqDto.getCurrentDealUser()).operator(Operator.AND));
        }
        //更新时间
        if (Objects.nonNull(superAdminQueryReqDto.getUpdateStartTime()) && Objects.nonNull(superAdminQueryReqDto.getUpdateEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(superAdminQueryReqDto.getUpdateStartTime()).to(superAdminQueryReqDto.getUpdateEndTime()));
        }
        //扩展字段
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend1())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend1", superAdminQueryReqDto.getExtend1()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend2())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend2", superAdminQueryReqDto.getExtend2()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend3())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend3", superAdminQueryReqDto.getExtend3()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend4())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend4", superAdminQueryReqDto.getExtend4()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend5())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend5", superAdminQueryReqDto.getExtend5()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend6())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend6", superAdminQueryReqDto.getExtend6()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend7())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend7", superAdminQueryReqDto.getExtend7()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend8())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend8", superAdminQueryReqDto.getExtend8()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend9())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend9", superAdminQueryReqDto.getExtend9()).operator(Operator.AND));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend10())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend10", superAdminQueryReqDto.getExtend10()).operator(Operator.AND));
        }
        log.info("boolQueryBuilder:{}", boolQueryBuilder);
        long boolQueryBuilderEnd = System.currentTimeMillis();
        log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
        int pageNo = superAdminQueryReqDto.getPageNum();
        int pageSize = superAdminQueryReqDto.getPageSize();
        TableDataInfo<SuperAdminQueryRspDto> tableDataInfo = ticketDataESService.superAdminQuery(index, boolQueryBuilder, pageNo, pageSize, superAdminQueryReqDto.isNeedCount());
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return tableDataInfo;
    }

    @Override
    public TableDataInfo busiAdminQuery(BusiAdminQueryReqDto busiAdminQueryReqDto, String sameOriginId, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        TableDataInfo<BusiAdminQueryRspDto> tableDataInfo = new TableDataInfo<>();
        try {
            if (StringUtils.isAnyEmpty(sameOriginId, userType, userId, userName)) {
                return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), "登入账号信息为空");
            }
            //权限校验
            List<String> queryAppIdList = busiAdminQueryReqDto.getAppIdList();
            if (!"ldap".equals(userType) || !"admin".equals(userId)) {//不是管理员
                String currentSameOriginId = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
                List<String> adminAppIdList = ticketAppService.queryAdminAppListForPointUser(currentSameOriginId);
                if (ObjectHelper.isEmpty(adminAppIdList)) {
                    return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), "此用户没有任何业务的管理员权限");
                }
                List<String> reqAppIdList = busiAdminQueryReqDto.getAppIdList();
                if (CollectionUtils.isNotEmpty(reqAppIdList) && !adminAppIdList.containsAll(reqAppIdList)) {
                    return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), "此用户没有所有选择的业务权限");
                }
                if (CollectionUtils.isEmpty(reqAppIdList)){
                    queryAppIdList = adminAppIdList;
                }
            }

            String extendFieldsStr = null;
            if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getAppIdList()) && 1 == busiAdminQueryReqDto.getAppIdList().size()) {
                String appId = busiAdminQueryReqDto.getAppIdList().get(0);
                TicketApp ticketApp = ticketAppService.getById(appId);
                if (Objects.isNull(ticketApp)) {
                    String errorMsg = String.format("不存在的业务（id:%s）", appId);
                    return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), errorMsg);
                }
                extendFieldsStr = ticketApp.getExtendFields();
            };
            //参数校验
            Response response = TicketDataQueryServiceInner.checkBusiAdminQuery(busiAdminQueryReqDto, sameOriginId, userType, userId, userName, extendFieldsStr);
            if (!response.isSuccess()) {
                return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), response.getMsg());
            }
            //根据查询条件构造es的BoolQueryBuilder
            long boolQueryBuilderStart = System.currentTimeMillis();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //delete_time IS NULL
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
            //订单状态 ticket_status  not in ('INIT', 'INIT')
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
            //模糊查询字段
            String searchValue =  busiAdminQueryReqDto.getSearchValue();
            if (StringUtils.isNotEmpty(searchValue)) {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
            }
            //业务ID
            if (CollectionUtils.isNotEmpty(queryAppIdList)) {
                BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
                appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", queryAppIdList));
                for (String appId : queryAppIdList) {
                    appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
                }
                boolQueryBuilder.filter(appIdBoolQueryBuilder);
            }
            //模版id
            if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getTemplateIdList())) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("template_id.keyword", busiAdminQueryReqDto.getTemplateIdList()));
            }
            //模版分类
            if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getCategoryIdList())) {
                List<Integer> categoryIdList = busiAdminQueryReqDto.getCategoryIdList();
                List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
                boolQueryBuilder.filter(QueryBuilders.termsQuery("beyond_category_id.keyword", categoryIdStrList));
            }
            //工单状态
            if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getTicketStatusList())) {
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiAdminQueryReqDto.getTicketStatusList()));
            }
            //创建时间
            if (Objects.nonNull(busiAdminQueryReqDto.getCreateStartTime()) && Objects.nonNull(busiAdminQueryReqDto.getCreateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").from(busiAdminQueryReqDto.getCreateStartTime()).to(busiAdminQueryReqDto.getCreateEndTime()));
            }
            //结单时间
            if (Objects.nonNull(busiAdminQueryReqDto.getFinishStartTime()) && Objects.nonNull(busiAdminQueryReqDto.getFinishEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("ticket_finish_time").from(busiAdminQueryReqDto.getFinishStartTime()).to(busiAdminQueryReqDto.getFinishEndTime()));
            }
            //申请人
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getApplyUser())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", busiAdminQueryReqDto.getApplyUser()).operator(Operator.AND));
            }
            //受理人
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getCurrentDealUser())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", busiAdminQueryReqDto.getCurrentDealUser()).operator(Operator.AND));
            }
            //更新时间
            if (Objects.nonNull(busiAdminQueryReqDto.getUpdateStartTime()) && Objects.nonNull(busiAdminQueryReqDto.getUpdateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(busiAdminQueryReqDto.getUpdateStartTime()).to(busiAdminQueryReqDto.getUpdateEndTime()));
            }
            // 当前处理节点
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getCurrentNodeName())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_node_name", busiAdminQueryReqDto.getCurrentNodeName()).operator(Operator.AND));
            }
            //扩展字段
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend1())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend1", busiAdminQueryReqDto.getExtend1()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend2())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend2", busiAdminQueryReqDto.getExtend2()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend3())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend3", busiAdminQueryReqDto.getExtend3()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend4())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend4", busiAdminQueryReqDto.getExtend4()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend5())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend5", busiAdminQueryReqDto.getExtend5()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend6())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend6", busiAdminQueryReqDto.getExtend6()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend7())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend7", busiAdminQueryReqDto.getExtend7()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend8())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend8", busiAdminQueryReqDto.getExtend8()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend9())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend9", busiAdminQueryReqDto.getExtend9()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend10())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend10", busiAdminQueryReqDto.getExtend10()).operator(Operator.AND));
            }
            log.info("boolQueryBuilder:{}", boolQueryBuilder);
            long boolQueryBuilderEnd = System.currentTimeMillis();
            log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
            int pageNo = busiAdminQueryReqDto.getPageNum();
            int pageSize = busiAdminQueryReqDto.getPageSize();
            IPage<BusiAdminQueryRspDto> page = ticketDataESService.busiAdminQuery(index, boolQueryBuilder, pageNo, pageSize, busiAdminQueryReqDto.isNeedCount());
            List<BusiAdminQueryRspDto> busiAdminQueryRspDtoList = Lists.newArrayList();
            if (null != page) {
                busiAdminQueryRspDtoList = (List)page.getData();
                tableDataInfo.setTotal(page.getTotalRecordCount());
            }
            tableDataInfo.setRows(busiAdminQueryRspDtoList);
        } catch (Exception e) {
            log.error("业务管理员查询工单列表异常:{}",e);
            return tableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return tableDataInfo;
    }


    /**
     * @param ownQueryReqDto
     * @return
     */
    @Override
    public TableDataInfo ownQuery(OwnQueryReqDto ownQueryReqDto, String sameOriginId, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        TableDataInfo<OwnQueryRspDto> tableDataInfo = new TableDataInfo<>();
        try {
            String extendFieldsStr = null;
            if (CollectionUtils.isNotEmpty(ownQueryReqDto.getAppIdList()) && 1 == ownQueryReqDto.getAppIdList().size()) {
                String appId = ownQueryReqDto.getAppIdList().get(0);
                TicketApp ticketApp = ticketAppService.getById(appId);
                if (Objects.isNull(ticketApp)) {
                    String errorMsg = String.format("不存在的业务（id:%s）", appId);
                    return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), errorMsg);
                }
                extendFieldsStr = ticketApp.getExtendFields();
            };
            Response response = TicketDataQueryServiceInner.checkOwnQuery(ownQueryReqDto, sameOriginId, userType, userId, userName, extendFieldsStr);
            if (!response.isSuccess()) {
                return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), response.getMsg());
            }
            //根据查询条件构造es的BoolQueryBuilder
            long boolQueryBuilderStart = System.currentTimeMillis();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //delete_time IS NULL
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
            //订单状态 ticket_status  not in ('INIT', 'INIT')
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
            //模糊查询字段
            String searchValue =  ownQueryReqDto.getSearchValue();
            if (StringUtils.isNotEmpty(searchValue)) {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
            }
            //业务ID
            if (CollectionUtils.isNotEmpty(ownQueryReqDto.getAppIdList())) {
                BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
                appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", ownQueryReqDto.getAppIdList()));
                for (String appId : ownQueryReqDto.getAppIdList()) {
                    appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
                }
                boolQueryBuilder.filter(appIdBoolQueryBuilder);
            }
            //模版id
            if (CollectionUtils.isNotEmpty(ownQueryReqDto.getTemplateIdList())) {
                boolQueryBuilder.filter(QueryBuilders.termsQuery("template_id.keyword", ownQueryReqDto.getTemplateIdList()));
            }
            //模版分类
            if (CollectionUtils.isNotEmpty(ownQueryReqDto.getCategoryIdList())) {
                List<Integer> categoryIdList = ownQueryReqDto.getCategoryIdList();
                List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
                boolQueryBuilder.filter(QueryBuilders.termsQuery("beyond_category_id.keyword", categoryIdStrList));
            }
            //工单状态
            if (CollectionUtils.isNotEmpty(ownQueryReqDto.getTicketStatusList())) {
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", ownQueryReqDto.getTicketStatusList()));
            }
            //创建时间
            if (Objects.nonNull(ownQueryReqDto.getCreateStartTime()) && Objects.nonNull(ownQueryReqDto.getCreateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").from(ownQueryReqDto.getCreateStartTime()).to(ownQueryReqDto.getCreateEndTime()));
            }
            //结单时间
            if (Objects.nonNull(ownQueryReqDto.getFinishStartTime()) && Objects.nonNull(ownQueryReqDto.getFinishEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("ticket_finish_time").from(ownQueryReqDto.getFinishStartTime()).to(ownQueryReqDto.getFinishEndTime()));
            }
            //申请人
            if (StringUtils.isNotEmpty(ownQueryReqDto.getApplyUser())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", ownQueryReqDto.getApplyUser()).operator(Operator.AND));
            }
            //受理人
            if (StringUtils.isNotEmpty(ownQueryReqDto.getCurrentDealUser())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", ownQueryReqDto.getCurrentDealUser()).operator(Operator.AND));
            }
            //更新时间
            if (Objects.nonNull(ownQueryReqDto.getUpdateStartTime()) && Objects.nonNull(ownQueryReqDto.getUpdateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(ownQueryReqDto.getUpdateStartTime()).to(ownQueryReqDto.getUpdateEndTime()));
            }
            UserDealTypeEnum userDealType = ownQueryReqDto.getUserDealType();
            String currentSameOriginId = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
            switch (userDealType) {
                case MY_DEAL_ALL:
                    //我处理的
                    BoolQueryBuilder myDealAllBoolQueryBuilder = QueryBuilders.boolQuery();
                    myDealAllBoolQueryBuilder.should(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND))
                            .should(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND))
                            .should(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                    boolQueryBuilder.filter(myDealAllBoolQueryBuilder);
                    break;
                case MY_DEAL_WAITING_HANDLE:
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND));
                    break;
                case MY_DEAL_HANDLED:
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND));
                    break;
                case MY_DEAL_HAS_CC:
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_cc_users", currentSameOriginId).operator(Operator.AND));
                    break;
                case MY_APPLY_ALL:
                    //我发起的
                    boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                    break;
                default:
                    return TableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), String.format("无效的处理类型:%s", userDealType));
            }
            //扩展字段
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend1())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend1", ownQueryReqDto.getExtend1()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend2())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend2", ownQueryReqDto.getExtend2()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend3())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend3", ownQueryReqDto.getExtend3()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend4())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend4", ownQueryReqDto.getExtend4()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend5())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend5", ownQueryReqDto.getExtend5()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend6())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend6", ownQueryReqDto.getExtend6()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend7())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend7", ownQueryReqDto.getExtend7()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend8())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend8", ownQueryReqDto.getExtend8()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend9())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend9", ownQueryReqDto.getExtend9()).operator(Operator.AND));
            }
            if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend10())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend10", ownQueryReqDto.getExtend10()).operator(Operator.AND));
            }
            log.info("boolQueryBuilder:{}", boolQueryBuilder);
            long boolQueryBuilderEnd = System.currentTimeMillis();
            log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
            int pageNo = ownQueryReqDto.getPageNum();
            int pageSize = ownQueryReqDto.getPageSize();
            IPage<OwnQueryRspDto> page = ticketDataESService.ownQuery(index, boolQueryBuilder, pageNo, pageSize, ownQueryReqDto.isNeedCount());
            List<OwnQueryRspDto> ownQueryRspDtoList = Lists.newArrayList();
            if (null != page) {
                ownQueryRspDtoList = (List)page.getData();
                tableDataInfo.setTotal(page.getTotalRecordCount());
            }
            tableDataInfo.setRows(ownQueryRspDtoList);
        } catch (Exception e) {
            log.error("我发起/审批的查询工单列表异常:{}",e);
            return tableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return tableDataInfo;
    }

    public static void main(String[] args) {
        List<List<BusiCompareInfo>> busiCompareInfoListList = new ArrayList<>();
        List<BusiCompareInfo> busiCompareInfoList = new ArrayList<>();
        BusiCompareInfo busiCompareInfo = new BusiCompareInfo();
        busiCompareInfo.setCompareId("tags");
        busiCompareInfo.setCompareType(BusiCompareType.CONTAIN_ANY);
        busiCompareInfo.setCompareValue("测试");
        busiCompareInfoList.add(busiCompareInfo);
        List<BusiCompareInfo> busiCompareInfoList1 = new ArrayList<>();
        BusiCompareInfo busiCompareInfo1 = new BusiCompareInfo();
        busiCompareInfo1.setCompareId("tags1");
        busiCompareInfo1.setCompareType(BusiCompareType.CONTAIN_ANY);
        busiCompareInfo1.setCompareValue("测试1");
        busiCompareInfoList1.add(busiCompareInfo1);
        busiCompareInfoListList.add(busiCompareInfoList1);
        System.out.println(JSONObject.toJSONString(busiCompareInfoListList));

    }

    @Override
    public RemoteTableDataInfo<BusiQueryRspDto> busiQuery(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        RemoteTableDataInfo<BusiQueryRspDto> remoteTableDataInfo = new RemoteTableDataInfo<>();
        remoteTableDataInfo.setCode(HttpStatus.SUCCESS);
        remoteTableDataInfo.setMsg("查询成功");
        try {
            //参数校验
            Response response = TicketDataQueryServiceInner.checkBusiQuery(busiQueryReqDto, userType, userId, userName);
            if (!response.isSuccess()) {
                return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), response.getMsg());
            }
            //根据查询条件构造es的BoolQueryBuilder
            long boolQueryBuilderStart = System.currentTimeMillis();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //delete_time IS NULL
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
            //订单状态 ticket_status  not in ('INIT', 'INIT')
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
            //模糊查询字段
            String searchValue =  busiQueryReqDto.getSearchValue();
            if (StringUtils.isNotEmpty(searchValue)) {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(searchValue, "all_ticket_data_info.*").operator(Operator.AND));
            }
            //业务ID
            List<String> queryAppIdList = busiQueryReqDto.getAppIdList();
            if (CollectionUtils.isNotEmpty(queryAppIdList)) {
                BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
                appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", queryAppIdList));
                for (String appId : queryAppIdList) {
                    appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
                }
                boolQueryBuilder.filter(appIdBoolQueryBuilder);
            }
            //模版idList或者模版codeList
            List<String> templateIdList = busiQueryReqDto.getTemplateIdList();
            if (CollectionUtils.isNotEmpty(templateIdList)) {
                BoolQueryBuilder templateIdCodeQueryBuilder = QueryBuilders.boolQuery();
                templateIdCodeQueryBuilder.should(QueryBuilders.termsQuery("template_id.keyword", templateIdList));
                templateIdCodeQueryBuilder.should(QueryBuilders.termsQuery("ticket_template_code.keyword", templateIdList));
                boolQueryBuilder.filter(templateIdCodeQueryBuilder);
            }
            //模版分类
            if (CollectionUtils.isNotEmpty(busiQueryReqDto.getCategoryIdList())) {
                List<Integer> categoryIdList = busiQueryReqDto.getCategoryIdList();
                List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
                boolQueryBuilder.filter(QueryBuilders.termsQuery("beyond_category_id.keyword", categoryIdStrList));
            }
            //创建时间
            if (StringUtils.isNotEmpty(busiQueryReqDto.getCreateStartTime()) && StringUtils.isNotEmpty(busiQueryReqDto.getCreateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("create_time").from(busiQueryReqDto.getCreateStartTime()).to(busiQueryReqDto.getCreateEndTime()));
            }
            //结单时间
            if (StringUtils.isNotEmpty(busiQueryReqDto.getFinishStartTime()) && StringUtils.isNotEmpty(busiQueryReqDto.getFinishEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("ticket_finish_time").from(busiQueryReqDto.getFinishStartTime()).to(busiQueryReqDto.getFinishEndTime()));
            }
            //更新时间
            if (StringUtils.isNotEmpty(busiQueryReqDto.getUpdateStartTime()) && StringUtils.isNotEmpty(busiQueryReqDto.getUpdateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(busiQueryReqDto.getUpdateStartTime()).to(busiQueryReqDto.getUpdateEndTime()));
            }
            //扩展字段
            if (StringUtils.isNotEmpty(busiQueryReqDto.getExtend1())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend1", busiQueryReqDto.getExtend1()).operator(Operator.AND));
            }
            //扩展字段
            if (StringUtils.isNotEmpty(busiQueryReqDto.getExtend2())) {
                boolQueryBuilder.must(QueryBuilders.matchQuery("extend2", busiQueryReqDto.getExtend2()).operator(Operator.AND));
            }
            //表达式
            List<List<BusiCompareInfo>> conditions = busiQueryReqDto.getBusiCompareInfoList();
            if (StringUtils.isNotEmpty(conditions)) {
                BoolQueryBuilder orBoolQuery = QueryBuilders.boolQuery();
                for (List<BusiCompareInfo> orConditions : conditions) {
                    BoolQueryBuilder andBoolQuery = QueryBuilders.boolQuery();
                    for (BusiCompareInfo andCondition : orConditions) {
                        String compareId = andCondition.getCompareId();
                        BusiCompareType compareType = andCondition.getCompareType();
                        Object compareValue = andCondition.getCompareValue();
                        String compareValueStr = String.valueOf(compareValue);
                        List<String> compareValueList = Arrays.asList(compareValueStr.split(","));
                        switch (compareType) {
                            case CONTAIN_ANY:
                                for (String value : compareValueList) {
                                    andBoolQuery.should(QueryBuilders.matchQuery(compareId, value).operator(Operator.AND));
                                }
                                break;
                            case NOT_CONTAIN_ANY:
                                for (String value : compareValueList) {
                                    andBoolQuery.mustNot(QueryBuilders.matchQuery(compareId, value).operator(Operator.AND));
                                }
                                break;
                            case BELONG_TO:
                                andBoolQuery.should(QueryBuilders.termsQuery(compareId, compareValueList));
                                break;
                            case NOT_BELONG_TO:
                                andBoolQuery.mustNot(QueryBuilders.termsQuery(compareId, compareValueList));
                            default:
                                throw new NotImplementedException(String.format("未实现的比较类型:%s", compareType.getCode()));
                        }
                    }
                    orBoolQuery.should(andBoolQuery);
                }
                orBoolQuery.minimumShouldMatch(1);
                boolQueryBuilder.filter(orBoolQuery);
            }
            BusiQueryUserDealTypeEnum userDealType = busiQueryReqDto.getUserDealType();
            TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
            if (null == ticketRemoteAccountDto || StringUtils.isEmpty(ticketRemoteAccountDto.getSameOriginId())) {
                return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), String.format("不存在的账户userId:%s,userType:%s", userId, userType));
            }
            String currentSameOriginId = String.format("\"sameOriginId\":\"%s\"", ticketRemoteAccountDto.getSameOriginId());
            log.info(String.format("登录账户userId:%s,userType:%s,sameOriginId:%s", userId, userType,currentSameOriginId));
            switch (userDealType) {
                case ALL_ALL:
                    //工单状态
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getTicketStatusList())) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiQueryReqDto.getTicketStatusList()));
                    }
                    //申请人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getApplyUserList())) {
                        BoolQueryBuilder applyUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String applyUser : busiQueryReqDto.getApplyUserList()) {
                            applyUserBoolQueryBuilder.should(QueryBuilders.matchQuery("apply_user", applyUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(applyUserBoolQueryBuilder);
                    }
                    //受理人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getCurrentDealUserList())) {
                        BoolQueryBuilder currentDealUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String currentDealUser : busiQueryReqDto.getCurrentDealUserList()) {
                            currentDealUserBoolQueryBuilder.should(QueryBuilders.matchQuery("current_deal_users", currentDealUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(currentDealUserBoolQueryBuilder);
                    }
                    break;
                case MY_DEPT_APPLY_WAITING_DISPATCH:
                    //工单状态
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getTicketStatusList())) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiQueryReqDto.getTicketStatusList()));
                    }
                    //申请人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getApplyUserList())) {
                        BoolQueryBuilder applyUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String applyUser : busiQueryReqDto.getApplyUserList()) {
                            applyUserBoolQueryBuilder.should(QueryBuilders.matchQuery("apply_user", applyUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(applyUserBoolQueryBuilder);
                    }
                    //受理人
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", "工单处理池").operator(Operator.AND));
                    break;
                case MY_APPLY_ALL:
                    //工单状态
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getTicketStatusList())) {
                        boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiQueryReqDto.getTicketStatusList()));
                    }
                    //受理人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getCurrentDealUserList())) {
                        BoolQueryBuilder currentDealUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String currentDealUser : busiQueryReqDto.getCurrentDealUserList()) {
                            currentDealUserBoolQueryBuilder.should(QueryBuilders.matchQuery("current_deal_users", currentDealUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(currentDealUserBoolQueryBuilder);
                    }
                    boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                    break;
                case MY_DEAL_WAITING_HANDLE:
                    //申请人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getApplyUserList())) {
                        BoolQueryBuilder applyUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String applyUser : busiQueryReqDto.getApplyUserList()) {
                            applyUserBoolQueryBuilder.should(QueryBuilders.matchQuery("apply_user", applyUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(applyUserBoolQueryBuilder);
                    }
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND));
                    boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                    break;
                case MY_DEAL_FINISH:
                    //申请人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getApplyUserList())) {
                        BoolQueryBuilder applyUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String applyUser : busiQueryReqDto.getApplyUserList()) {
                            applyUserBoolQueryBuilder.should(QueryBuilders.matchQuery("apply_user", applyUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(applyUserBoolQueryBuilder);
                    }
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_done_users", currentSameOriginId).operator(Operator.AND));
                    boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLY_END.getCode()));
                    break;
                case MY_APPLY_WITHDRAW:
                    boolQueryBuilder.must(QueryBuilders.matchQuery("apply_user", currentSameOriginId).operator(Operator.AND));
                    boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.WITHDRAW.getCode()));
                    break;
                case BACK_MY:
                    //申请人
                    if (CollectionUtils.isNotEmpty(busiQueryReqDto.getApplyUserList())) {
                        BoolQueryBuilder applyUserBoolQueryBuilder = QueryBuilders.boolQuery();
                        for (String applyUser : busiQueryReqDto.getApplyUserList()) {
                            applyUserBoolQueryBuilder.should(QueryBuilders.matchQuery("apply_user", applyUser).operator(Operator.AND));
                        }
                        boolQueryBuilder.filter(applyUserBoolQueryBuilder);
                    }
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_node_name", "(重审)").operator(Operator.AND));
                    boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", currentSameOriginId).operator(Operator.AND));
                    boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                    break;
                default:
                    return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), String.format("无效的处理类型:%s", userDealType));
            }
            log.info("boolQueryBuilder:{}", boolQueryBuilder);
            long boolQueryBuilderEnd = System.currentTimeMillis();
            log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
            int pageNo = busiQueryReqDto.getPageNum();
            int pageSize = busiQueryReqDto.getPageSize();
            List<SortDescriptor> sortDescriptorList = busiQueryReqDto.getSortDescriptorList();
            List<String> sorts = new ArrayList<>();
            List<String> orders = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sortDescriptorList)) {
                sorts = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getField())) {
                        throw new ServiceException("排序参数异常");
                    }
                    return it.getField();
                }).collect(Collectors.toList());
                orders = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getDirection())) {
                        throw new ServiceException("排序参数异常");
                    }
                    return it.getDirection();
                }).collect(Collectors.toList());
            }
            IPage<BusiQueryRspDto> page = ticketDataESService.busiQuery(index, boolQueryBuilder, pageNo, pageSize, busiQueryReqDto.isNeedCount(), sorts, orders);
            List<BusiQueryRspDto> busiQueryRspDtoList = Lists.newArrayList();
            if (null != page) {
                busiQueryRspDtoList = (List)page.getData();
                remoteTableDataInfo.setTotal(page.getTotalRecordCount());
            }
            remoteTableDataInfo.setRows(busiQueryRspDtoList);
        } catch (Exception e) {
            log.error("业务管理员查询工单列表异常:{}",e);
            return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return remoteTableDataInfo;
    }

    @Override
    public RemoteTableDataInfo<BusiCommonESQueryRspDto> busiCommonESQuery(BusiCommonESQueryReqDto busiCommonESQueryReqDto) {
        long start = System.currentTimeMillis();
        RemoteTableDataInfo<BusiCommonESQueryRspDto> remoteTableDataInfo = new RemoteTableDataInfo<>();
        remoteTableDataInfo.setCode(HttpStatus.SUCCESS);
        remoteTableDataInfo.setMsg("查询成功");
        try {
            //参数校验
            if (org.apache.commons.collections.CollectionUtils.isEmpty(busiCommonESQueryReqDto.getAppIdList())) {
                return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), "业务id的List不能为空");
            }
            if (org.apache.commons.collections.CollectionUtils.isEmpty(busiCommonESQueryReqDto.getTemplateIdList())) {
                return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), "模板id的List不能为空");
            }
            //根据查询条件构造es的BoolQueryBuilder
            long boolQueryBuilderStart = System.currentTimeMillis();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //delete_time IS NULL
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
            //订单状态 ticket_status  not in ('INIT', 'INIT')
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));

            //业务ID
            List<String> queryAppIdList = busiCommonESQueryReqDto.getAppIdList();
            if (CollectionUtils.isNotEmpty(queryAppIdList)) {
                BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
                appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", queryAppIdList));
                for (String appId : queryAppIdList) {
                    appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
                }
                boolQueryBuilder.filter(appIdBoolQueryBuilder);
            }
            //模版idList或者模版codeList
            List<String> templateIdList = busiCommonESQueryReqDto.getTemplateIdList();
            if (CollectionUtils.isNotEmpty(templateIdList)) {
                BoolQueryBuilder templateIdCodeQueryBuilder = QueryBuilders.boolQuery();
                templateIdCodeQueryBuilder.should(QueryBuilders.termsQuery("template_id.keyword", templateIdList));
                templateIdCodeQueryBuilder.should(QueryBuilders.termsQuery("ticket_template_code.keyword", templateIdList));
                boolQueryBuilder.filter(templateIdCodeQueryBuilder);
            }
            //工单状态
            if (CollectionUtils.isNotEmpty(busiCommonESQueryReqDto.getTicketStatusList())) {
                boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiCommonESQueryReqDto.getTicketStatusList()));
            }

            //表达式
            List<BusiESCompareInfo> busiESCompareInfoList = busiCommonESQueryReqDto.getBusiESCompareInfoList();
            if (StringUtils.isNotEmpty(busiESCompareInfoList)) {
                BoolQueryBuilder andBoolQuery = QueryBuilders.boolQuery();
                for (BusiESCompareInfo busiESCompareInfo : busiESCompareInfoList) {
                    String compareKey = busiESCompareInfo.getCompareKey();
                    BusiESCompareType compareType = busiESCompareInfo.getCompareType();
                    Object compareValue = busiESCompareInfo.getCompareValue();
                    switch (compareType) {
                        case EQ:
                            String compareValueStr = String.valueOf(compareValue);
                            andBoolQuery.must(QueryBuilders.termQuery(compareKey, compareValueStr));
                            break;
                        case LIKE:
                            compareValueStr = String.valueOf(compareValue);
                            andBoolQuery.must(QueryBuilders.matchQuery(compareKey, compareValueStr).operator(Operator.AND));
                            break;
                        default:
                            throw new NotImplementedException(String.format("未实现的比较类型:%s", compareType.getCode()));
                    }
                }
                boolQueryBuilder.filter(andBoolQuery);
            }
            log.info("boolQueryBuilder:{}", boolQueryBuilder);
            long boolQueryBuilderEnd = System.currentTimeMillis();
            log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
            int pageNo = busiCommonESQueryReqDto.getPageNum();
            int pageSize = busiCommonESQueryReqDto.getPageSize();
            List<SortDescriptor> sortDescriptorList = busiCommonESQueryReqDto.getSortDescriptorList();
            List<String> sorts = new ArrayList<>();
            List<String> orders = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sortDescriptorList)) {
                sorts = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getField())) {
                        throw new ServiceException("排序参数为空");
                    }
                    return it.getField();
                }).collect(Collectors.toList());
                orders = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getDirection())) {
                        throw new ServiceException("排序方向为空");
                    }
                    return it.getDirection();
                }).collect(Collectors.toList());
            }
            IPage<BusiCommonESQueryRspDto> page = ticketDataESService.busiCommonESQuery(index, boolQueryBuilder, pageNo, pageSize, Boolean.FALSE, sorts, orders);
            List<BusiCommonESQueryRspDto> busiCommonESQueryRspDtoList = Lists.newArrayList();
            if (null != page) {
                busiCommonESQueryRspDtoList = (List)page.getData();
                remoteTableDataInfo.setTotal(page.getTotalRecordCount());
            }
            remoteTableDataInfo.setRows(busiCommonESQueryRspDtoList);
        } catch (Exception e) {
            log.error("业务管理员查询工单列表异常:{}",e);
            return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return remoteTableDataInfo;
    }

    @Override
    public RemoteTableDataInfo<SlaTagsQueryRspDto> slaTagsQuery(SlaTagsQueryReqDto slaTagsQueryReqDto, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        RemoteTableDataInfo<SlaTagsQueryRspDto> remoteTableDataInfo = new RemoteTableDataInfo<>();
        remoteTableDataInfo.setCode(HttpStatus.SUCCESS);
        remoteTableDataInfo.setMsg("查询成功");
        try {
            //参数校验
            Response response = TicketDataQueryServiceInner.checkSlaTagsQuery(slaTagsQueryReqDto, userType, userId, userName);
            if (!response.isSuccess()) {
                return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.CHECK_PARAMS_EXCEPTION.getCode()), response.getMsg());
            }
            //根据查询条件构造es的BoolQueryBuilder
            long boolQueryBuilderStart = System.currentTimeMillis();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //delete_time IS NULL
            boolQueryBuilder.mustNot(QueryBuilders.existsQuery("delete_time"));
            //订单状态 ticket_status  not in ('INIT', 'DRAFT')
            boolQueryBuilder.mustNot(QueryBuilders.termsQuery("ticket_status.keyword", Arrays.asList("INIT","DRAFT")));
            //模版idList或者模版codeList
            List<String> templateIdList = slaTagsQueryReqDto.getTemplateIdList();
            if (CollectionUtils.isNotEmpty(templateIdList)) {
                boolQueryBuilder.mustNot(QueryBuilders.termsQuery("template_id.keyword", templateIdList));
            }
            //更新时间
            if (StringUtils.isNotEmpty(slaTagsQueryReqDto.getUpdateStartTime()) && StringUtils.isNotEmpty(slaTagsQueryReqDto.getUpdateEndTime())) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(slaTagsQueryReqDto.getUpdateStartTime()).to(slaTagsQueryReqDto.getUpdateEndTime()));
            }
            //表达式
            List<String> tagList = slaTagsQueryReqDto.getTagList();
            if (StringUtils.isNotEmpty(tagList)) {
                BoolQueryBuilder tagBoolQueryBuilder = QueryBuilders.boolQuery();
                for (String tag : tagList) {
                    tagBoolQueryBuilder.should(QueryBuilders.matchQuery("tags", tag).operator(Operator.AND));
                }
                boolQueryBuilder.filter(tagBoolQueryBuilder);
            }
            log.info("boolQueryBuilder:{}", boolQueryBuilder);
            long boolQueryBuilderEnd = System.currentTimeMillis();
            log.info("构建BoolQueryBuilder耗时 {}ms, index: {}", boolQueryBuilderEnd - boolQueryBuilderStart);
            int pageNo = slaTagsQueryReqDto.getPageNum();
            int pageSize = slaTagsQueryReqDto.getPageSize();
            List<SortDescriptor> sortDescriptorList = slaTagsQueryReqDto.getSortDescriptorList();
            List<String> sorts = new ArrayList<>();
            List<String> orders = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(sortDescriptorList)) {
                sorts = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getField())) {
                        throw new ServiceException("排序参数异常");
                    }
                    return it.getField();
                }).collect(Collectors.toList());
                orders = sortDescriptorList.stream().map(it-> {
                    if (StringUtils.isEmpty(it.getDirection())) {
                        throw new ServiceException("排序参数异常");
                    }
                    return it.getDirection();
                }).collect(Collectors.toList());
            }
            IPage<SlaTagsQueryRspDto> page = ticketDataESService.slaTagsQuery(index, boolQueryBuilder, pageNo, pageSize, Boolean.FALSE, sorts, orders);
            List<SlaTagsQueryRspDto> queryRspDtoList = Lists.newArrayList();
            if (null != page) {
                queryRspDtoList = (List)page.getData();
                remoteTableDataInfo.setTotal(page.getTotalRecordCount());
            }
            remoteTableDataInfo.setRows(queryRspDtoList);
        } catch (Exception e) {
            log.error("查询工单列表异常:{}",e);
            return RemoteTableDataInfo.fail(Integer.valueOf(BizResponseEnums.QUERY_ERROR.getCode()),e.getMessage());
        }
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return remoteTableDataInfo;
    }

    @Override
    public Response<Long> busiQueryCount(BusiQueryReqDto busiQueryReqDto, String userType, String userId, String userName) {
        if (StringUtils.isAnyEmpty(userType, userId, userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "登入账号信息为空");
        }
        if (Objects.isNull(busiQueryReqDto.getUserDealType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "用户处理类型为空");
        }
        if (CollectionUtils.isEmpty(busiQueryReqDto.getAppIdList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务id不能为空");
        }
        String updateStartTime = busiQueryReqDto.getUpdateStartTime();
        String updateEndTime = busiQueryReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) || StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和更新结束时间必须同时传值");
        }
        if (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 365)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚1年");
            }
        }
        BusiQueryUserDealTypeEnum userDealType = busiQueryReqDto.getUserDealType();
        //根据查询条件构造es的BoolQueryBuilder
        long start = System.currentTimeMillis();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //业务ID
        List<String> queryAppIdList = busiQueryReqDto.getAppIdList();
        if (CollectionUtils.isNotEmpty(queryAppIdList)) {
            BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
            appIdBoolQueryBuilder.should(QueryBuilders.termsQuery("app_id.keyword", queryAppIdList));
            for (String appId : queryAppIdList) {
                appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));
            }
            boolQueryBuilder.filter(appIdBoolQueryBuilder);
        }
        if (StringUtils.isNotEmpty(busiQueryReqDto.getExtend1())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("extend1", busiQueryReqDto.getExtend1()).operator(Operator.AND));
        }
        //更新时间
        if (Objects.nonNull(busiQueryReqDto.getUpdateStartTime()) && Objects.nonNull(busiQueryReqDto.getUpdateEndTime())) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("update_time").from(busiQueryReqDto.getUpdateStartTime()).to(busiQueryReqDto.getUpdateEndTime()));
        }
        //表达式
        List<List<BusiCompareInfo>> conditions = busiQueryReqDto.getBusiCompareInfoList();
        if (StringUtils.isNotEmpty(conditions)) {
            BoolQueryBuilder orBoolQuery = QueryBuilders.boolQuery();
            for (List<BusiCompareInfo> orConditions : conditions) {
                BoolQueryBuilder andBoolQuery = QueryBuilders.boolQuery();
                for (BusiCompareInfo andCondition : orConditions) {
                    String compareId = andCondition.getCompareId();
                    BusiCompareType compareType = andCondition.getCompareType();
                    Object compareValue = andCondition.getCompareValue();
                    String compareValueStr = String.valueOf(compareValue);
                    List<String> compareValueList = Arrays.asList(compareValueStr.split(","));
                    switch (compareType) {
                        case CONTAIN_ANY:
                            for (String value : compareValueList) {
                                andBoolQuery.should(QueryBuilders.matchQuery(compareId, value).operator(Operator.AND));
                            }
                            break;
                        case NOT_CONTAIN_ANY:
                            for (String value : compareValueList) {
                                andBoolQuery.mustNot(QueryBuilders.matchQuery(compareId, value).operator(Operator.AND));
                            }
                            break;
                        case BELONG_TO:
                            andBoolQuery.should(QueryBuilders.termsQuery(compareId, compareValueList));
                            break;
                        case NOT_BELONG_TO:
                            andBoolQuery.mustNot(QueryBuilders.termsQuery(compareId, compareValueList));
                        default:
                            throw new NotImplementedException(String.format("未实现的比较类型:%s", compareType.getCode()));
                    }
                }
                orBoolQuery.should(andBoolQuery);
            }
            orBoolQuery.minimumShouldMatch(1);
            boolQueryBuilder.filter(orBoolQuery);
        }
        switch (userDealType) {
            //待分配的
            case MY_DEPT_APPLY_WAITING_DISPATCH:
                //工单状态
                if (CollectionUtils.isNotEmpty(busiQueryReqDto.getTicketStatusList())) {
                    boolQueryBuilder.must(QueryBuilders.termsQuery("ticket_status.keyword", busiQueryReqDto.getTicketStatusList()));
                }
                //受理人
                boolQueryBuilder.must(QueryBuilders.matchQuery("current_deal_users", "工单处理池").operator(Operator.AND));
                break;
            //待处理的
            case ALL_APPLYING:
                //状态为 处理中
                boolQueryBuilder.must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.APPLYING.getCode()));
                break;
            default:
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("无效的处理类型:%s", userDealType));
        }
        log.info("busiQueryCount 的 boolQueryBuilder:{}", boolQueryBuilder);
        Response<Long> response = ticketDataESService.fuzzyQueryCount(index, boolQueryBuilder);
        long end = System.currentTimeMillis();
        log.info("查询流程总耗时 {}ms", end - start);
        return response;
    }

    @Override
    public Response processStageQueryCount (ProcessStageCountReqDto processStageCountReqDto, String userType, String userId, String userName) {

        long start = System.currentTimeMillis();
        //校验传参
        if (Objects.isNull(processStageCountReqDto)
                || StringUtils.isNull(processStageCountReqDto.getAppId())
                || StringUtils.isEmpty(userType)
                || StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "查询条件：用户信息或appid为空");
        }
        //校验是否可以获取到 sameOriginId
        TicketRemoteAccountDto ticketRemoteAccount = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
        if (Objects.isNull(ticketRemoteAccount) || StringUtils.isEmpty(ticketRemoteAccount.getSameOriginId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户userId:%s,userType:%s", userId, userType));
        }
        String sameOriginId = ticketRemoteAccount.getSameOriginId();

        String appId = processStageCountReqDto.getAppId();
        BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
        appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
        appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));

        //待我处理的
        BoolQueryBuilder pendingCountBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder)
                .must(QueryBuilders.matchQuery("current_deal_users", sameOriginId).operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d"))
                .must(QueryBuilders.termQuery("ticket_status.keyword", "APPLYING"))
                .mustNot(QueryBuilders.existsQuery("delete_time"));

        //我处理的
        BoolQueryBuilder myDealAllBoolQueryBuilder = QueryBuilders.boolQuery();
        myDealAllBoolQueryBuilder.should(QueryBuilders.matchQuery("current_done_users", sameOriginId).operator(Operator.AND))
                .should(QueryBuilders.matchQuery("current_deal_users", sameOriginId).operator(Operator.AND))
                .should(QueryBuilders.matchQuery("current_cc_users", sameOriginId).operator(Operator.AND));
        BoolQueryBuilder ongoingCountBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder)
                .must(myDealAllBoolQueryBuilder)
                .must(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d"))
                .mustNot(QueryBuilders.existsQuery("delete_time"));

        //经过我：已完成
        BoolQueryBuilder completedCountBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder)
                .must(QueryBuilders.matchQuery("current_done_users", sameOriginId).operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d"))
                .must(QueryBuilders.termQuery("ticket_status.keyword", "APPLY_END"))
                .mustNot(QueryBuilders.existsQuery("delete_time"));

        log.info("待我处理工单的QueryBuilder：{}", pendingCountBoolQuery);
        log.info("经过我的，处理中工单的QueryBuilder：{}", ongoingCountBoolQuery);
        log.info("经过我的，处理完成的QueryBuilder：{}", completedCountBoolQuery);

        Long pendingCount = ticketDataESService.count(index, pendingCountBoolQuery);
        Long ongoingCount = ticketDataESService.count(index, ongoingCountBoolQuery);
        Long completedCount = ticketDataESService.count(index, completedCountBoolQuery);

        ProcessStageCountRespDto respDto = new ProcessStageCountRespDto();
        respDto.setPendingCount(pendingCount);
        respDto.setOngoingCount(ongoingCount);
        respDto.setCompletedCount(completedCount);

        long end = System.currentTimeMillis();
        log.info("统计 processStageQueryCount 总耗时 {} ms", end - start);
        return new Response<ProcessStageCountRespDto>().success(respDto);
    }

    @Override
    public Response<SubmitStageCountRespDto> submitStageQueryCount (SubmitStageCountReqDto submitStageCountReqDto, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        //校验传参
        if (Objects.isNull(submitStageCountReqDto)
                || StringUtils.isNull(submitStageCountReqDto.getAppId())
                || StringUtils.isEmpty(userType)
                || StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "请检查查询条件、用户信息、appid 等是否为空");
        }
        //校验是否可以获取到 sameOriginId
        TicketRemoteAccountDto ticketRemoteAccount = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
        if (Objects.isNull(ticketRemoteAccount) || StringUtils.isEmpty(ticketRemoteAccount.getSameOriginId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户userId:%s,userType:%s", userId, userType));
        }

        String appId = submitStageCountReqDto.getAppId();
        BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
        appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
        appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));

        //根据查询条件构造 es 的 BoolQueryBuilder
        BoolQueryBuilder newCountBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder)
                .must(QueryBuilders.matchQuery("apply_user", ticketRemoteAccount.getSameOriginId()).operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d"))
                .mustNot(QueryBuilders.existsQuery("delete_time"));

        BoolQueryBuilder returnedCountBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder)
                .must(QueryBuilders.matchQuery("current_deal_users", ticketRemoteAccount.getSameOriginId()).operator(Operator.AND))
                .must(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d"))
                .must(QueryBuilders.termQuery("ticket_status.keyword", TicketDataStatusEnum.BACK.getCode()))
                .mustNot(QueryBuilders.existsQuery("delete_time"));

        log.info("新建工单的QueryBuilder：{}",newCountBoolQuery);
        log.info("退回工单的QueryBuilder：{}",returnedCountBoolQuery);

        Long newCount = ticketDataESService.count(index, newCountBoolQuery);
        Long returnedCount = ticketDataESService.count(index, returnedCountBoolQuery);

        SubmitStageCountRespDto respDto = new SubmitStageCountRespDto();
        respDto.setNewCount(newCount);
        respDto.setReturnedCount(returnedCount);

        long end = System.currentTimeMillis();
        log.info("统计 submitStageQueryCount 总耗时 {} ms", end - start);
        return new Response<SubmitStageCountRespDto>().success(respDto);
    }

    @Override
    public Response<TopRankingRespDto> getTopRanking (TopRankingReqDto topRankingReqDto, String userType, String userId, String userName) {

        long start = System.currentTimeMillis();
        //校验传参
        if (Objects.isNull(topRankingReqDto)
                || StringUtils.isNull(topRankingReqDto.getAppId())
                || CollectionUtils.isEmpty(topRankingReqDto.getTopGeneralList())
                || CollectionUtils.isEmpty(topRankingReqDto.getTopSeniorList())
                || StringUtils.isEmpty(userType)
                || StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(userName)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "查询条件:用户信息、appid或者用户列表等是否为空");
        }
        //校验是否可以获取到 sameOriginId
        TicketRemoteAccountDto ticketRemoteAccount = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
        if (Objects.isNull(ticketRemoteAccount) || StringUtils.isEmpty(ticketRemoteAccount.getSameOriginId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户userId:%s,userType:%s", userId, userType));
        }
        String topUserType = topRankingReqDto.getUserType();
        List<TopRankingUser> topGeneralList = topRankingReqDto.getTopGeneralList();
        List<String> topGeneralUserIdList = topGeneralList.stream().map(it->it.getUserId()).collect(Collectors.toList());
        List<TicketAccountMapping> topGeneralAccountMappingList = ticketAccountMappingService.lambdaQuery()
                .isNull(TicketAccountMapping::getDeleteTime)
                .in(TicketAccountMapping::getAccountId, topGeneralUserIdList)
                .eq(TicketAccountMapping::getAccountType, topUserType).list();
        if (CollectionUtils.isEmpty(topGeneralAccountMappingList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户列表userIdList:%s,userType:%s", JSONObject.toJSONString(topGeneralUserIdList), topUserType));
        }
        List<String> topGeneralSameOriginIdList = topGeneralAccountMappingList.stream().map(it->it.getSameOriginId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(topGeneralSameOriginIdList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据userIdList:%s,userType:%s查出的sameOriginId为空", JSONObject.toJSONString(topGeneralUserIdList), topUserType));
        }

        List<TopRankingUser> topSeniorList = topRankingReqDto.getTopSeniorList();
        List<String> topSeniorUserIdList = topSeniorList.stream().map(it->it.getUserId()).collect(Collectors.toList());
        List<TicketAccountMapping> topSeniorAccountMappingList = ticketAccountMappingService.lambdaQuery()
                .isNull(TicketAccountMapping::getDeleteTime)
                .in(TicketAccountMapping::getAccountId, topSeniorUserIdList)
                .eq(TicketAccountMapping::getAccountType, topUserType).list();
        if (CollectionUtils.isEmpty(topSeniorAccountMappingList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户列表userIdList:%s,userType:%s", JSONObject.toJSONString(topSeniorUserIdList), topUserType));
        }
        List<String> topSeniorSameOriginIdList = topSeniorAccountMappingList.stream().map(it->it.getSameOriginId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(topSeniorSameOriginIdList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据userIdList:%s,userType:%s查出的sameOriginId为空", JSONObject.toJSONString(topGeneralUserIdList), topUserType));
        }

        String appId = topRankingReqDto.getAppId();
        BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
        appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
        appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));

        // 普诉
        BoolQueryBuilder generalBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder) // 匹配 app_id
                .must(QueryBuilders.termsQuery("apply_user_sameoriginid.keyword", topGeneralSameOriginIdList)) // 过滤多个 apply_user
                .filter(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d")); // 时间范围过滤
        TermsAggregationBuilder generalAgg = AggregationBuilders
                .terms("general_top_apply_users")
                .field("apply_user_sameoriginid.keyword")
                .size(10)
                .order(BucketOrder.count(false));
        Map<String, Long> topRankGeneralMap = ticketDataESService.getTermsAggBucketsToMap(index, "general_top_apply_users", 10, generalBoolQuery, generalAgg);
        List<TopRankingUser> topRankGeneralList = topRankGeneralMap.entrySet().stream()
                .map(entry -> new TopRankingUser(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        //资深
        BoolQueryBuilder seniorBoolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder) // 匹配 app_id
                .must(QueryBuilders.termsQuery("apply_user_sameoriginid.keyword", topSeniorSameOriginIdList)) // 过滤多个 apply_user
                .filter(QueryBuilders.rangeQuery("create_time").gte("now/d").lt("now+1d/d")); // 时间范围过滤
        TermsAggregationBuilder seniorAgg = AggregationBuilders
                .terms("senior_top_apply_users")
                .field("apply_user_sameoriginid.keyword")
                .size(10)
                .order(BucketOrder.count(false));
        Map<String, Long> topRankSeniorMap = ticketDataESService.getTermsAggBucketsToMap(index, "senior_top_apply_users", 10, seniorBoolQuery, seniorAgg);
        List<TopRankingUser> topRankSeniorList = topRankSeniorMap.entrySet().stream()
                .map(entry -> new TopRankingUser(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        log.info("普诉QueryBuilder:{}", generalBoolQuery);
        log.info("资深QueryBuilder:{}", seniorBoolQuery);

        TopRankingRespDto respDto = new TopRankingRespDto();
        respDto.setTopGeneralList(topRankGeneralList);
        respDto.setTopSeniorList(topRankSeniorList);

        long end = System.currentTimeMillis();
        log.info("统计排名 总耗时 {} ms", end - start);
        return new Response<TopRankingRespDto>().success(respDto);
    }

    @Override
    public Response<TicketDispatchCountRspDto> getTicketDispatchCountList(TicketDispatchCountReqDto ticketDispatchCountReqDto, String userType, String userId, String userName) {
        long start = System.currentTimeMillis();
        //校验传参
        if (Objects.isNull(ticketDispatchCountReqDto) || Objects.isNull(ticketDispatchCountReqDto.getAccountType()) || CollectionUtils.isEmpty(ticketDispatchCountReqDto.getAccountIdList())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数异常");
        }
        //校验是否可以获取到 sameOriginId
        TicketRemoteAccountDto ticketRemoteAccount = ticketAccountService.getTicketRemoteAccountByIdAndType(userId, userType);
        if (Objects.isNull(ticketRemoteAccount) || StringUtils.isEmpty(ticketRemoteAccount.getSameOriginId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户userId:%s,userType:%s", userId, userType));
        }
        //校验是否可以获取到 sameOriginId
        String accountType = ticketDispatchCountReqDto.getAccountType();
        List<String> accountIdList = ticketDispatchCountReqDto.getAccountIdList();
        List<TicketAccountMapping> ticketAccountMappingList = ticketAccountMappingService.lambdaQuery()
                .isNull(TicketAccountMapping::getDeleteTime)
                .in(TicketAccountMapping::getAccountId, accountIdList)
                .eq(TicketAccountMapping::getAccountType, accountType).list();
        if (CollectionUtils.isEmpty(ticketAccountMappingList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("不存在的账户列表userIdList:%s,userType:%s", JSONObject.toJSONString(accountIdList), accountType));
        }
        //sameOriginId和accountId的映射
        Map<String,String> sameOriginIdUserIdMap = ticketAccountMappingList.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getSameOriginId()))
                .collect(Collectors.toMap(TicketAccountMapping::getSameOriginId, TicketAccountMapping::getAccountId));

        List<String> sameOriginIdList = ticketAccountMappingList.stream().map(it->it.getSameOriginId()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sameOriginIdList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("根据userIdList:%s,userType:%s查出的原账号列表为空", JSONObject.toJSONString(accountIdList), accountType));
        }

        String updateStartTime = ticketDispatchCountReqDto.getUpdateStartTime();
        String updateEndTime = ticketDispatchCountReqDto.getUpdateEndTime();
        if ((StringUtils.isEmpty(updateStartTime) || StringUtils.isEmpty(updateEndTime))) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION,"更新开始时间和更新结束时间必须同时传值");
        }
        if (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            Date updateStartTimeDate = DateUtil.parse(updateStartTime, "yyyy-MM-dd HH:mm:ss");
            Date updateEndTimeDate = DateUtil.parse(updateEndTime, "yyyy-MM-dd HH:mm:ss");
            if ((updateStartTimeDate.after(updateEndTimeDate) || DateUtil.between(updateStartTimeDate, updateEndTimeDate, DateUnit.DAY) > 365)) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新时间的终止时间比开始时间最多晚1年");
            }
        }

        String appId = ticketDispatchCountReqDto.getAppId();
        BoolQueryBuilder appIdBoolQueryBuilder = QueryBuilders.boolQuery();
        appIdBoolQueryBuilder.should(QueryBuilders.termQuery("app_id.keyword", appId));
        appIdBoolQueryBuilder.should(QueryBuilders.matchQuery("beyond_apps", appId).operator(Operator.AND));

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(appIdBoolQueryBuilder) // 匹配 app_id
                .must(QueryBuilders.termsQuery("one_current_deal_user_sameoriginid.keyword", sameOriginIdList));
        if (StringUtils.isNotEmpty(updateStartTime) && StringUtils.isNotEmpty(updateEndTime)) {
            boolQuery.must(QueryBuilders.rangeQuery("update_time").from(updateStartTime).to(updateEndTime));
        }
        log.info("查询待分配QueryBuilder:{}", boolQuery);

        TermsAggregationBuilder agg = AggregationBuilders
                .terms("current_deal_users_count")
                .field("one_current_deal_user_sameoriginid.keyword")
                .order(BucketOrder.count(false));
        Map<String, Long> currentDealUsersCountMap = ticketDataESService.getTermsAggBucketsToMap(index, "current_deal_users_count", 0, boolQuery, agg);
        List<TicketDispatchCountDto> ticketDispatchCountDtoList = currentDealUsersCountMap.entrySet().stream()
                .map(entry -> new TicketDispatchCountDto(sameOriginIdUserIdMap.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
        long end = System.currentTimeMillis();
        log.info("按照账户统计待分配的工单，总耗时 {} ms", end - start);
        TicketDispatchCountRspDto ticketDispatchCountRspDto = new TicketDispatchCountRspDto();
        ticketDispatchCountRspDto.setTicketDispatchCountDtoList(ticketDispatchCountDtoList);
        return Response.success(ticketDispatchCountRspDto);
    }


    /**
     * 查询工单后对接口的后置处理
     *
     * @param ticketDataList
     */
    private List<DownloadTicketDataRespDto> getDownloadTicketDataDtoList(List<TicketData> ticketDataList) {
        if (CollUtil.isEmpty(ticketDataList)) {
            return new ArrayList<>();
        }
        List<String> appIdList = ticketDataList.stream().map(TicketData::getAppId).collect(Collectors.toList());
        Map<String, String> appIdNameMap = ticketAppService.selectNameMapByIdList(appIdList);

        List<String> templateIdList = ticketDataList.stream().map(TicketData::getTemplateId).collect(Collectors.toList());
        Map<String, String> templateIdNameMap = ticketTemplateService.selectNameMapByIdList(templateIdList);

        List<DownloadTicketDataRespDto> downloadTicketDataRespDtoList = new ArrayList<>();
        for (TicketData ticketData : ticketDataList) {
            DownloadTicketDataRespDto downloadTicketDataRespDto = new DownloadTicketDataRespDto(ticketData);
            downloadTicketDataRespDto.setApplyUser(accountReturnComponent.toAccountInfoStrForFront(downloadTicketDataRespDto.getApplyUser()));
            downloadTicketDataRespDto.setCurrentDealUsers(accountReturnComponent.toAccountInfoStrForFront(downloadTicketDataRespDto.getCurrentDealUsers()));
            downloadTicketDataRespDto.setAppName(appIdNameMap.getOrDefault(downloadTicketDataRespDto.getAppName(), ""));
            downloadTicketDataRespDto.setTicketTemplateName(templateIdNameMap.getOrDefault(downloadTicketDataRespDto.getTicketTemplateName(), ""));
            downloadTicketDataRespDtoList.add(downloadTicketDataRespDto);
        }
        return downloadTicketDataRespDtoList;
    }
}