package com.smy.tfs.web.controller.test;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.dbo.TicketTemplate;
import com.smy.tfs.api.dto.BusiTicketDataRequestDto;
import com.smy.tfs.api.dto.BusiTicketDataResponseDto;
import com.smy.tfs.api.dto.SyncExtendFieldsDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.BusiCommonESQueryReqDto;
import com.smy.tfs.api.dto.query.BusiCommonESQueryRspDto;
import com.smy.tfs.api.dto.query.BusiESCompareInfo;
import com.smy.tfs.api.dto.query.RemoteTableDataInfo;
import com.smy.tfs.api.dto.test.TicketObjectDto;
import com.smy.tfs.api.dto.test.UpdateTicketDataDto;
import com.smy.tfs.api.enums.BusiESCompareType;
import com.smy.tfs.api.enums.FormItemAdvancedSearchEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.component.TicketAppComponent;
import com.smy.tfs.biz.component.TicketCategoryComponent;
import com.smy.tfs.biz.component.TicketTemplateComponent;
import com.smy.tfs.common.core.controller.BaseController;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/ticketdata/test")
@ResponseBody
public class TicketDataTestController extends BaseController {
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ITicketFormItemDataService ticketFormItemDataService;
    @Resource
    private ITicketTemplateService ticketTemplateService;

