package com.smy.tfs.quartz.task;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.service.ITicketFormItemValuesService;
import com.smy.tfs.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("ticketAccountDataRepairTask")
public class TicketAccountDataRepairTask {
    private final static String accountMapKey = "%s:%s";
    private final static String searchKeyWord = "sameOriginId";

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ITicketExecutorGroupService ticketExecutorGroupService;
    @Resource
    private ITicketFlowNodeExecutorDataService ticketFlowNodeExecutorDataService;
    @Resource
    private ITicketFlowNodeExecutorTemplateService ticketFlowNodeExecutorTemplateService;
    @Resource
    private ITicketFormItemValuesService ticketFormItemValuesService;


    // 定时任务方法
    public void repairTicketAccountData() {
        SecurityUtils.wrapContext(TfsBaseConstant.defaultOriginId, TfsBaseConstant.defaultUserId, TfsBaseConstant.defaultUserName,
                TfsBaseConstant.defaultUserType, TfsBaseConstant.defaultAppId, this::doRepairTicketAccountData);
    }

    private void doRepairTicketAccountData() {
        log.info("开始执行全量数据修复任务");
        Map<String, String> accountInfoMap = initAccountInfoMap();

        log.info("开始执行全量数据修复任务--【ticket_app】");
        List<TicketApp> ticketAppList = ticketAppService.list();
        ticketAppList.forEach(ticketApp -> ticketApp.setAppAdminUsers(parseOldAccountInfoToNew(ticketApp.getAppAdminUsers(), accountInfoMap)));
        ticketAppService.updateBatchById(ticketAppList, 100);

        log.info("开始执行全量数据修复任务--【ticket_data】");
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery().orderByDesc(TicketData::getCreateTime).list();
        ticketDataList.forEach(ticketData -> {
            ticketData.setCurrentDealUsers(parseOldAccountInfoToNew(ticketData.getCurrentDealUsers(), accountInfoMap));
            ticketData.setCurrentDoneUsers(parseOldAccountInfoToNew(ticketData.getCurrentDoneUsers(), accountInfoMap));
            ticketData.setCurrentCcUsers(parseOldAccountInfoToNew(ticketData.getCurrentCcUsers(), accountInfoMap));
            ticketData.setApplyUser(parseOldAccountInfoToNew(ticketData.getApplyUser(), accountInfoMap));
        });
        ticketDataService.updateBatchById(ticketDataList, 100);

        log.info("开始执行全量数据修复任务--【ticket_executor_group】");
        List<TicketExecutorGroup> ticketExecutorGroupList = ticketExecutorGroupService.list();
        ticketExecutorGroupList.forEach(ticketExecutorGroup -> {
            ticketExecutorGroup.setAccountInfo(parseOldAccountInfoToNew(ticketExecutorGroup.getAccountInfo(), accountInfoMap));
        });
        ticketExecutorGroupService.updateBatchById(ticketExecutorGroupList, 100);

        log.info("开始执行全量数据修复任务--【ticket_flow_node_executor_data】");
        List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = ticketFlowNodeExecutorDataService.list();
        ticketFlowNodeExecutorDataList.forEach(ticketFlowNodeExecutorData -> {
            ticketFlowNodeExecutorData.setExecutorValue(parseOldAccountInfoToNew(ticketFlowNodeExecutorData.getExecutorValue(), accountInfoMap));
            ticketFlowNodeExecutorData.setExecutorList(parseOldAccountInfoToNew(ticketFlowNodeExecutorData.getExecutorList(), accountInfoMap));
            ticketFlowNodeExecutorData.setExecutorDoneList(parseOldAccountInfoToNew(ticketFlowNodeExecutorData.getExecutorDoneList(), accountInfoMap));
        });
        ticketFlowNodeExecutorDataService.updateBatchById(ticketFlowNodeExecutorDataList, 100);

        log.info("开始执行全量数据修复任务--【ticket_flow_node_executor_template】");
        List<TicketFlowNodeExecutorTemplate> ticketFlowNodeExecutorTemplateList = ticketFlowNodeExecutorTemplateService.list();
        ticketFlowNodeExecutorTemplateList.forEach(ticketFlowNodeExecutorTemplate -> {
            ticketFlowNodeExecutorTemplate.setExecutorValue(parseOldAccountInfoToNew(ticketFlowNodeExecutorTemplate.getExecutorValue(), accountInfoMap));
        });
        ticketFlowNodeExecutorTemplateService.updateBatchById(ticketFlowNodeExecutorTemplateList, 100);

        log.info("开始执行全量数据修复任务--【ticket_form_item_values】");
        List<TicketFormItemValues> ticketFormItemValueList = ticketFormItemValuesService.list();
        ticketFormItemValueList.forEach(ticketFormItemValue -> {
            ticketFormItemValue.setCurrentDealUsers(parseOldAccountInfoToNew(ticketFormItemValue.getCurrentDealUsers(), accountInfoMap));
            ticketFormItemValue.setCurrentDoneUsers(parseOldAccountInfoToNew(ticketFormItemValue.getCurrentDoneUsers(), accountInfoMap));
            ticketFormItemValue.setCurrentCcUsers(parseOldAccountInfoToNew(ticketFormItemValue.getCurrentCcUsers(), accountInfoMap));
            ticketFormItemValue.setApplyUser(parseOldAccountInfoToNew(ticketFormItemValue.getApplyUser(), accountInfoMap));
        });
        ticketFormItemValuesService.updateBatchById(ticketFormItemValueList, 100);
    }

