package com.smy.tfs.api.dto.test;

import lombok.Data;

import java.io.Serializable;

@Data
public class TicketObjectDto implements Serializable {
    private static final long serialVersionUID = -7735826450363100097L;
    //工单所属应用id
    private String appId;
    //工单模版id
    private String templateId;
    //工单分类id
    private String categoryId;
}
