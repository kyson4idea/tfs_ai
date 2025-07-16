package com.smy.tfs.biz.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.TicketFlowNodeDataDto;
import com.smy.tfs.api.dto.TicketFormItemDataDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.AccountInfoDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.service.INotificationBizService;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.common.URL;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationBizService implements INotificationBizService {

    @Resource
    TfSJumpUrlProperties tfSJumpUrlProperties;
    @Resource
    ITicketAccountService ticketAccountService;
    @Resource
    ITicketFlowNodeActionDataService ticketFlowNodeActionDataService;
    @Resource
    NotificationService notificationService;
    @Resource
    ITicketFlowNodeDataService flowNodeDataService;
    @Resource
    TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    public Response<String> SendDealCard(
            String cardTitle,
            TicketDataDto ticketDataDto,
            AccountInfo dealUser,
            ApproveDealTypeEnum dealType,
            List<AccountInfo> sendUsers,
            Boolean saveData,
            String dealOpinion
    ) {
        String dealUserStr = JSONUtil.toJsonStr(dealUser);
        //数据查询, 工单数据
        if (ticketDataDto.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s不在审批中", ticketDataDto.getId()));
        }
        if (ticketDataDto.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        List<AccountInfoDto> qwUserList = new ArrayList<>();
        for (AccountInfo sendUser : sendUsers) {
            TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(sendUser.getAccountId(), sendUser.getAccountType());
            if (ticketRemoteAccountDto != null && org.apache.commons.lang3.StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId())) {
                qwUserList.add(new AccountInfoDto(sendUser.getSameOriginId(), sendUser.getAccountType(), sendUser.getAccountId(), sendUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
            }
        }
        TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
        if (currentFlowNodeData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 未找到当前节点", ticketDataDto.getId()));
        }
        List<TicketFlowNodeActionData> actionDataList = ticketFlowNodeActionDataService.lambdaQuery()
                .isNull(TicketFlowNodeActionData::getDeleteTime)
                .eq(TicketFlowNodeActionData::getTicketDataId, ticketDataDto.getId())
                .eq(TicketFlowNodeActionData::getTicketFlowNodeDataId, currentFlowNodeData.getId()).list();

        //new
        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
        qwcardMsg.setTitle(cardTitle);

        String desciptionTemplate = "申请时间：{{apply_time}}    业务：{{app_name}}";
        qwcardMsg.setDescription(desciptionTemplate
                .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                .replace("{{app_name}}", ticketDataDto.getAppName()));

        log.info("SendDealCard 需要通知的人，{}", JSONUtil.toJsonStr(qwUserList));
        for (AccountInfoDto accountInfo : qwUserList) {
            String appUserId = StringUtils.EMPTY;
            String accountType = StringUtils.EMPTY;
            if (!ObjectHelper.anyIsEmpty(accountInfo, accountInfo.getAccountId())) {
                appUserId = accountInfo.getAccountId();
            }
            if (!ObjectHelper.anyIsEmpty(accountInfo, accountInfo.getAccountType())) {
                accountType = accountInfo.getAccountType();
            }
            String appId = ticketDataDto.getAppId();
            String ldapUserId = getLdapUserId(appUserId, accountType);
            String jumpUrl;
            if (ObjectHelper.isNotEmpty(ldapUserId)) {
                StringBuilder jumpUrlStringBuilder = new StringBuilder(tfSJumpUrlProperties.getGetUserTokenUrl()).append(ticketDataDto.getId());
                jumpUrlStringBuilder.append("&appUserId=").append(URL.encode(appUserId));
                jumpUrlStringBuilder.append("&appId=").append(URL.encode(appId));
                jumpUrlStringBuilder.append("&ldapUserId=").append(URL.encode(ldapUserId));
                jumpUrl = jumpUrlStringBuilder.toString();
            } else {
                StringBuilder jumpUrlStringBuilder = new StringBuilder(tfSJumpUrlProperties.getTicketDetailUrl()).append(ticketDataDto.getId());
                jumpUrl = jumpUrlStringBuilder.toString();
            }
            qwcardMsg.setJumpUrl(jumpUrl);

            List<NotificationService.KvContent> kvContentList = BuildKvContentList(ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList(), jumpUrl);
            qwcardMsg.setKvContentList(kvContentList);
            qwcardMsg.setUserIdList(Arrays.asList(accountInfo.getQywxId()));

            log.info("accountInfo:{},jumpUrl:{}", JSONObject.toJSONString(accountInfo), jumpUrl);
            log.info("accountInfo:{},qwcardMsg:{}", JSONObject.toJSONString(accountInfo), JSON.toJSONString(qwcardMsg));

            Map<String, String> buttonMap = new LinkedHashMap<>();
            if (CollectionUtils.isEmpty(actionDataList)) {
                String rejectKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.REJECT.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(rejectKey, "驳回");
                String passKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.PASS.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(passKey, "通过");
                qwcardMsg.setButtonKeyMap(buttonMap);
            } else {
                for (TicketFlowNodeActionData actionData : actionDataList) {
                    com.alibaba.fastjson2.JSONObject jsonObject = new com.alibaba.fastjson2.JSONObject();
                    if (Arrays.asList(
                            ActionTypeEnum.APPROVE_PASS,
                            ActionTypeEnum.APPROVE_REJECT,
                            ActionTypeEnum.APPROVE_FINISH,
                            ActionTypeEnum.UPDATE_TICKET
                    ).contains(actionData.getActionType())) {
                        jsonObject.put("ticketDataId", ticketDataDto.getId());
                        jsonObject.put("actionType", actionData.getActionType());
                        jsonObject.put("actionName", actionData.getActionName());
                        jsonObject.put("actionValue", actionData.getActionValue());
                        jsonObject.put("accountType", accountInfo.getAccountType());
                        jsonObject.put("accountId", accountInfo.getAccountId());
                        jsonObject.put("accountName", accountInfo.getAccountName());
                        jsonObject.put("nodeId", currentFlowNodeData.getId());
                        String actionKey = "approveCardButtonCallBack2-" + jsonObject.toJSONString();
                        buttonMap.put(actionKey, actionData.getActionName());
                    }
                }
                if (buttonMap.size() > 4 || buttonMap.size() < 1) {
                    return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "企微卡片，按钮数量配置过多或没有");
                }
                qwcardMsg.setButtonKeyMap(buttonMap);
            }
            NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
            if ("0".equals(cardNotifyRet.getErrorCode())) {
                TicketFlowNodeData ticketFlowNodeData = flowNodeDataService.lambdaQuery()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId())
                        .one();
                Map<String, List> wxDealCardCode = new HashMap<>();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(ticketFlowNodeData.getNodeWxDealCardCode())) {
                    wxDealCardCode.putAll(JSONObject.parseObject(ticketFlowNodeData.getNodeWxDealCardCode(), Map.class));
                }
                String key = String.format("%s-%s-%s", accountInfo.getAccountId(), accountInfo.getAccountType(), TicketMsgArriveTypeEnum.WECOM.getCode());
                if (!wxDealCardCode.containsKey(key)) {
                    wxDealCardCode.put(key, new ArrayList());
                }
                wxDealCardCode.get(key).add(cardNotifyRet.getResponseCode());
                var msgSaveRes = flowNodeDataService.lambdaUpdate()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId())
                        .isNull(TicketFlowNodeData::getDeleteTime)
                        .set(TicketFlowNodeData::getNodeWxDealCardCode, JSONUtil.toJsonStr(wxDealCardCode))
                        .update();
                if (!msgSaveRes) {
                    log.error(String.format("节点卡片信息存储失败，工单ID：%s 流程节点ID：%s", currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId()));
                }
            }
        }
        if (saveData) {
            Date now = new Date();
            TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
            approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            approveDetail.setTicketDataId(ticketDataDto.getId());
            approveDetail.setTicketFlowNodeDataId(ticketDataDto.getCurrentNodeId());
            approveDetail.setDealUserType(dealUser.getAccountType());
            approveDetail.setDealUserId(dealUser.getAccountId());
            approveDetail.setDealUserName(dealUser.getAccountName());
            approveDetail.setDealType(dealType);
            approveDetail.setDealOpinion(dealOpinion);
            approveDetail.setCreateTime(now);
            approveDetail.setCreateBy(dealUserStr);
            approveDetail.setUpdateTime(now);
            approveDetail.setUpdateBy(dealUserStr);
            Boolean saveRes = ticketFlowNodeApproveDetailService.save(approveDetail);
            if (!saveRes) {
                return new Response<>(null, BizResponseEnums.SAVE_ERROR, "操作记录保存失败");
            }
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "操作成功");
    }


    public Response<String> SendNotifyCard(String cardTitle, TicketDataDto ticketDataDto, String currentNodeDataID, AccountInfo dealUser, ApproveDealTypeEnum dealType, List<AccountInfo> sendUsers, Boolean saveData) {
        String dealUserStr = JSONUtil.toJsonStr(dealUser);
        if (ticketDataDto.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        if (CollectionUtils.isEmpty(sendUsers)) {
            return new Response<>(null, BizResponseEnums.SUCCESS, "通知人为空");
        }
        List<AccountInfoDto> qwUserList = new ArrayList<>();
        for (AccountInfo sendUser : sendUsers) {
            //当前节点已发送过消息的，不再发送
            List<TicketFlowNodeApproveDetail> haveDealUserList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, currentNodeDataID)
                    .eq(TicketFlowNodeApproveDetail::getDealType, dealType)
                    .eq(TicketFlowNodeApproveDetail::getDealUserType, sendUser.getAccountType())
                    .eq(TicketFlowNodeApproveDetail::getDealUserId, sendUser.getAccountId()).list();
            if (CollectionUtils.isEmpty(haveDealUserList)) {
                TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(sendUser.getAccountId(), sendUser.getAccountType());
                if (ticketRemoteAccountDto != null && org.apache.commons.lang3.StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId())) {
                    qwUserList.add(new AccountInfoDto(sendUser.getSameOriginId(), sendUser.getAccountType(), sendUser.getAccountId(), sendUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
                }
            }
        }
        String msgCode = "";
        if (!CollectionUtils.isEmpty(qwUserList)) {
            NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
            qwcardMsg.setTitle(cardTitle);

            String description = "申请时间：{{apply_time}}   业务：{{app_name}}";
            qwcardMsg.setDescription(description
                    .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                    .replace("{{app_name}}", ticketDataDto.getAppName())
            );

            Map<String, String> linkMap = new HashMap<>();
            StringBuilder jumpUrlStringBuilder = new StringBuilder(tfSJumpUrlProperties.getTicketDetailUrl()).append(ticketDataDto.getId());
            String jumpUrl = jumpUrlStringBuilder.toString();
            linkMap.put("工单详情", jumpUrl);
            qwcardMsg.setLinkKeyMap(linkMap);

            List<String> userIdList = qwUserList.stream().map(AccountInfoDto::getQywxId).collect(Collectors.toList());
            qwcardMsg.setUserIdList(userIdList);
            qwcardMsg.setJumpUrl(jumpUrl);

            List<NotificationService.KvContent> kvContentList = BuildKvContentList(ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList(), jumpUrl);
            qwcardMsg.setKvContentList(kvContentList);

            log.info("jumpUrl:{}", jumpUrl);
            log.info("qwcardMsg:{}", JSON.toJSONString(qwcardMsg));

            NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
            msgCode = cardNotifyRet.getResponseCode();
        }

        if (saveData) {
            var now = new Date();
            for (AccountInfoDto info : qwUserList) {
                TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
                approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
                approveDetail.setTicketDataId(ticketDataDto.getId());
                approveDetail.setTicketFlowNodeDataId(currentNodeDataID);
                approveDetail.setDealUserId(info.getAccountId());
                approveDetail.setDealUserType(info.getAccountType());
                approveDetail.setDealUserName(info.getAccountName());
                approveDetail.setDealType(dealType);
                approveDetail.setDealOpinion("");
                approveDetail.setCreateBy(dealUserStr);
                approveDetail.setUpdateBy(dealUserStr);
                approveDetail.setCreateTime(now);
                approveDetail.setUpdateTime(now);
                var ccRes = ticketFlowNodeApproveDetailService.save(approveDetail);
                if (ccRes == false) {
                    log.error("卡片通知数据保存异常");
                }
            }
        }
        return new Response<>(msgCode, BizResponseEnums.SUCCESS, "发送通知卡片成功");
    }

    private String getLdapUserId(String appUserId, String accountType) {
        if ("ldap".equals(accountType)) {
            return appUserId;
        }
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(appUserId, accountType);
        if (ObjectHelper.isEmpty(ticketAccountMapping) || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            log.error(String.format("未找到userId: %s, userType: %s 对应的用户信息", appUserId, accountType));
            return null;
        }
        String sameOriginId = ticketAccountMapping.getSameOriginId();
        List<TicketAccountMapping> ticketAccountMappingList = ticketAccountMappingService.lambdaQuery()
                .eq(TicketAccountMapping::getSameOriginId, sameOriginId)
                .eq(TicketAccountMapping::getAccountType, "ldap")
                .isNull(TicketAccountMapping::getDeleteTime)
                .list();
        if (ObjectHelper.isEmpty(ticketAccountMappingList)) {
            log.error(String.format("ldap同源账户appUserId: %s, userType: %s , sameOriginId: %s不存在", appUserId, accountType, sameOriginId));
            return null;
        }
        for (var item : ticketAccountMappingList) {
            if (StringUtils.isNotEmpty(item.getAccountId())) {
                return item.getAccountId();
            }
        }
        return null;
    }

    public static List<NotificationService.KvContent> BuildKvContentList(List<TicketFormItemDataDto> ticketFormItemDataDtoList, String jumpUrl) {
        List<NotificationService.KvContent> kvContentList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketFormItemDataDtoList)) {
            int maxSum = 0;
            int itemCount = ticketFormItemDataDtoList.size();
            for (TicketFormItemDataDto ticketFormItemDataDto : ticketFormItemDataDtoList) {
                if (StringUtils.isNotEmpty(ticketFormItemDataDto.getItemParentId())) {
                    continue;
                }
                maxSum = maxSum + 1;
                if (itemCount > 6 && maxSum == 6) {
                    kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                    break;
                } else {
                    Map<String, String> configObj = new HashMap<>();
                    if (StringUtils.isNotBlank(ticketFormItemDataDto.getItemConfig())) {
                        configObj.putAll(JSONObject.parseObject(ticketFormItemDataDto.getItemConfig(), Map.class));
                    }
                    if ("NO".equals(configObj.get("displayAble"))) {
                        maxSum = maxSum - 1;
                        continue;
                    }
                    if (configObj.containsKey("displayValue")) {
                        ticketFormItemDataDto.setItemValue(configObj.get("displayValue"));
                    }
                    switch (ticketFormItemDataDto.getItemType()) {
                        case INPUTNUMBER://
                            String inputNumberValue = ticketFormItemDataDto.getItemValue();
                            if (configObj.containsKey("numUnit")) {
                                inputNumberValue = inputNumberValue + configObj.get("numUnit");
                            }
                            kvContentList.add(new NotificationService.KvContent(
                                    NotificationService.KvContentType.TEXT,
                                    ticketFormItemDataDto.getItemLabel(),
                                    inputNumberValue,
                                    "",
                                    "",
                                    ""));
                            break;
                        case SELECTMULTIPLE:
                            List<String> valueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), String.class);
                            if (valueArr == null) {
                                valueArr = new ArrayList<>();
                            }
                            StringBuilder selectMultipleValue = new StringBuilder();
                            for (String value : valueArr) {
                                selectMultipleValue.append(value).append(" ");
                            }
                            kvContentList.add(
                                    new NotificationService.KvContent(
                                            NotificationService.KvContentType.TEXT,
                                            ticketFormItemDataDto.getItemLabel(),
                                            selectMultipleValue.toString(),
                                            "",
                                            "",
                                            "")
                            );
                            break;
                        case TIMESPAN:
                            try {
                                List<String> timeSpanValueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), String.class);
                                SimpleDateFormat sdf = new SimpleDateFormat(configObj.get("dateFormat"));
                                Date startTime = sdf.parse(timeSpanValueArr.get(0));
                                Date endTime = sdf.parse(timeSpanValueArr.get(1));
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                sdf.format(startTime) + "到" + sdf.format(endTime),
                                                "",
                                                "",
                                                "")
                                );
                            } catch (Exception e) {
                                log.error("时间格式转换异常", e);
                            }
                            break;
                        case TIME:
                            try {
                                String timeValue = ticketFormItemDataDto.getItemValue();
                                SimpleDateFormat sdf = new SimpleDateFormat(configObj.get("dateFormat"));
                                Date time = sdf.parse(timeValue);
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                sdf.format(time),
                                                "",
                                                "",
                                                "")
                                );
                            } catch (Exception e) {
                                log.error("时间格式转换异常", e);
                            }
                            break;
                        case PERSON:
                            try {
                                List<Object> deptValueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), Object.class);
                                StringBuilder sb = null;
                                if (CollectionUtils.isNotEmpty(deptValueArr)) {
                                    for (var obj : deptValueArr) {
                                        JSONObject jObject = (JSONObject) JSONObject.toJSON(obj);
                                        if (sb == null) {
                                            sb = new StringBuilder(jObject.get("accountName").toString());
                                        } else {
                                            sb.append("，" + jObject.get("accountName").toString());
                                        }
                                    }
                                }
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                sb == null ? "" : sb.toString(),
                                                "",
                                                "",
                                                "")
                                );
                            } catch (Exception e) {
                                log.error("人员格式转换异常", e);
                            }
                            break;
                        case DEPT:
                            try {
                                List<Object> deptValueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), Object.class);
                                StringBuilder sb = null;
                                if (CollectionUtils.isNotEmpty(deptValueArr)) {
                                    for (var obj : deptValueArr) {
                                        JSONObject jObject = (JSONObject) JSONObject.toJSON(obj);
                                        if (sb == null) {
                                            sb = new StringBuilder(jObject.get("deptName").toString());
                                        } else {
                                            sb.append("，" + jObject.get("deptName").toString());
                                        }
                                    }
                                }
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                sb == null ? "" : sb.toString(),
                                                "",
                                                "",
                                                "")
                                );
                            } catch (Exception e) {
                                log.error("部门格式转换异常", e);
                            }
                            break;
                        case PICTURE:
                            kvContentList.add(new NotificationService.KvContent(
                                    NotificationService.KvContentType.LINK,
                                    ticketFormItemDataDto.getItemLabel(),
                                    "查看图片..",
                                    jumpUrl,
                                    "",
                                    "")
                            );
                            break;
                        case LINK:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看链接..", ticketFormItemDataDto.getItemValue(), "", ""));
                            break;
                        case FILE:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看附件..", jumpUrl, "", ""));
                            break;
                        case GROUP:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看明细..", jumpUrl, "", ""));
                            break;
                        case TABLE:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看表格..", jumpUrl, "", ""));
                            break;
                        default:
                            kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                            break;
                    }
                }
            }
        }
        return kvContentList;
    }

    public static List<NotificationService.KvContent> BuildContentList(List<TicketFormItemData> ticketFormItemDataList, String jumpUrl) {
        List<TicketFormItemDataDto> ticketFormItemDataDtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ticketFormItemDataList)) {
            ticketFormItemDataList.forEach(x -> {
                ticketFormItemDataDtoList.add(new TicketFormItemDataDto(x));
            });
        }
        return BuildKvContentList(ticketFormItemDataDtoList, jumpUrl);
    }
}