    private Map<String, String> initAccountInfoMap() {
        List<TicketAccountMapping> accountMappingList = ticketAccountMappingService
                .lambdaQuery()
                .isNotNull(TicketAccountMapping::getSameOriginId)
                .isNotNull(TicketAccountMapping::getAccountId)
                .isNotNull(TicketAccountMapping::getAccountType)
                .list();

        Map<String, String> accountInfoMap = new HashMap<>();
        for (TicketAccountMapping ticketAccountMapping : accountMappingList) {
            accountInfoMap.put(String.format(accountMapKey, ticketAccountMapping.getAccountId(), ticketAccountMapping.getAccountType()),
                    ticketAccountMapping.getSameOriginId());
        }
        return accountInfoMap;
    }

    private String parseOldAccountInfoToNew(String oldAccountInfoStr, Map<String, String> accountInfoMap) {
        try {
            if (JSONUtil.isJsonArray(oldAccountInfoStr)) {
                List<AccountInfo> accountInfoList = JSON.parseArray(oldAccountInfoStr, AccountInfo.class);
                for (AccountInfo accountInfo : accountInfoList) {
                    //存在有问题的用户，直接返回原值
                    if (StrUtil.hasBlank(accountInfo.getAccountId(), accountInfo.getAccountType())){
                        return oldAccountInfoStr;
                    }
                    String sameOriginId = accountInfoMap.get(String.format(accountMapKey, accountInfo.getAccountId(), accountInfo.getAccountType()));
                    if (StrUtil.isNotBlank(sameOriginId)) {
                        accountInfo.setSameOriginId(sameOriginId);
                    }
                }
                return JSONUtil.toJsonStr(accountInfoList);
            } else if (JSONUtil.isTypeJSONObject(oldAccountInfoStr)) {
                AccountInfo accountInfo = JSONUtil.toBean(oldAccountInfoStr, AccountInfo.class);
                //存在有问题的用户，直接返回原值
                if (StrUtil.hasBlank(accountInfo.getAccountId(), accountInfo.getAccountType())){
                    return oldAccountInfoStr;
                }
                String sameOriginId = accountInfoMap.get(String.format(accountMapKey, accountInfo.getAccountId(), accountInfo.getAccountType()));
                if (StrUtil.isNotBlank(sameOriginId)) {
                    accountInfo.setSameOriginId(sameOriginId);
                }
                return JSONUtil.toJsonStr(accountInfo);
            }
            return oldAccountInfoStr;
        } catch (Exception e){
            log.error("线上账号数据修复时，账号信息转换失败，原因：", e);
            return oldAccountInfoStr;
        }
    }
}
