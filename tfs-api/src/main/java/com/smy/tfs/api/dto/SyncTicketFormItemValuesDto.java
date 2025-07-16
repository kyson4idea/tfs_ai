package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class SyncTicketFormItemValuesDto implements Serializable{

    private static final long serialVersionUID = -7664385144691139736L;
    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 开始时间 yyyy-MM-dd mm:ss
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;


}