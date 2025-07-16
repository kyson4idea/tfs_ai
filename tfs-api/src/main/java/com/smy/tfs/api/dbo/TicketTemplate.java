package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import com.smy.tfs.api.enums.TicketMsgBuildTypeEnum;
import com.smy.tfs.api.enums.TicketTemplateStatusEnum;
import com.smy.tfs.api.enums.YESNOEnum;
import lombok.Data;

import java.io.Serializable;


/**
 * <p>
 * 工单模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Data
@TableName("ticket_template")
public class TicketTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -3342983098919986742L;
    /**
     * 工单ID
     */
    private String id;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 模版状态
     */
    private TicketTemplateStatusEnum ticketStatus;

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
     * 工单消息生成方式
     */
    private TicketMsgBuildTypeEnum ticketMsgBuildType;


    /**
     * 工单消息触达方式
     */
    private TicketMsgArriveTypeEnum ticketMsgArriveType;


    /**
     * 工单支持修改标识
     */
    private YESNOEnum ticketFormChangeFlag;


    /**
     * 工单支持时效标识
     */
    private YESNOEnum ticketAgingFlag;

    /**
     * 工单创建超时时长(单位小时)
     */
    private Integer ticketAgingTime;

    /**
     * 工单处理超时时长(单位小时)
     */
    private Integer ticketDealTime;

    /**
     * 显示部门名称
     */
    private YESNOEnum showDeptNameFlag;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 工单发起类型["jssdk","api","pc"]
     */
    private String applyTicketWays;

}
