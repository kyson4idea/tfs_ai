package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeTemplate;
import com.smy.tfs.api.enums.AuditedMethodEnum;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.common.annotation.Excel;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工单流程节点模版对象 ticket_flow_node_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowNodeTemplateDto implements Serializable {

    private static final long serialVersionUID = 2999336188586707060L;
    /**
     * $column.columnComment
     */
    private String id;

    private String nodeName;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String preNodeId;

    /**
     * $column.columnComment
     */
    private String ticketDataId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String ticketFlowTemplateId;

    private String ticketTemplateId;

    /** 审批方式
     会签
     或签 */
    @Excel(name = "审批方式 会签 或签")
    private String auditedMethod;

    /** 审批类型
    人工审核&自动审核&自动拒绝 */
    @Excel(name = "审批类型 人工审核&自动审核&自动拒绝")
    private String auditedType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date deleteTime;

    /**
     * 节点顺序
     */
    private Integer nodeOrder;

    /**
     * 当前节点可修改的字段
     */
    private String modifyFieldList;

//    //节点规则类型：静态规则static 动态规则dynamic
//    private String flowNodeRuleType;

    private List<TicketFlowEventTemplateDto> ticketFlowEventTemplateDtoList;

    private List<TicketFlowNodeRuleTemplateDto> ticketFlowNodeRuleTemplateDtoList;

    private List<TicketFlowNodeExecutorTemplateDto> ticketFlowNodeExecutorTemplateDtoList;

    private List<TicketFlowNodeActionTemplateDto> ticketFlowNodeActionTemplateDtoList;

    public TicketFlowNodeTemplate toTicketFlowNodeTemplate(TicketFlowNodeTemplateDto ticketFlowNodeTemplateDto){
        AuditedMethodEnum auditedMethod = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeTemplateDto.getAuditedMethod())) {
            auditedMethod = AuditedMethodEnum.getEnumByCode(ticketFlowNodeTemplateDto.getAuditedMethod());
        }
        AuditedType auditedType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeTemplateDto.getAuditedType())) {
            auditedType = AuditedType.getEnumByCode(ticketFlowNodeTemplateDto.getAuditedType());
        }
        TicketFlowNodeTemplate ticketFlowNodeTemplate = new TicketFlowNodeTemplate();
        ticketFlowNodeTemplate.setId(ticketFlowNodeTemplateDto.getId());
        ticketFlowNodeTemplate.setNodeName(ticketFlowNodeTemplateDto.getNodeName());
        ticketFlowNodeTemplate.setPreNodeId(ticketFlowNodeTemplateDto.getPreNodeId());
        ticketFlowNodeTemplate.setTicketFlowTemplateId(ticketFlowNodeTemplateDto.getTicketFlowTemplateId());
        ticketFlowNodeTemplate.setTicketTemplateId(ticketFlowNodeTemplateDto.getTicketTemplateId());
        ticketFlowNodeTemplate.setAuditedMethod(auditedMethod);
        ticketFlowNodeTemplate.setAuditedType(auditedType);
        ticketFlowNodeTemplate.setNodeOrder(ticketFlowNodeTemplateDto.getNodeOrder());
        ticketFlowNodeTemplate.setModifyFieldList(ticketFlowNodeTemplateDto.getModifyFieldList());
        return ticketFlowNodeTemplate;

    }
    public TicketFlowNodeTemplateDto() {

    }

    public TicketFlowNodeTemplateDto(TicketFlowNodeTemplate ticketFlowNodeTemplate) {
        String auditedMethod = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeTemplate.getAuditedMethod())) {
            auditedMethod = ticketFlowNodeTemplate.getAuditedMethod().getCode();
        }
        String auditedType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeTemplate.getAuditedType())) {
            auditedType = ticketFlowNodeTemplate.getAuditedType().getCode();
        }

        this.id = ticketFlowNodeTemplate.getId();
        this.nodeName = ticketFlowNodeTemplate.getNodeName();
        this.preNodeId = ticketFlowNodeTemplate.getPreNodeId();
        this.ticketFlowTemplateId = ticketFlowNodeTemplate.getTicketFlowTemplateId();
        this.ticketTemplateId = ticketFlowNodeTemplate.getTicketTemplateId();
        this.auditedMethod = auditedMethod;
        this.auditedType = auditedType;
        this.nodeOrder = ticketFlowNodeTemplate.getNodeOrder();
        this.modifyFieldList = ticketFlowNodeTemplate.getModifyFieldList();

    }
}
