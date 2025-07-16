package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BusiTicketDataFieldsMappingDto implements Serializable {
    private static final long serialVersionUID = -9081961132958200317L;
    /**
     * 字段code
     */
    private String FieldCode;

    /**
     * 字段name
     */
    private String FieldName;

    /**
     * 字段type
     */
    private String FieldType;


}
