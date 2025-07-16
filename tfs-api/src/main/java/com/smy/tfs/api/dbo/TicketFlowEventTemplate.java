package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.EventTypeEnum;
import com.smy.tfs.api.enums.ExecuteStepEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程动作模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_event_template")
public class TicketFlowEventTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -1368868002874743694L;
    private String id;

    /**
     * 流程模版ID
     */
    private String ticketFlowNodeTemplateId;


    //事件类型 dubboService,httpService
    private EventTypeEnum eventType;

    //事件配置 例如 tfs-core:TicketFlowService.createTicketDynamic
    //约定：
    // createTicketDynamic方法的第一个参数是string类型的签名（sign），第二参数当前数据的eventTag（用户在流程节点模版配置的）,
    // 执行方法的返回值是一个int类型：0表示执行成功，其余表示失败
    private String eventConfig;

    //事件标识，用户在流程节点模版配置的
    private String eventTag;

    /**
     * before:执行前
     * passed:通过后
     * done:执行后
     */
    private ExecuteStepEnum executeStep;

    //工单模版ID
    private String ticketTemplateId;

    //推送配置
    private String pushConfig;

}
