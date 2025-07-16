package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowTemplate;
import com.smy.tfs.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工单流程模版对象 ticket_flow_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowTemplateDto implements Serializable {

    private static final long serialVersionUID = 5979313375714942098L;
    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 工单模版id
     */
    private String ticketTemplateId;

    /** 开始时抄送

     [
     user_type:””,//用户类型，用户组 上一级，指定账户体系
     user_id:””，//用户ID
     ] */
    @Excel(name = "开始时抄送 [ user_type:””,//用户类型，用户组 上一级，指定账户体系 user_id:””，//用户ID ]")
    private String startCc;

    /** 结束时抄送 */
    @Excel(name = "结束时抄送")
    private String endCc;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date deleteTime;


    private List<TicketFlowNodeTemplateDto> ticketFlowNodeTemplateDtoList;
    
    public TicketFlowTemplate toTicketFlowTemplate (TicketFlowTemplateDto ticketFlowTemplateDto) {
        TicketFlowTemplate ticketFlowTemplate = new TicketFlowTemplate();
        ticketFlowTemplate.setId(ticketFlowTemplateDto.getId());
        ticketFlowTemplate.setTicketTemplateId(ticketFlowTemplateDto.getTicketTemplateId());
        ticketFlowTemplate.setStartCc(ticketFlowTemplateDto.getStartCc());
        ticketFlowTemplate.setEndCc(ticketFlowTemplateDto.getEndCc());
        ticketFlowTemplate.setDeleteTime(ticketFlowTemplateDto.getDeleteTime());
        return ticketFlowTemplate;
    }

    public TicketFlowTemplateDto () {

    }

    public TicketFlowTemplateDto (TicketFlowTemplate ticketFlowTemplate) {
        this.id = ticketFlowTemplate.getId();
        this.ticketTemplateId = ticketFlowTemplate.getTicketTemplateId();
        this.startCc = ticketFlowTemplate.getStartCc();
        this.endCc = ticketFlowTemplate.getEndCc();
    }


}