    @PostMapping({"/syncHistoryExtendFields"})
    public AjaxResult syncHistoryExtendFields(@RequestBody SyncExtendFieldsDto syncExtendFieldsDto) {
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery()
                .isNull(TicketTemplate::getDeleteTime)
                .eq(TicketTemplate::getTicketTemplateCode,syncExtendFieldsDto.getTicketTemplateCode())
                .list();
        if (CollectionUtils.isEmpty(ticketTemplateList)) {
            throw new ServiceException("根据模板code(%s)查出的模板为空");
        }
        String ticketTemplateId = ticketTemplateList.get(0).getId();
        ThreadUtil.execAsync(() -> {
            logger.info("同步历史催收模版公共扩展字段开始...");
            while (true) {
                PageInfo<TicketData> pageInfo = PageHelper.<TicketData>startPage(syncExtendFieldsDto.getPageNum(), syncExtendFieldsDto.getPageSize())
                        .doSelectPageInfo(() -> ticketDataService.lambdaQuery()
                                .isNull(TicketData::getDeleteTime)
                                .eq(TicketData::getTemplateId, ticketTemplateId)
                                .isNull(TicketData::getExtend6)
                                .list());
                if (null == pageInfo || CollectionUtils.isEmpty(pageInfo.getList())) {
                    logger.error("查询工单列表为空:{}", JSONObject.toJSONString(pageInfo));
                    break;
                }
                List<TicketData> ticketDataList = pageInfo.getList();
                for (TicketData ticketData : ticketDataList) {
                    String tickDataId = ticketData.getId();
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .isNull(TicketFormItemData::getDeleteTime)
                            .eq(TicketFormItemData::getTicketDataId, tickDataId)
                            .list();
                    if (CollectionUtils.isEmpty(ticketFormItemDataList)) {
                        continue;
                    }
                    for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
                        String itemLabel = ticketFormItemData.getItemLabel();
                        String itemValue = ticketFormItemData.getItemValue();
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("反馈渠道")) {
                            ticketData.setExtend6(itemValue);
                        }
                    }
                }
                ticketDataService.saveOrUpdateBatch(ticketDataList);
                logger.info("已同步完第{}页",syncExtendFieldsDto.getPageNum());
                syncExtendFieldsDto.setPageNum(syncExtendFieldsDto.getPageNum() + 1);
            }
            logger.info("同步历史催收模版公共扩展字段结束...");
        });
        return AjaxResult.success("同步历史催收模版公共扩展字段进行中...");
    }

    @PostMapping({"/syncKefuExtendFields"})
    public AjaxResult syncKefuExtendFields(@RequestBody SyncExtendFieldsDto syncExtendFieldsDto) {
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery()
                .isNull(TicketTemplate::getDeleteTime)
                .eq(TicketTemplate::getTicketTemplateCode,syncExtendFieldsDto.getTicketTemplateCode())
                .list();
        if (CollectionUtils.isEmpty(ticketTemplateList)) {
            throw new ServiceException("根据模板code(%s)查出的模板为空");
        }
        String ticketTemplateId = ticketTemplateList.get(0).getId();
        ThreadUtil.execAsync(() -> {
            logger.info("同步客服工单模版公共扩展字段开始...");
            while (true) {
                PageInfo<TicketData> pageInfo = PageHelper.<TicketData>startPage(syncExtendFieldsDto.getPageNum(), syncExtendFieldsDto.getPageSize())
                        .doSelectPageInfo(() -> ticketDataService.lambdaQuery()
                                .isNull(TicketData::getDeleteTime)
                                .eq(TicketData::getTemplateId, ticketTemplateId)
                                .ge(TicketData::getCreateTime, "2024-12-20 00:00:00")
                                .isNull(TicketData::getExtend2)
                                .list());
                if (null == pageInfo || CollectionUtils.isEmpty(pageInfo.getList())) {
                    logger.error("查询工单列表为空:{}", JSONObject.toJSONString(pageInfo));
                    break;
                }
                List<TicketData> ticketDataList = pageInfo.getList();
                for (TicketData ticketData : ticketDataList) {
                    String tickDataId = ticketData.getId();
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .isNull(TicketFormItemData::getDeleteTime)
                            .eq(TicketFormItemData::getTicketDataId, tickDataId)
                            .list();
                    if (CollectionUtils.isEmpty(ticketFormItemDataList)) {
                        continue;
                    }
                    for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
                        String itemLabel = ticketFormItemData.getItemLabel();
                        String itemValue = ticketFormItemData.getItemValue();
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("自然人客户号")) {
                            ticketData.setExtend1(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("工单类型")) {
                            ticketData.setExtend2(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("客户名称")) {
                            ticketData.setExtend3(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("客户诉求")) {
                            ticketData.setExtend4(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("联系方式")) {
                            ticketData.setExtend5(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("反馈渠道")) {
                            ticketData.setExtend6(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("优先级")) {
                            ticketData.setExtend7(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("跟进状态")) {
                            ticketData.setExtend8(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("客户号")) {
                            ticketData.setExtend9(itemValue);
                        }
                    }
                }
                ticketDataService.saveOrUpdateBatch(ticketDataList);
                logger.info("已同步完第{}页",syncExtendFieldsDto.getPageNum());
                syncExtendFieldsDto.setPageNum(syncExtendFieldsDto.getPageNum() + 1);
            }
            logger.info("同步客服工单模版公共扩展字段结束...");
        });
        return AjaxResult.success("同步客服工单模版公共扩展字段进行中...");
    }

    @PostMapping({"/syncComplainExtendFields"})
    public AjaxResult syncComplainExtendFields(@RequestBody SyncExtendFieldsDto syncExtendFieldsDto) {
        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery()
                .isNull(TicketTemplate::getDeleteTime)
                .eq(TicketTemplate::getTicketTemplateCode,syncExtendFieldsDto.getTicketTemplateCode())
                .list();
        if (CollectionUtils.isEmpty(ticketTemplateList)) {
            throw new ServiceException("根据模板code(%s)查出的模板为空");
        }
        String ticketTemplateId = ticketTemplateList.get(0).getId();
        ThreadUtil.execAsync(() -> {
            logger.info("同步内部投诉模版公共扩展字段开始...");
            while (true) {
                PageInfo<TicketData> pageInfo = PageHelper.<TicketData>startPage(syncExtendFieldsDto.getPageNum(), syncExtendFieldsDto.getPageSize())
                        .doSelectPageInfo(() -> ticketDataService.lambdaQuery()
                                .isNull(TicketData::getDeleteTime)
                                .eq(TicketData::getTemplateId, ticketTemplateId)
                                .ge(TicketData::getCreateTime, "2024-12-20 00:00:00")
                                .isNull(TicketData::getExtend6)
                                .list());
                if (null == pageInfo || CollectionUtils.isEmpty(pageInfo.getList())) {
                    logger.error("查询工单列表为空:{}", JSONObject.toJSONString(pageInfo));
                    break;
                }
                List<TicketData> ticketDataList = pageInfo.getList();
                for (TicketData ticketData : ticketDataList) {
                    String tickDataId = ticketData.getId();
                    List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataService.lambdaQuery()
                            .isNull(TicketFormItemData::getDeleteTime)
                            .eq(TicketFormItemData::getTicketDataId, tickDataId)
                            .list();
                    if (CollectionUtils.isEmpty(ticketFormItemDataList)) {
                        continue;
                    }
                    for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
                        String itemLabel = ticketFormItemData.getItemLabel();
                        String itemValue = ticketFormItemData.getItemValue();
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("自然人客户号")) {
                            ticketData.setExtend1(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("工单类型")) {
                            ticketData.setExtend2(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("客户名称")) {
                            ticketData.setExtend3(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("客户诉求")) {
                            ticketData.setExtend4(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("联系方式")) {
                            ticketData.setExtend5(itemValue);
                        }
                        if (StringUtils.isNotBlank(itemLabel) && itemLabel.equals("反馈渠道")) {
                            ticketData.setExtend6(itemValue);
                        }
                    }
                }
                ticketDataService.saveOrUpdateBatch(ticketDataList);
                logger.info("已同步完第{}页",syncExtendFieldsDto.getPageNum());
                syncExtendFieldsDto.setPageNum(syncExtendFieldsDto.getPageNum() + 1);
            }
            logger.info("同步内部投诉模版公共扩展字段结束...");
        });
        return AjaxResult.success("同步内部投诉模版公共扩展字段进行中...");
    }

    @Resource
    private ITicketDataOpenService ticketDataOpenService;
    @PostMapping({"/queryBusiTicketDataList"})
    public AjaxResult queryBusiTicketDataList(@RequestBody BusiTicketDataRequestDto busiTicketDataRequestDto) throws Exception{
        busiTicketDataRequestDto.setCreateStartTime(DateUtils.parseDate("2024-01-02 00:00:00", DateUtils.YYYY_MM_DD_HH_MM_SS));
        Response<BusiTicketDataResponseDto> busiTicketDataResponseDtoResponse = ticketDataOpenService.queryBusiTicketDataList(busiTicketDataRequestDto);
        logger.info("查询工单结果:{}", JSONObject.toJSONString(busiTicketDataResponseDtoResponse));
        return AjaxResult.success(busiTicketDataResponseDtoResponse);
    }


    @Resource
    private ITicketDataQueryService ticketDataQueryService;
    @PostMapping({"/busiCommonESQuery"})
    public AjaxResult busiCommonESQuery(@RequestBody BusiCommonESQueryReqDto busiCommonESQueryReqDto) {
        logger.info("查询入参:{}", JSONObject.toJSONString(busiCommonESQueryReqDto));
        RemoteTableDataInfo<BusiCommonESQueryRspDto>  remoteTableDataInfo = ticketDataQueryService.busiCommonESQuery(busiCommonESQueryReqDto);
        logger.info("查询工单结果:{}", JSONObject.toJSONString(remoteTableDataInfo));
        return AjaxResult.success(remoteTableDataInfo);
    }

    @Resource
    private TicketAppComponent ticketAppComponent;
    @Resource
    private TicketTemplateComponent ticketTemplateComponent;
    @Resource
    private TicketCategoryComponent ticketCategoryComponent;
    @PostMapping({"/queryTicketObject"})
    public AjaxResult queryTicketObject(@RequestBody TicketObjectDto ticketObjectDto) throws Exception{
        logger.info("ticketObjectDto:{}", ticketObjectDto);
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(ticketObjectDto.getAppId())) {
            logger.info("通过AppId查询结果({})", ticketAppComponent.getAppNameById(ticketObjectDto.getAppId()));
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(ticketObjectDto.getTemplateId())) {
            logger.info("通过TemplateId查询结果({})", ticketTemplateComponent.getTemplateNameById(ticketObjectDto.getTemplateId()));
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(ticketObjectDto.getCategoryId())) {
            logger.info("通过CategoryId查询结果({})", ticketCategoryComponent.getCategoryNameByTemplateId(ticketObjectDto.getCategoryId()));
        }
        return AjaxResult.success();
    }


    @PostMapping({"/updateTicketData"})
    public AjaxResult updateTicketData(@RequestBody UpdateTicketDataDto updateTicketDataDto) {
        logger.info("updateTicketDataDto:{}", updateTicketDataDto);
        if (Objects.isNull(updateTicketDataDto)
                || (com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getTicketDataId())
                && com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getAppId())
                && com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getUpdateStartTime())
                && com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getUpdateEndTime()))

        ) {
            throw new ServiceException(String.format("更新订单数据的条件异常：%s",updateTicketDataDto));
        }
        LambdaUpdateWrapper<TicketData> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.setSql("update_time = DATE_ADD(update_time, INTERVAL 1 SECOND)");
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getTicketDataId())) {
            lambdaUpdateWrapper.eq(TicketData::getId, updateTicketDataDto.getTicketDataId());
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getUpdateStartTime())) {
            lambdaUpdateWrapper.ge(TicketData::getUpdateTime, updateTicketDataDto.getUpdateStartTime());
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getUpdateEndTime())) {
            lambdaUpdateWrapper.le(TicketData::getUpdateTime, updateTicketDataDto.getUpdateEndTime());
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getAppId())) {
            lambdaUpdateWrapper.eq(TicketData::getAppId, updateTicketDataDto.getAppId());
        }
        Boolean updateFlag = ticketDataService.update(lambdaUpdateWrapper);
        return AjaxResult.success(DateUtils.getTime()+": "+updateFlag);
    }

    @PostMapping({"/updateTicketFormItemData"})
    public AjaxResult updateTicketFormItemData(@RequestBody UpdateTicketDataDto updateTicketDataDto) {
        logger.info("updateTicketFormItemData:{}", updateTicketDataDto);
        if (Objects.isNull(updateTicketDataDto)
                || (com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getTicketDataId())
                && com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getUpdateStartTime())
                && com.smy.tfs.common.utils.StringUtils.isEmpty(updateTicketDataDto.getUpdateEndTime()))

        ) {
            throw new ServiceException(String.format("更新订单数据的条件异常：%s",updateTicketDataDto));
        }
        LambdaUpdateWrapper<TicketFormItemData> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.setSql("update_time = DATE_ADD(update_time, INTERVAL 1 SECOND)");
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getTicketDataId())) {
            lambdaUpdateWrapper.eq(TicketFormItemData::getTicketDataId, updateTicketDataDto.getTicketDataId());
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getUpdateStartTime())) {
            lambdaUpdateWrapper.ge(TicketFormItemData::getUpdateTime, updateTicketDataDto.getUpdateStartTime());
        }
        if (com.smy.tfs.common.utils.StringUtils.isNotEmpty(updateTicketDataDto.getUpdateEndTime())) {
            lambdaUpdateWrapper.le(TicketFormItemData::getUpdateTime, updateTicketDataDto.getUpdateEndTime());
        }
        lambdaUpdateWrapper.eq(TicketFormItemData::getItemAdvancedSearch, FormItemAdvancedSearchEnum.TRUE);
        Boolean updateFlag = ticketFormItemDataService.update(lambdaUpdateWrapper);
        return AjaxResult.success(DateUtils.getTime()+": "+updateFlag);
    }


}
