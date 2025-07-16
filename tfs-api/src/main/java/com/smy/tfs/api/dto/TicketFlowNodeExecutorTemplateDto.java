package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeExecutorTemplate;
import com.smy.tfs.api.enums.DefaultExecutorTypeEnum;
import com.smy.tfs.api.enums.DeptLevelEnum;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工单流程节点执行人模版对象 ticket_flow_node_executor_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowNodeExecutorTemplateDto implements Serializable {

    private static final long serialVersionUID = 4050128408580190367L;
    /**
     * $column.columnComment
     */
    private String id;

    /* 工单流程节点模版ID */
    private String ticketFlowNodeTemplateId;

    /* 执行者类型 */
    private String executorType;

    /** 执行者值 */
    private String executorValue;

    private String executorList;


    /** 删除时间 */
    private Date deleteTime;
    //工单模版ID
    private String ticketTemplateId;

//    /**
//     *  部门层级
//     */
//    private DeptLevelEnum deptLevel;

    private DefaultExecutorTypeEnum defaultExecutorType;

    private String defaultExecutorValue;

    /**当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]*/
    private String groupValue;

    public TicketFlowNodeExecutorTemplateDto (){}

    public TicketFlowNodeExecutorTemplate toTicketFlowNodeExecutorTemplate(TicketFlowNodeExecutorTemplateDto ticketFlowNodeExecutorTemplateDto){
        ExecutorTypeEnum executorType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplateDto.getExecutorType())) {
            executorType = ExecutorTypeEnum.getEnumByCode(ticketFlowNodeExecutorTemplateDto.getExecutorType());
        }
        TicketFlowNodeExecutorTemplate ticketFlowNodeExecutorTemplate = new TicketFlowNodeExecutorTemplate();
        ticketFlowNodeExecutorTemplate.setId(ticketFlowNodeExecutorTemplateDto.getId());
        ticketFlowNodeExecutorTemplate.setTicketFlowNodeTemplateId(ticketFlowNodeExecutorTemplateDto.getTicketFlowNodeTemplateId());
        ticketFlowNodeExecutorTemplate.setExecutorType(executorType);
        ticketFlowNodeExecutorTemplate.setExecutorValue(ticketFlowNodeExecutorTemplateDto.getExecutorValue());
        ticketFlowNodeExecutorTemplate.setTicketTemplateId(ticketFlowNodeExecutorTemplateDto.getTicketTemplateId());
//        ticketFlowNodeExecutorTemplate.setDeptLevel(ticketFlowNodeExecutorTemplateDto.getDeptLevel());
        ticketFlowNodeExecutorTemplate.setGroupValue(ticketFlowNodeExecutorTemplateDto.getGroupValue());
        ticketFlowNodeExecutorTemplate.setDefaultExecutorType(ticketFlowNodeExecutorTemplateDto.getDefaultExecutorType());
        ticketFlowNodeExecutorTemplate.setDefaultExecutorValue(ticketFlowNodeExecutorTemplateDto.getDefaultExecutorValue());
        return ticketFlowNodeExecutorTemplate;

    }

    public TicketFlowNodeExecutorTemplateDto(TicketFlowNodeExecutorTemplate ticketFlowNodeExecutorTemplate) {
        String executorType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowNodeExecutorTemplate.getExecutorType())) {
            executorType = ticketFlowNodeExecutorTemplate.getExecutorType().getCode();
        }
        this.id = ticketFlowNodeExecutorTemplate.getId();
        this.ticketFlowNodeTemplateId = ticketFlowNodeExecutorTemplate.getTicketFlowNodeTemplateId();
        this.executorType = executorType;
        this.executorValue = ticketFlowNodeExecutorTemplate.getExecutorValue();
        this.ticketTemplateId = ticketFlowNodeExecutorTemplate.getTicketTemplateId();
//        this.deptLevel = ticketFlowNodeExecutorTemplate.getDeptLevel();
        this.defaultExecutorType = ticketFlowNodeExecutorTemplate.getDefaultExecutorType();
        this.defaultExecutorValue = ticketFlowNodeExecutorTemplate.getDefaultExecutorValue();
        this.groupValue = ticketFlowNodeExecutorTemplate.getGroupValue();
    }

}



