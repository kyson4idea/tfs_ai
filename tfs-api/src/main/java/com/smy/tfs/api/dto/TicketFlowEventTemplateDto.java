package com.smy.tfs.api.dto;

import com.alibaba.fastjson2.JSONObject;
import com.smy.tfs.api.dbo.TicketFlowEventTemplate;
import com.smy.tfs.api.enums.EventTypeEnum;
import com.smy.tfs.api.enums.ExecuteStepEnum;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 工单流程动作模版对象 ticket_flow_event_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowEventTemplateDto implements Serializable {

    private static final long serialVersionUID = 4557109937441359049L;
    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 流程模版ID
     */
    private String ticketFlowNodeTemplateId;

    /**
     * 事件标识
     */
    private String eventTag;

    /**
     * before:执行前
     * doing:执行中
     * done:执行后
     * pass:通过后
     * reject:拒绝后
     */
    private String executeStep;

    /** 删除时间 */
    private Date deleteTime;
    //事件类型 dubboService,httpService
    private String eventType;

    private String eventConfig;

    //工单模版ID
    private String ticketTemplateId;

    //推送配置
    private JSONObject pushConfig;

    public TicketFlowEventTemplate toTicketFlowEventTemplate(TicketFlowEventTemplateDto ticketFlowEventTemplateDto){
        ExecuteStepEnum executeStep = null;
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateDto.getExecuteStep())) {
            executeStep = ExecuteStepEnum.getEnumByCode(ticketFlowEventTemplateDto.getExecuteStep());
        }
        EventTypeEnum eventType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplateDto.getEventType())) {
            eventType = EventTypeEnum.getEnumByCode(ticketFlowEventTemplateDto.getEventType());
        }
        TicketFlowEventTemplate ticketFlowEventTemplate = new TicketFlowEventTemplate();
        ticketFlowEventTemplate.setId(ticketFlowEventTemplateDto.getId());
        ticketFlowEventTemplate.setTicketFlowNodeTemplateId(ticketFlowEventTemplateDto.getTicketFlowNodeTemplateId());
        ticketFlowEventTemplate.setEventTag(ticketFlowEventTemplateDto.getEventTag());
        ticketFlowEventTemplate.setExecuteStep(executeStep);
        ticketFlowEventTemplate.setEventType(eventType);
        ticketFlowEventTemplate.setEventConfig(ticketFlowEventTemplateDto.getEventConfig());
        ticketFlowEventTemplate.setTicketTemplateId(ticketFlowEventTemplateDto.getTicketTemplateId());
        ticketFlowEventTemplate.setPushConfig(JSONObject.toJSONString(ticketFlowEventTemplateDto.getPushConfig()));
        return ticketFlowEventTemplate;

    }

    public TicketFlowEventTemplateDto() {
    }

    public TicketFlowEventTemplateDto(TicketFlowEventTemplate ticketFlowEventTemplate) {
        String executeStep = null;
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplate.getExecuteStep())) {
            executeStep = ticketFlowEventTemplate.getExecuteStep().getCode();
        }
        String eventType = null;
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplate.getEventType())) {
            eventType = ticketFlowEventTemplate.getEventType().getCode();
        }
        this.eventType = eventType;
        this.eventConfig = ticketFlowEventTemplate.getEventConfig();
        this.id = ticketFlowEventTemplate.getId();
        this.ticketFlowNodeTemplateId = ticketFlowEventTemplate.getTicketFlowNodeTemplateId();
        this.executeStep = executeStep;
        this.eventTag = ticketFlowEventTemplate.getEventTag();
        this.ticketTemplateId = ticketFlowEventTemplate.getTicketTemplateId();
        if (ObjectHelper.isNotEmpty(ticketFlowEventTemplate.getPushConfig())) {
            this.pushConfig = JSONObject.parseObject(ticketFlowEventTemplate.getPushConfig());
        }

    }



}
