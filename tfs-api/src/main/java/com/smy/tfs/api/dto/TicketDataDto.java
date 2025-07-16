package com.smy.tfs.api.dto;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.api.enums.TicketMsgBuildTypeEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import com.smy.tfs.common.utils.SecurityUtils;
import lombok.Data;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.smy.tfs.api.enums.NodeStatusEnum.APPROVE_REJECT;
import static com.smy.tfs.api.enums.NodeStatusEnum.APPROVING;


@Data
public class TicketDataDto implements Serializable {

    private static final long serialVersionUID = -8469037461666417025L;
    /**
     * 工单ID
     */
    private String id;

    /**
     * 操作id集合
     */
    private List<String> idList;

    /**
     * 工单模版ID
     */
    private String templateId;

    /**
     * 业务号，保障只有一个审批中的工单
     */
    private String ticketBusinessKey;

    /**
     * 工单名称
     */
    private String ticketTemplateName;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 工单状态：     * 草稿中     * 审批中     * 审批结束
     */
    private TicketDataStatusEnum ticketStatus;

    /**
     * 工单状态具体值，为了筛选判断
     */
    private String ticketStatusStr;

    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 说明
     */
    private String description;

    /**
     * 工单模板标识
     */
    private String ticketTemplateCode;

    /**
     * 关联的应用
     */
    private String beyondApps;

    /**
     * 接口生成标识
     */
    private String interfaceKey;

    /**
     * 当前处理节点名称
     */
    private String currentNodeName;

    /**
     * 当前处理节点ID
     */
    private String currentNodeId;

    /*
     * 当前节点处理人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentDealUsers;

    /*
     * 处理完成的人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentDoneUsers;

    /*
     * 所有抄送过的人
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     */
    private String currentCcUsers;


    /**
     * 删除时间 yyyy-MM-dd mm:ss
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;

    /**
     * 创建者
     */
    private String createBy;


    private String applyUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 工单完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ticketFinishTime;

    private TicketMsgBuildTypeEnum ticketMsgBuildType;

    private TicketMsgArriveTypeEnum ticketMsgArriveType;

    /**
     * 工单支持修改标识
     */
    private YESNOEnum ticketFormChangeFlag;

    private String wxChatGroupId;

    /**
     * 当前操作人是否有审批权限
     */
    private boolean hasApprovalAuth;

    /**
     * 基础流程 ["flowId-flowName"...]
     */
    private String baseFlow;

    private String tags;

    private List<String> showTags;

    public TicketDataDto() {
    }

    public TicketDataDto(TicketData ticketData) {
        this.id = ticketData.getId();
        this.templateId = ticketData.getTemplateId();
        this.ticketBusinessKey = ticketData.getTicketBusinessKey();
        this.appId = ticketData.getAppId();
        this.ticketStatus = ticketData.getTicketStatus();
        this.ticketName = ticketData.getTicketName();
        this.description = ticketData.getDescription();
        this.currentNodeName = ticketData.getCurrentNodeName();
        this.currentNodeId = ticketData.getCurrentNodeId();
        this.currentDealUsers = ticketData.getCurrentDealUsers();
        this.currentDoneUsers = ticketData.getCurrentDoneUsers();
        this.currentCcUsers = ticketData.getCurrentCcUsers();
        this.applyUser = ticketData.getApplyUser();
        this.createBy = ticketData.getCreateBy();
        this.createTime = ticketData.getCreateTime();
        this.updateBy = ticketData.getUpdateBy();
        this.updateTime = ticketData.getUpdateTime();
        this.ticketFinishTime = ticketData.getTicketFinishTime();
        this.deleteTime = ticketData.getDeleteTime();
        this.ticketTemplateCode = ticketData.getTicketTemplateCode();
        this.beyondApps = ticketData.getBeyondApps();
        this.interfaceKey = ticketData.getInterfaceKey();
        this.ticketMsgBuildType = ticketData.getTicketMsgBuildType();
        this.ticketMsgArriveType = ticketData.getTicketMsgArriveType();
        this.ticketFormChangeFlag = ticketData.getTicketFormChangeFlag();
        this.wxChatGroupId = ticketData.getWxChatGroupId();
        this.baseFlow = ticketData.getBaseFlow();
        this.tags = ticketData.getTags();
    }

    //流程数据
    private TicketFlowDataDto ticketFlowDataDto;

    //表单数据
    private TicketFormDataDto ticketFormDataDto;

    private TicketAppDto ticketAppDto;

    public String getApplyUserName() {
        if (StringUtils.isEmpty(this.applyUser) || !this.applyUser.contains(":")) {
            return "";
        }
        var accountInfo = AccountInfo.ToAccountInfo(this.applyUser);
        return accountInfo.getAccountName();
    }

