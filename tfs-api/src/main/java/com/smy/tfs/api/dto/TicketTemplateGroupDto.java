package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TicketTemplateGroupDto implements Serializable {
    private static final long serialVersionUID = 8073293924150830530L;
    private String appId;
    private String appName;
    private List<TicketTemplateDto> ticketTemplateDtoList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date appCreateTime;
}
