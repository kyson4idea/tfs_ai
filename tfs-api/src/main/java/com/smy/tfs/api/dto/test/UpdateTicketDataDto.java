package com.smy.tfs.api.dto.test;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateTicketDataDto implements Serializable {
    private static final long serialVersionUID = -7735826450363100097L;
    //工单id
    private String ticketDataId;
    //更新开始时间
    private String updateStartTime;
    //更新结束时间
    private String updateEndTime;
    //应用id
    private String appId;
}
