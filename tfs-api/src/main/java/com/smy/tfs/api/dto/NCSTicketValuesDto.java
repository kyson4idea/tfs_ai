package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class NCSTicketValuesDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @JsonAlias({"apply_id", "applyId"})
    private String applyId;

    @JsonAlias({"form_item_value1", "formItemValue1"})
    private String formItemValue1;

    @JsonAlias({"form_item_value2", "formItemValue2"})
    private String formItemValue2;
}
