package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import com.smy.tfs.api.enums.EventStatusEnum;
import com.smy.tfs.api.enums.EventTypeEnum;
import com.smy.tfs.api.enums.ExecuteStepEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 工单流程动作数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_event_data")
public class TicketFlowEventData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -192915724348561606L;
    private String id;

    private String ticketDataId;

    /**
     * 1:待执行
     * 2:执行中，异步待确认
     * 3:执行失败，待执行
     * 20:执行成功（终态）
     * 30:执行失败（终态）
     */
    private EventStatusEnum eventStatus;

    /**
     * 执行时间
     */
    private LocalDateTime executeTime;

    private String ticketFlowNodeDataId;

    //事件类型 dubboService,httpService
    private EventTypeEnum eventType;

    //事件配置 例如 tfs-core:TicketFlowService.createTicketDynamic
    //约定： TODO: json格式
    // createTicketDynamic方法的第一个参数是string类型的签名（sign），第二参数当前数据的eventTag（用户在流程节点模版配置的）,
    // 执行方法的返回值是一个int类型：0表示执行成功，其余表示失败
    private String eventConfig;

    //事件标识，用户在流程节点模版配置的
    private String eventTag;

    //事件传输数据，json string格式 ticket_flow_event_data
    private String eventTranData;

    /**
     * before:执行前
     * passed:通过后
     * done:执行后
     */
    private ExecuteStepEnum executeStep;

    private String templateId;
    //推送配置
    private String pushConfig;

    //触发类型
    private ApproveDealTypeEnum approveDealType;

    public TicketFlowEventData() {
    }

    public TicketFlowEventData(TicketFlowEventTemplate template,String id,String ticketDataId,EventStatusEnum eventStatus,String ticketFlowNodeDataId) {
        this.id = id;
        this.ticketDataId = ticketDataId;
        this.eventStatus= eventStatus;
        this.executeTime = null;
        this.ticketFlowNodeDataId = ticketFlowNodeDataId;
        this.eventType = template.getEventType();
        this.eventConfig = template.getEventConfig();
        this.eventTag = template.getEventTag();
        this.executeStep = template.getExecuteStep();
        this.templateId = template.getId();
        this.pushConfig = template.getPushConfig();
        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
    }
}
