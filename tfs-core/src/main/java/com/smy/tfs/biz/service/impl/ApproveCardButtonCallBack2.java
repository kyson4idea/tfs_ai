package com.smy.tfs.biz.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.dto.out.TicketActionDto;
import com.smy.tfs.api.enums.ActionTypeEnum;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service("approveCardButtonCallBack2")
public class ApproveCardButtonCallBack2 implements NotificationService.ICardButtonCallBack {

    @Resource
    private TicketDataApproveService ticketDataApproveService;

    /**
     * @param key  button key
     * @param qwid qw userid
     */
    @Override
    public void callback(String key, String qwid) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(key.substring("approveCardButtonCallBack2-".length()));
            String ticketDataId = jsonObject.getString("ticketDataId");
            String actionType = jsonObject.getString("actionType");
            String actionDescription = jsonObject.getString("actionDescription");
            String actionName = jsonObject.getString("actionName");
            //{"update": [{"code": "name","value": "aaa"}]}
            String actionValue = jsonObject.getString("actionValue");
            String accountType = jsonObject.getString("accountType");
            String accountId = jsonObject.getString("accountId");
            String accountName = jsonObject.getString("accountName");
            String nodeId = jsonObject.getString("nodeId");

            ActionTypeEnum actionTypeEnum = ActionTypeEnum.getEnumByCode(actionType);
            Response<String> response = null;
            switch (actionTypeEnum) {
                case APPROVE_PASS:
                    TicketFlowNodeApproveDto passApproveDto = new TicketFlowNodeApproveDto();
                    passApproveDto.setTicketID(ticketDataId);
                    passApproveDto.setDealNodeId(nodeId);
                    passApproveDto.setDealType("PASS");
                    passApproveDto.setDealDescription(actionName);
                    passApproveDto.setDealOpinion("企微快速通过");
                    response = ticketDataApproveService.approve(passApproveDto, accountType, accountId, accountName);
                    break;
                case APPROVE_REJECT:
                    TicketFlowNodeApproveDto rejectApproveDto = new TicketFlowNodeApproveDto();
                    rejectApproveDto.setTicketID(ticketDataId);
                    rejectApproveDto.setDealNodeId(nodeId);
                    rejectApproveDto.setDealType("REJECT");
                    rejectApproveDto.setDealDescription(actionName);
                    rejectApproveDto.setDealOpinion("企微快速驳回");
                    response = ticketDataApproveService.approve(rejectApproveDto, accountType, accountId, accountName);
                    break;
                case APPROVE_FINISH:
                    TicketFlowNodeApproveDto finishApproveDto = new TicketFlowNodeApproveDto();
                    finishApproveDto.setTicketID(ticketDataId);
                    finishApproveDto.setDealNodeId(nodeId);
                    finishApproveDto.setDealType("FINISH");
                    finishApproveDto.setDealDescription(actionName);
                    finishApproveDto.setDealOpinion("企微快速关单");
                    response = ticketDataApproveService.approve(finishApproveDto, accountType, accountId, accountName);
                    break;
                case UPDATE_TICKET:
                    //{"update_ticket": [{"code": "name","value": "aaa"}]}
                    Response<TicketDataDto> ticketDataDtoResp = ticketDataApproveService.selectTicketData(new ReqParam(ticketDataId));
                    if (!ticketDataDtoResp.isSuccess()) {
                        log.error("修改工单 查询工单数据异常：" + ticketDataDtoResp.getMsg());
                        return;
                    }
                    TicketDataDto ticketDataDto = ticketDataDtoResp.getData();

                    TicketFormUpdateDto ticketFormDataDto = new TicketFormUpdateDto();
                    ticketFormDataDto.setTicketDataId(ticketDataId);
                    ticketFormDataDto.setDealDescription(actionName);
                    ticketFormDataDto.setDealOpinion("企微快速审批");
                    if (StringUtils.isNotEmpty(actionValue)) {
                        JSONObject jsonActionObject = JSONObject.parseObject(actionValue);
                        if (jsonActionObject.containsKey("update_ticket")) {
                            JSONArray jsonArray = jsonActionObject.getJSONArray("update_ticket");
                            if (CollectionUtils.isNotEmpty(jsonArray)) {
                                List<TicketFormItemStdDto> formItems = new ArrayList<>();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    JSONObject actionItem = JSONObject.from(jsonArray.get(i));
                                    TicketFormItemStdDto item = new TicketFormItemStdDto();
                                    item.setTemplateId(actionItem.getString("code"));
                                    item.setValue(actionItem.getString("value"));
                                    formItems.add(item);
                                }
                                ticketFormDataDto.setFormItems(formItems);
                            }
                        }
                    }
                    response = ticketDataApproveService.updateTicket(ticketFormDataDto, accountType, accountId, accountName);
                    if (response.isSuccess()) {
                        if (StringUtils.isNotEmpty(ticketDataDto.getCurrentNodeId()) && ticketDataDto.getTicketFlowDataDto() != null && CollectionUtils.isNotEmpty(ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
                            TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(node -> Objects.equals(node.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
                            if (currentFlowNodeData != null) {
                                AccountInfo dealUser=new AccountInfo();
                                dealUser.setAccountId(accountId);
                                dealUser.setAccountType(accountType);
                                dealUser.setAccountName(accountName);
                                ticketDataApproveService.disable(ticketDataDto, currentFlowNodeData, dealUser, actionName + "完成");
                            }
                        }
                    }
                    break;
                default:
                    log.error("企微审批回调, 不支持的审批操作类型:{}", actionType);
                    return;
            }
            if (!response.isSuccess()) {
                log.error("企业微信卡片审批失败,入参 {} {} response = {}", key, qwid, JSONUtil.toJsonStr(response));
            }
        } catch (Exception e) {
            log.error("企业微信卡片审批异常, 入参 {} {} error = {}", key, qwid, JSONUtil.toJsonStr(e));
        }
    }
}