    public void buildTags() {
        if (CollectionUtils.isNotEmpty(this.showTags)) {
            return;
        }
        this.showTags = new ArrayList<>();
        if (this.getTicketStatus() != null) {
            switch (this.getTicketStatus()) {
                case INIT:
                    this.showTags.add(TicketDataStatusEnum.INIT.getMsg() + ":info");
                    break;
                case DRAFT:
                    this.showTags.add(TicketDataStatusEnum.DRAFT.getMsg() + ":info");
                    break;
                case APPLYING:
                    this.showTags.add(TicketDataStatusEnum.APPLYING.getMsg() + ":default");
                    break;
                case APPLY_END:
                    this.showTags.add(TicketDataStatusEnum.APPLY_END.getMsg() + ":success");
                    break;
                case REJECT:
                    this.showTags.add(TicketDataStatusEnum.REJECT.getMsg() + ":danger");
                    break;
                case WITHDRAW:
                    this.showTags.add(TicketDataStatusEnum.WITHDRAW.getMsg() + ":warning");
                    break;
            }
        }
        if (StringUtils.isNotEmpty(this.currentNodeId) && this.getTicketFlowDataDto() != null && CollectionUtils.isNotEmpty(this.getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
            List<TicketFlowNodeDataDto> currentNodeList = this.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), this.currentNodeId)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(currentNodeList) && currentNodeList.get(0).getNodeName() != null && currentNodeList.get(0).getNodeName().endsWith("(重审)")) {
                this.showTags.add("重审节点:warning:dark");
            }
        }
        if (StringUtils.isNotEmpty(this.tags)) {
            List<String> tagList = Arrays.asList(this.tags);
            for (String tag : tagList) {
                this.showTags.add(tag + ":danger");
            }
        }
    }


    //数据去重
    public void DealUserDistinct() {
        if (this.getTicketFlowDataDto() == null || CollectionUtils.isEmpty(this.getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
            return;
        }
        List<TicketFlowNodeDataDto> ticketFlowNodeDataDtoList = this.getTicketFlowDataDto().getTicketFlowNodeDataDtoList();
        if (CollUtil.isNotEmpty(ticketFlowNodeDataDtoList)) {
            for (var flowNode : ticketFlowNodeDataDtoList) {
                if (flowNode.getNodeStatus() == APPROVING || flowNode.getNodeStatus() == APPROVE_REJECT) {
                    List<TicketFlowNodeApproveDetailDto> approveDetailDtoList = flowNode.getApproveDetailList();
                    Map<String, String> dealUserMap = new HashMap<>();//审批人type-id:ok
                    Map<String, String> sendUserMap = new HashMap<>(); //审批人type-id:ok
                    if (CollUtil.isNotEmpty(approveDetailDtoList)) {
                        for (var approveDetail : approveDetailDtoList) {
                            switch (approveDetail.getDealType()) {
                                case PASS:
                                case REJECT:
                                    dealUserMap.putIfAbsent(approveDetail.getDealUserType() + "-" + approveDetail.getDealUserId(), "ok");
                                    break;
                                case SEND:
                                    sendUserMap.putIfAbsent(approveDetail.getDealUserType() + "-" + approveDetail.getDealUserId(), "ok");
                                    break;
                            }
                        }
                    }
                    List<TicketFlowNodeExecutorDataDto> excutorList = flowNode.getExcutorList();
                    if (CollUtil.isNotEmpty(excutorList)) {
                        for (TicketFlowNodeExecutorDataDto excutor : excutorList) {
                            switch (excutor.getExecutorType()) {
                                case APPLY_MEMBER_LIST:
                                case APPLY_GROUP:
                                case APPLY_LEADER:
                                case APPLY_SELF:
                                case APPLY_POINT:
                                case APPLY_DEPT_MANAGERS:
                                case APPLY_EXTERNAL_APPROVER:
                                    List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(excutor.getExecutorList());
                                    List<AccountInfo> newAccountInfoList = new java.util.ArrayList<>();
                                    for (AccountInfo accountInfo : accountInfoList) {
                                        if (dealUserMap.containsKey(accountInfo.getAccountType() + "-" + accountInfo.getAccountId())) {
                                            continue;
                                        }
                                        newAccountInfoList.add(accountInfo);
                                    }
                                    excutor.setExecutorList(AccountInfo.ToAccountInfoListStr(newAccountInfoList));
                                    break;
                                case CA_LEADER:
                                case CA_GROUP:
                                case CA_MEMBER_LIST:
                                case CA_SELF:
                                case CA_DEPT_MANAGERS:
                                case CA_EXTERNAL_APPROVER:
                                case CE_LEADER:
                                case CE_GROUP:
                                case CE_MEMBER_LIST:
                                case CE_SELF:
                                case CE_DEPT_MANAGERS:
                                case CE_EXTERNAL_APPROVER:
                                    List<AccountInfo> accountInfoList1 = AccountInfo.ToAccountInfoList(excutor.getExecutorList());
                                    List<AccountInfo> newAccountInfoList1 = new java.util.ArrayList<>();
                                    for (AccountInfo accountInfo : accountInfoList1) {
                                        if (sendUserMap.containsKey(accountInfo.getAccountType() + "-" + accountInfo.getAccountId())) {
                                            continue;
                                        }
                                        sendUserMap.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), "ok");
                                        newAccountInfoList1.add(accountInfo);
                                    }
                                    excutor.setExecutorList(AccountInfo.ToAccountInfoListStr(newAccountInfoList1));
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void DealHasApprovalAuth(String sameOriginId) {
        if (StringUtils.isNotEmpty(sameOriginId)) {
            String sameOriginIdStr = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
            if (this.currentDealUsers != null && this.currentDealUsers.contains(sameOriginIdStr)) {
                this.hasApprovalAuth = true;
                return;
            }
        }
        this.hasApprovalAuth = false;
    }
}