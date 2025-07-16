package com.smy.tfs.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SyncExtendFieldsDto implements Serializable {
    private static final long serialVersionUID = 3314016194399556697L;

    //工单所属模版列表
    private String ticketTemplateCode;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页显示记录数
     */
    private Integer pageSize = 5000;



}
