package com.smy.tfs.biz.controller;

import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.TicketDataListRequestDto;
import com.smy.tfs.api.dto.TicketDataListResponseDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.enums.UserDealTypeEnum;
import com.smy.tfs.api.service.IPostloanService;
import com.smy.tfs.api.service.ITicketDataQueryService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.page.TableDataInfo;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@RestController
@ResponseBody
public class TicketDataQueryController extends BaseController {

    @Resource
    private ITicketDataQueryService ticketDataQueryService;
    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private IPostloanService postloanService;

    @Value("${fuzzyQueryFlag:true}")
    private  Boolean fuzzyQueryFlag ;

    @Value("${pcQueryFlag:true}")
    private  Boolean pcQueryFlag ;

    @PostMapping({"/test/callback"})
    public Object callbackTest(@RequestBody TicketDataDto dataDto) {
        return postloanService.autoAssignToRecentHandlerCallback(dataDto);

    }

    /**
     * 模糊查询工单数据(适用于jssdk根据工单通用字段及模版字段模糊查询工单列表)
     * @param fuzzyQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/fuzzyQuery","/outside/ticketDataQuery/fuzzyQuery"})
    public TableDataInfo fuzzyQuery(@RequestBody FuzzyQueryReqDto fuzzyQueryReqDto){
        fuzzyQueryReqDto.setNeedCount(Boolean.FALSE);
        String sameOriginId = getLoginUser().getSameOriginId();
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        if (!fuzzyQueryFlag) {
            logger.info("路由到数据库查询:开关({}),{}", fuzzyQueryFlag, fuzzyQueryReqDto);
            TicketDataListRequestDto ticketDataListRequestDto = getTicketDataListRequestDto(fuzzyQueryReqDto);
            Response<List<TicketDataListResponseDto>> response = ticketDataService.selectTicketDataList(ticketDataListRequestDto);
            TableDataInfo tableDataInfo = getDataTable(response.getData());
            return tableDataInfo;
        }
        logger.info("路由到es查询:开关({}),{}", fuzzyQueryFlag, fuzzyQueryReqDto);
        return ticketDataQueryService.fuzzyQuery(fuzzyQueryReqDto, sameOriginId, userType, userId, userName);
    }

    @PostMapping({"/ticketDataQuery/fuzzyQueryCount","/outside/ticketDataQuery/fuzzyQueryCount"})
    public AjaxResult fuzzyQueryCount(@RequestBody FuzzyQueryReqDto fuzzyQueryReqDto){
        fuzzyQueryReqDto.setNeedCount(Boolean.FALSE);
        String sameOriginId = getLoginUser().getSameOriginId();
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        if (!fuzzyQueryFlag) {
            logger.info("路由到数据库查询:开关({}),{}", fuzzyQueryFlag, fuzzyQueryReqDto);
            TicketDataListRequestDto ticketDataListRequestDto = getTicketDataListRequestDto(fuzzyQueryReqDto);
            Integer count = ticketDataService.selectTicketDataCount(ticketDataListRequestDto);
            return AjaxResult.success(count);
        }
        logger.info("路由到es查询:开关({}),{}", fuzzyQueryFlag, fuzzyQueryReqDto);
        Response<Long> response =  ticketDataQueryService.fuzzyQueryCount(fuzzyQueryReqDto, sameOriginId, userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 精确查询工单数据(适用于jssdk根据工单通用字段及模版字段精确查询工单列表)
     * @param accurateQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/accurateQuery","/outside/ticketDataQuery/accurateQuery"})
    public TableDataInfo accurateQuery(@RequestBody AccurateQueryReqDto accurateQueryReqDto){
        return  ticketDataQueryService.accurateQuery(accurateQueryReqDto);
    }



    /**
     * 查询工单列表数据（超管）
     * @param superAdminQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/superAdminQuery","/outside/ticketDataQuery/superAdminQuery"})
    public TableDataInfo superAdminQuery(@RequestBody SuperAdminQueryReqDto superAdminQueryReqDto){
        superAdminQueryReqDto.setNeedCount(Boolean.FALSE);
        String sameOriginId = getLoginUser().getSameOriginId();
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        if (!pcQueryFlag) {
            logger.info("路由到数据库查询:开关({}),{}", pcQueryFlag, superAdminQueryReqDto);
            TicketDataListRequestDto ticketDataListRequestDto = getTicketDataListRequestDto(superAdminQueryReqDto);
            Response<List<TicketDataListResponseDto>> response = ticketDataService.selectTicketDataList(ticketDataListRequestDto, superAdminQueryReqDto.getPageNum(),superAdminQueryReqDto.getPageSize());
            TableDataInfo tableDataInfo = getDataTable(response.getData());
            return tableDataInfo;
        }
        logger.info("路由到es查询:开关({}),{}", pcQueryFlag, superAdminQueryReqDto);
        return  ticketDataQueryService.superAdminQuery(superAdminQueryReqDto, sameOriginId, userType, userId, userName);
    }


    /**
     * 查询工单列表数据（业管）
     * @param busiAdminQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/busiAdminQuery","/outside/ticketDataQuery/busiAdminQuery"})
    public TableDataInfo busiAdminQuery(@RequestBody BusiAdminQueryReqDto busiAdminQueryReqDto){
        busiAdminQueryReqDto.setNeedCount(Boolean.FALSE);
        String sameOriginId = getLoginUser().getSameOriginId();
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        if (!pcQueryFlag) {
            logger.info("路由到数据库查询:开关({}),{}", pcQueryFlag, busiAdminQueryReqDto);
            TicketDataListRequestDto ticketDataListRequestDto = getTicketDataListRequestDto(busiAdminQueryReqDto);
            Response<List<TicketDataListResponseDto>> response = ticketDataService.selectTicketDataList(ticketDataListRequestDto, busiAdminQueryReqDto.getPageNum(), busiAdminQueryReqDto.getPageSize());
            TableDataInfo tableDataInfo = getDataTable(response.getData());
            return tableDataInfo;
        }
        logger.info("路由到es查询:开关({}),{}", pcQueryFlag, busiAdminQueryReqDto);
        return  ticketDataQueryService.busiAdminQuery(busiAdminQueryReqDto, sameOriginId, userType, userId, userName);
    }

    /**
     * 查询工单列表数据（我审批的/我处理的）
     * @param ownQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/ownQuery","/outside/ticketDataQuery/ownQuery"})
    public TableDataInfo ownQuery(@RequestBody OwnQueryReqDto ownQueryReqDto){
        ownQueryReqDto.setNeedCount(Boolean.FALSE);
        String sameOriginId = getLoginUser().getSameOriginId();
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        if (!pcQueryFlag) {
            logger.info("路由到数据库查询:开关({}),{}", pcQueryFlag, ownQueryReqDto);
            TicketDataListRequestDto ticketDataListRequestDto = getTicketDataListRequestDto(ownQueryReqDto);
            Response<List<TicketDataListResponseDto>> response = ticketDataService.selectTicketDataList(ticketDataListRequestDto, ownQueryReqDto.getPageNum(), ownQueryReqDto.getPageSize());
            TableDataInfo tableDataInfo = getDataTable(response.getData());
            return tableDataInfo;
        }
        logger.info("路由到es查询:开关({}),{}", pcQueryFlag, ownQueryReqDto);
        return  ticketDataQueryService.ownQuery(ownQueryReqDto, sameOriginId, userType, userId, userName);
    }


    /**
     * 查询工单列表数据（方舟）
     * @param busiQueryReqDto
     * @return
     */
    @PostMapping({"/ticketDataQuery/busiQuery","/outside/ticketDataQuery/busiQuery"})
    public RemoteTableDataInfo busiQuery(@RequestBody BusiQueryReqDto busiQueryReqDto){
        String userType = getLoginUser().getUserType();
        String userId = getLoginUser().getUsername();
        String userName = getLoginUser().getNickName();
        logger.info("es查询:{}", busiQueryReqDto);
        return  ticketDataQueryService.busiQuery(busiQueryReqDto, userType, userId, userName);
    }

