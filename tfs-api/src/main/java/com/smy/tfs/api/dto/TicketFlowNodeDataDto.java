package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.AuditedMethodEnum;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.api.enums.NodeStatusEnum;
import com.smy.tfs.common.annotation.Excel;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class TicketFlowNodeDataDto implements Serializable {
    private static final long serialVersionUID = -4098778119848353764L;
    /**
     * $column.columnComment
     */
    private String id;

    private String appId;

    //节点名称
    private String nodeName;

    /**
     * 上节点ID, first表示开始节点
     */
    @Excel(name = "上节点ID, first表示开始节点")
    private String preNodeId;

    /**
     * 模版ID
     */
    @Excel(name = "模版ID")
    private String templateId;

    /**
     * 工单数据ID
     */
    @Excel(name = "工单数据ID")
    private String ticketDataId;

    /**
     * 流程数据ID
     */
    @Excel(name = "流程数据ID")
    private String ticketFlowDataId;

    /**
     * 审批方式
     * 会签
     * 或签
     */
    @Excel(name = "审批方式会签 或签")
    private AuditedMethodEnum auditedMethod;

    /**
     * 审批类型
     * 人工审核&自动审核&自动拒绝
     */
    @Excel(name = "审批类型 人工审核&自动审核&自动拒绝")
    private AuditedType auditedType;

    /**
     * 流程节点状态
     * APPROVE_INIT 审批初始化
     * APPROVE_PASS 审批通过
     * APPROVE_REJECT 审批拒绝
     * APPROVING 审批中
     * * @see com.smy.tfs.api.enums.NodeStatusEnum
     */
    private NodeStatusEnum nodeStatus;

    /**
     * 节点顺序
     */
    private int nodeOrder;

    /**
     * 企微审批卡片ID
     */
    private String nodeWxDealCardCode;

    private String nodeWxDealCardMessageId;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "删除时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date deleteTime;

    //动作列表
    private List<TicketFlowNodeActionDataDto> actionList;

    //执行人/抄送人 列表
    private List<TicketFlowNodeExecutorDataDto> excutorList;

    //审批明细列表
    private List<TicketFlowNodeApproveDetailDto> approveDetailList;

    /**
     * 当前节点可修改的字段
     */
    private String modifyFieldList;


    public TicketFlowNodeDataDto() {
    }

    public TicketFlowNodeDataDto(TicketFlowNodeData ticketFlowNodeData) {
        this.id = ticketFlowNodeData.getId();
        this.nodeName = ticketFlowNodeData.getNodeName();
        this.preNodeId = ticketFlowNodeData.getPreNodeId();
        this.templateId = ticketFlowNodeData.getTemplateId();
        this.ticketDataId = ticketFlowNodeData.getTicketDataId();
        this.ticketFlowDataId = ticketFlowNodeData.getTicketFlowDataId();
        this.auditedMethod = ticketFlowNodeData.getAuditedMethod();
        this.auditedType = ticketFlowNodeData.getAuditedType();
        this.nodeStatus = ticketFlowNodeData.getNodeStatus();
        this.nodeOrder = ticketFlowNodeData.getNodeOrder();
        this.nodeWxDealCardCode = ticketFlowNodeData.getNodeWxDealCardCode();
        this.nodeWxDealCardMessageId = ticketFlowNodeData.getNodeWxDealCardMessageId();
        this.deleteTime = ticketFlowNodeData.getDeleteTime();
        this.modifyFieldList = ticketFlowNodeData.getModifyFieldList();
    }

    public  TicketFlowNodeData ToTicketFlowNodeData(){
        TicketFlowNodeData ticketFlowNodeData=new TicketFlowNodeData();
        ticketFlowNodeData.setId(this.id);
        ticketFlowNodeData.setNodeName(this.nodeName);
        ticketFlowNodeData.setPreNodeId(this.preNodeId);
        ticketFlowNodeData.setTemplateId(this.templateId);
        ticketFlowNodeData.setTicketDataId(this.ticketDataId);
        ticketFlowNodeData.setTicketFlowDataId(this.ticketFlowDataId);
        ticketFlowNodeData.setAuditedMethod(this.auditedMethod);
        ticketFlowNodeData.setAuditedType(this.auditedType);
        ticketFlowNodeData.setNodeStatus(this.nodeStatus);
        ticketFlowNodeData.setNodeOrder(this.nodeOrder);
        ticketFlowNodeData.setNodeWxDealCardCode(this.nodeWxDealCardCode);
        ticketFlowNodeData.setNodeWxDealCardMessageId(this.nodeWxDealCardMessageId);
        ticketFlowNodeData.setDeleteTime(this.deleteTime);
        return ticketFlowNodeData;
    }

    /**
     * 获取节点执行人列表
     *
     * @return
     */
    public List<AccountInfo> getDealUserList() {
        if (CollectionUtils.isEmpty(excutorList)) {
            return new ArrayList<>();
        }
        Map<String, AccountInfo> accountInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(excutorList)) {
            for (var item : excutorList) {
                switch (item.getExecutorType()) {
                    case APPLY_MEMBER_LIST:
                    case APPLY_GROUP:
                    case APPLY_LEADER:
                    case APPLY_SELF:
                    case APPLY_POINT:
                    case APPLY_DEPT_MANAGERS:
                    case APPLY_DEPT_POINT:
                    case APPLY_EXTERNAL_APPROVER:
                        //初始化时已设置好，此处直接取值即可
                        List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(item.getExecutorList());
                        for (AccountInfo accountInfo : accountInfoList) {
                            accountInfoMap.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return new ArrayList<>(accountInfoMap.values());
    }



    /**
     * 获取审批拒绝抄送人列表
     *
     * @return
     */
    public List<AccountInfo> getRejectCCUserList() {
        Map<String, AccountInfo> accountInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(this.excutorList)) {
            for (var item : this.excutorList) {
                switch (item.getExecutorType()) {
                    case CE_LEADER:
                    case CE_DEPT_MANAGERS:
                    case CE_GROUP:
                    case CE_MEMBER_LIST:
                    case CE_SELF:
                    case CE_EXTERNAL_APPROVER:
                        //初始化时已设置好，此处直接取值即可
                        List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(item.getExecutorList());
                        for (AccountInfo accountInfo : accountInfoList) {
                            accountInfoMap.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return new ArrayList<>(accountInfoMap.values());
    }



    public List<AccountInfo> getAgreeCCUserList( ) {
        Map<String, AccountInfo> accountInfoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(this.excutorList)) {
            for (var item : this.excutorList) {
                switch (item.getExecutorType()) {
                    case CA_LEADER:
                    case CA_DEPT_MANAGERS:
                    case CA_GROUP:
                    case CA_MEMBER_LIST:
                    case CA_SELF:
                    case CA_EXTERNAL_APPROVER:
                    case CE_LEADER:
                    case CE_DEPT_MANAGERS:
                    case CE_GROUP:
                    case CE_MEMBER_LIST:
                    case CE_SELF:
                    case CE_EXTERNAL_APPROVER:
                        //初始化时已设置好，此处直接取值即可
                        List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(item.getExecutorList());
                        for (AccountInfo accountInfo : accountInfoList) {
                            accountInfoMap.putIfAbsent(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return new ArrayList<>(accountInfoMap.values());
    }

}
