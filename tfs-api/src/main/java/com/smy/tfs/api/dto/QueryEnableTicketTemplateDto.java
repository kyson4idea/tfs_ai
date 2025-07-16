package com.smy.tfs.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class QueryEnableTicketTemplateDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 应用ID
     */
    private String appId;
    /**
     * 模版状态
     */
    private String ticketStatus;
    /**
     * 是否支持查询订阅应用
     */
    private boolean supportBeyondApps = true;

}
