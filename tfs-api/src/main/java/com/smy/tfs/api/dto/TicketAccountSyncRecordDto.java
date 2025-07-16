package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TicketAccountSyncRecordDto implements Serializable {
    private static final long serialVersionUID = 4589999997948981471L;
    /**
     * 账户配置id
     */
    private String ticketAccountId;

    /**
     * 同步结果
     */
    private String syncResult;

    /**
     * 同步结果描述
     */
    private String syncResultDes;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;
}
