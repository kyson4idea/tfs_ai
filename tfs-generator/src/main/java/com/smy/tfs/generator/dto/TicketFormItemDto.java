package com.smy.tfs.generator.dto;

public class TicketFormItemDto {
    /**
     * 表单组件模版ID
     */
    private  String templateId;

    /**
     * 表单组件值
     */
    private  String value;

    /**
     * 表单组件描述
     */

    private  String description;

    public TicketFormItemDto() {
    }

    public TicketFormItemDto(String templateId, String value, String description) {
        this.templateId = templateId;
        this.value = value;
        this.description = description;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getdescription() {
        return description;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TicketFormItemDto{" +
                "templateId='" + templateId + '\'' +
                ", value='" + value + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