    private TicketDataListRequestDto getTicketDataListRequestDto(SuperAdminQueryReqDto superAdminQueryReqDto){
        TicketDataListRequestDto ticketDataListRequestDto = new TicketDataListRequestDto();
        ticketDataListRequestDto.setNeedCount(superAdminQueryReqDto.isNeedCount());
        ticketDataListRequestDto.setAppIdList(superAdminQueryReqDto.getAppIdList());
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getAppIdList()) && superAdminQueryReqDto.getAppIdList().size() == 1) {
            ticketDataListRequestDto.setAppId(superAdminQueryReqDto.getAppIdList().get(0));
        }
        ticketDataListRequestDto.setTemplateIdList(superAdminQueryReqDto.getTemplateIdList());
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getTemplateIdList()) && superAdminQueryReqDto.getTemplateIdList().size() == 1) {
            ticketDataListRequestDto.setTemplateId(superAdminQueryReqDto.getTemplateIdList().get(0));
        }
        ticketDataListRequestDto.setTicketStatusStrList(superAdminQueryReqDto.getTicketStatusList());
        ticketDataListRequestDto.setSearchValue(superAdminQueryReqDto.getSearchValue());
        ticketDataListRequestDto.setApplyUser(superAdminQueryReqDto.getApplyUser());
        ticketDataListRequestDto.setCurrentDealUser(superAdminQueryReqDto.getCurrentDealUser());
        if (CollectionUtils.isNotEmpty(superAdminQueryReqDto.getCategoryIdList())) {
            List<Integer> categoryIdList = superAdminQueryReqDto.getCategoryIdList();
            List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
            ticketDataListRequestDto.setCategoryIdListStr(String.join(",", categoryIdStrList));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getUpdateStartTime())) {
            ticketDataListRequestDto.setUpdateStartTime(DateUtils.parseDate(superAdminQueryReqDto.getUpdateStartTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getUpdateEndTime())) {
            ticketDataListRequestDto.setUpdateEndTime(DateUtils.parseDate(superAdminQueryReqDto.getUpdateEndTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getCreateStartTime())) {
            ticketDataListRequestDto.setCreateStartTime(DateUtils.parseDate(superAdminQueryReqDto.getCreateStartTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getCreateEndTime())) {
            ticketDataListRequestDto.setCreateEndTime(DateUtils.parseDate(superAdminQueryReqDto.getCreateEndTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getFinishStartTime())) {
            ticketDataListRequestDto.setFinishStartTime(DateUtils.parseDate(superAdminQueryReqDto.getFinishStartTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getFinishEndTime())) {
            ticketDataListRequestDto.setFinishEndTime(DateUtils.parseDate(superAdminQueryReqDto.getFinishEndTime()));
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend1())) {
            ticketDataListRequestDto.setExtend1(superAdminQueryReqDto.getExtend1());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend2())) {
            ticketDataListRequestDto.setExtend2(superAdminQueryReqDto.getExtend2());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend3())) {
            ticketDataListRequestDto.setExtend3(superAdminQueryReqDto.getExtend3());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend4())) {
            ticketDataListRequestDto.setExtend4(superAdminQueryReqDto.getExtend4());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend5())) {
            ticketDataListRequestDto.setExtend5(superAdminQueryReqDto.getExtend5());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend6())) {
            ticketDataListRequestDto.setExtend6(superAdminQueryReqDto.getExtend6());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend7())) {
            ticketDataListRequestDto.setExtend7(superAdminQueryReqDto.getExtend7());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend8())) {
            ticketDataListRequestDto.setExtend8(superAdminQueryReqDto.getExtend8());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend9())) {
            ticketDataListRequestDto.setExtend9(superAdminQueryReqDto.getExtend9());
        }
        if (StringUtils.isNotEmpty(superAdminQueryReqDto.getExtend10())) {
            ticketDataListRequestDto.setExtend10(superAdminQueryReqDto.getExtend10());
        }
        logger.info("路由到数据库查询:开关({}),实际入参：{}", pcQueryFlag, ticketDataListRequestDto);
        return ticketDataListRequestDto;
    }

    private TicketDataListRequestDto getTicketDataListRequestDto(BusiAdminQueryReqDto busiAdminQueryReqDto){
        TicketDataListRequestDto ticketDataListRequestDto = new TicketDataListRequestDto();
        ticketDataListRequestDto.setNeedControl(true);
        ticketDataListRequestDto.setNeedCount(busiAdminQueryReqDto.isNeedCount());
        ticketDataListRequestDto.setAppIdList(busiAdminQueryReqDto.getAppIdList());
        if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getAppIdList()) && busiAdminQueryReqDto.getAppIdList().size() == 1) {
            ticketDataListRequestDto.setAppId(busiAdminQueryReqDto.getAppIdList().get(0));
        }
        ticketDataListRequestDto.setTemplateIdList(busiAdminQueryReqDto.getTemplateIdList());
        if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getTemplateIdList()) && busiAdminQueryReqDto.getTemplateIdList().size() == 1) {
            ticketDataListRequestDto.setTemplateId(busiAdminQueryReqDto.getTemplateIdList().get(0));
        }
        ticketDataListRequestDto.setTicketStatusStrList(busiAdminQueryReqDto.getTicketStatusList());
        ticketDataListRequestDto.setSearchValue(busiAdminQueryReqDto.getSearchValue());
        ticketDataListRequestDto.setApplyUser(busiAdminQueryReqDto.getApplyUser());
        ticketDataListRequestDto.setCurrentDealUser(busiAdminQueryReqDto.getCurrentDealUser());
        if (CollectionUtils.isNotEmpty(busiAdminQueryReqDto.getCategoryIdList())) {
            List<Integer> categoryIdList = busiAdminQueryReqDto.getCategoryIdList();
            List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
            ticketDataListRequestDto.setCategoryIdListStr(String.join(",", categoryIdStrList));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getUpdateStartTime())) {
            ticketDataListRequestDto.setUpdateStartTime(DateUtils.parseDate(busiAdminQueryReqDto.getUpdateStartTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getUpdateEndTime())) {
            ticketDataListRequestDto.setUpdateEndTime(DateUtils.parseDate(busiAdminQueryReqDto.getUpdateEndTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getCreateStartTime())) {
            ticketDataListRequestDto.setCreateStartTime(DateUtils.parseDate(busiAdminQueryReqDto.getCreateStartTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getCreateEndTime())) {
            ticketDataListRequestDto.setCreateEndTime(DateUtils.parseDate(busiAdminQueryReqDto.getCreateEndTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getFinishStartTime())) {
            ticketDataListRequestDto.setFinishStartTime(DateUtils.parseDate(busiAdminQueryReqDto.getFinishStartTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getFinishEndTime())) {
            ticketDataListRequestDto.setFinishEndTime(DateUtils.parseDate(busiAdminQueryReqDto.getFinishEndTime()));
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend1())) {
            ticketDataListRequestDto.setExtend1(busiAdminQueryReqDto.getExtend1());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend2())) {
            ticketDataListRequestDto.setExtend2(busiAdminQueryReqDto.getExtend2());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend3())) {
            ticketDataListRequestDto.setExtend3(busiAdminQueryReqDto.getExtend3());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend4())) {
            ticketDataListRequestDto.setExtend4(busiAdminQueryReqDto.getExtend4());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend5())) {
            ticketDataListRequestDto.setExtend5(busiAdminQueryReqDto.getExtend5());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend6())) {
            ticketDataListRequestDto.setExtend6(busiAdminQueryReqDto.getExtend6());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend7())) {
            ticketDataListRequestDto.setExtend7(busiAdminQueryReqDto.getExtend7());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend8())) {
            ticketDataListRequestDto.setExtend8(busiAdminQueryReqDto.getExtend8());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend9())) {
            ticketDataListRequestDto.setExtend9(busiAdminQueryReqDto.getExtend9());
        }
        if (StringUtils.isNotEmpty(busiAdminQueryReqDto.getExtend10())) {
            ticketDataListRequestDto.setExtend10(busiAdminQueryReqDto.getExtend10());
        }
        logger.info("路由到数据库查询:开关({}),实际入参：{}", pcQueryFlag, ticketDataListRequestDto);
        return ticketDataListRequestDto;
    }

    private TicketDataListRequestDto getTicketDataListRequestDto(OwnQueryReqDto ownQueryReqDto){
        TicketDataListRequestDto ticketDataListRequestDto = new TicketDataListRequestDto();
        ticketDataListRequestDto.setNeedCount(ownQueryReqDto.isNeedCount());
        ticketDataListRequestDto.setAppIdList(ownQueryReqDto.getAppIdList());
        if (CollectionUtils.isNotEmpty(ownQueryReqDto.getAppIdList()) && ownQueryReqDto.getAppIdList().size() == 1) {
            ticketDataListRequestDto.setAppId(ownQueryReqDto.getAppIdList().get(0));
        }
        ticketDataListRequestDto.setTemplateIdList(ownQueryReqDto.getTemplateIdList());
        if (CollectionUtils.isNotEmpty(ownQueryReqDto.getTemplateIdList()) && ownQueryReqDto.getTemplateIdList().size() == 1) {
            ticketDataListRequestDto.setTemplateId(ownQueryReqDto.getTemplateIdList().get(0));
        }
        ticketDataListRequestDto.setTicketStatusStrList(ownQueryReqDto.getTicketStatusList());
        ticketDataListRequestDto.setSearchValue(ownQueryReqDto.getSearchValue());
        ticketDataListRequestDto.setApplyUser(ownQueryReqDto.getApplyUser());
        ticketDataListRequestDto.setCurrentDealUser(ownQueryReqDto.getCurrentDealUser());
        if (CollectionUtils.isNotEmpty(ownQueryReqDto.getCategoryIdList())) {
            List<Integer> categoryIdList = ownQueryReqDto.getCategoryIdList();
            List<String> categoryIdStrList = categoryIdList.stream().map(String::valueOf).collect(Collectors.toList());
            ticketDataListRequestDto.setCategoryIdListStr(String.join(",", categoryIdStrList));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getUpdateStartTime())) {
            ticketDataListRequestDto.setUpdateStartTime(DateUtils.parseDate(ownQueryReqDto.getUpdateStartTime()));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getUpdateEndTime())) {
            ticketDataListRequestDto.setUpdateEndTime(DateUtils.parseDate(ownQueryReqDto.getUpdateEndTime()));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getCreateStartTime())) {
            ticketDataListRequestDto.setCreateStartTime(DateUtils.parseDate(ownQueryReqDto.getCreateStartTime()));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getCreateEndTime())) {
            ticketDataListRequestDto.setCreateEndTime(DateUtils.parseDate(ownQueryReqDto.getCreateEndTime()));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getFinishStartTime())) {
            ticketDataListRequestDto.setFinishStartTime(DateUtils.parseDate(ownQueryReqDto.getFinishStartTime()));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getFinishEndTime())) {
            ticketDataListRequestDto.setFinishEndTime(DateUtils.parseDate(ownQueryReqDto.getFinishEndTime()));
        }
        UserDealTypeEnum userDealType = ownQueryReqDto.getUserDealType();
        if (Objects.isNull(userDealType)) {
            throw new ServiceException("用户处理类型不能为空");
        }
        switch (userDealType) {
            case MY_DEAL_ALL:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                break;
            case MY_DEAL_WAITING_HANDLE:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("WAITING_HANDLE");
                break;
            case MY_DEAL_HANDLED:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("HANDLED");
                break;
            case MY_DEAL_HAS_CC:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("HAS_CC");
                break;
            case MY_APPLY_ALL:
                ticketDataListRequestDto.setCreatedByMe(true);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                break;
            default:
                throw new ServiceException(String.format("用户处理类型(%s)不匹配",userDealType));
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend1())) {
            ticketDataListRequestDto.setExtend1(ownQueryReqDto.getExtend1());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend2())) {
            ticketDataListRequestDto.setExtend2(ownQueryReqDto.getExtend2());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend3())) {
            ticketDataListRequestDto.setExtend3(ownQueryReqDto.getExtend3());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend4())) {
            ticketDataListRequestDto.setExtend4(ownQueryReqDto.getExtend4());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend5())) {
            ticketDataListRequestDto.setExtend5(ownQueryReqDto.getExtend5());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend6())) {
            ticketDataListRequestDto.setExtend6(ownQueryReqDto.getExtend6());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend7())) {
            ticketDataListRequestDto.setExtend7(ownQueryReqDto.getExtend7());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend8())) {
            ticketDataListRequestDto.setExtend8(ownQueryReqDto.getExtend8());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend9())) {
            ticketDataListRequestDto.setExtend9(ownQueryReqDto.getExtend9());
        }
        if (StringUtils.isNotEmpty(ownQueryReqDto.getExtend10())) {
            ticketDataListRequestDto.setExtend10(ownQueryReqDto.getExtend10());
        }
        logger.info("路由到数据库查询:开关({}),实际入参：{}", pcQueryFlag, ticketDataListRequestDto);
        return ticketDataListRequestDto;
    }

    private TicketDataListRequestDto getTicketDataListRequestDto(FuzzyQueryReqDto fuzzyQueryReqDto){
        TicketDataListRequestDto ticketDataListRequestDto = new TicketDataListRequestDto();
        if (StringUtils.isNotEmpty(fuzzyQueryReqDto.getAppId())) {
            ticketDataListRequestDto.setAppId(fuzzyQueryReqDto.getAppId());
        }
        if (StringUtils.isNotEmpty(fuzzyQueryReqDto.getSearchValue())) {
            ticketDataListRequestDto.setSearchValue(fuzzyQueryReqDto.getSearchValue());
        }
        if (Objects.nonNull(fuzzyQueryReqDto.getTicketAccessParty())) {
            ticketDataListRequestDto.setTicketAccessParty(fuzzyQueryReqDto.getTicketAccessParty());
        }
        ticketDataListRequestDto.setNeedCount(false);
        UserDealTypeEnum userDealType = fuzzyQueryReqDto.getUserDealType();
        switch (userDealType) {
            case MY_DEAL_ALL:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                break;
            case MY_DEAL_WAITING_HANDLE:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("WAITING_HANDLE");
                break;
            case MY_DEAL_HANDLED:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("HANDLED");
                break;
            case MY_DEAL_HAS_CC:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(true);
                ticketDataListRequestDto.setTicketStatusForUserStr("HAS_CC");
                break;
            case MY_APPLY_ALL:
                ticketDataListRequestDto.setCreatedByMe(true);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                break;
            case MY_APPLY_APPLYING:
                ticketDataListRequestDto.setCreatedByMe(true);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                ticketDataListRequestDto.setTicketStatusStr("APPLYING");
                break;
            case MY_APPLY_PASS:
                ticketDataListRequestDto.setCreatedByMe(true);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                ticketDataListRequestDto.setTicketStatusStr("APPLY_END");
                break;
            case MY_APPLY_REJECT:
                ticketDataListRequestDto.setCreatedByMe(true);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                ticketDataListRequestDto.setTicketStatusStr("REJECT");
                break;
            case ALL_ALL:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                break;
            case ALL_APPLYING:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                ticketDataListRequestDto.setTicketStatusStr("APPLYING");
                break;
            case ALL_APPLYEND:
                ticketDataListRequestDto.setCreatedByMe(false);
                ticketDataListRequestDto.setNeedHandleByMe(false);
                ticketDataListRequestDto.setTicketStatusStr("APPLY_END");
                break;
            default:
                throw new RuntimeException(String.format("无效的处理类型:%s",userDealType));
        }
        logger.info("路由到数据库查询:开关({}),实际入参：{}", fuzzyQueryFlag, ticketDataListRequestDto);
        return ticketDataListRequestDto;
    }



}
