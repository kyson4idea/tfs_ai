package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class NCSTicketExtraDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单ID
     */
    @JsonAlias({"apply_id", "applyId"})
    private String applyId;

    /**
     * 工单内容
     */
    @JsonAlias({"form_items", "formItems"})
    private List<FormItem> formItemList;

    @Data
    public static class FormItem {
        /**
         * 属性名称
         */
        @JsonAlias({"template_id", "templateId"})
        private String templateId;

        /**
         * 属性值
         */
        private String value;
    }
}
