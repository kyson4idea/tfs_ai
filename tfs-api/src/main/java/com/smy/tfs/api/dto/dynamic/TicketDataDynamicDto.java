package com.smy.tfs.api.dto.dynamic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public  class TicketDataDynamicDto implements Serializable {
    private static final long serialVersionUID = 3321873987293213251L;
    /* 工单ID */
    private String id;

    /* 应用ID */
    private String appId;

    /* 工单名称 */
    private String ticketName;

    /* 流程数据 */
    private TicketFlowDataDynamicDto ticketFlowDataDynamicDto;

    /* 表单数据 */
    private TicketFormDataDynamicDto ticketFormDataDynamicDto;
}