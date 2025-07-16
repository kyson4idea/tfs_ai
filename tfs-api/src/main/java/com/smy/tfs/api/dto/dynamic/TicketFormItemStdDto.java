package com.smy.tfs.api.dto.dynamic;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;

@Getter
@Setter
public class TicketFormItemStdDto implements Serializable {

    private static final long serialVersionUID = -1052875911115801506L;

    public TicketFormItemStdDto(String templateId, String value) {
        this.templateId = templateId;
        this.value = value;
        this.type = "INPUT";
    }

    public TicketFormItemStdDto(String templateId, String value, String type) {
        this.templateId = templateId;
        this.value = value;
        this.type = type;
    }

    public TicketFormItemStdDto() {

    }

    @RequestParam(value = "表单字段标识(必填参数)", example = " ", description = "如果传ID，ID必须在表单模版里面存在；如果非纯数字，则会视为新字段在表单中展示。")
    private String templateId;

    @RequestParam(value = "表单字段值(必填参数)", example = " ", description = "表单组件值")
    private String value;

    /**
     * @see com.smy.tfs.api.enums.FormItemTypeEnum
     */
    @RequestParam(value = "表单字段类型(可选参数)", example = " ", description = "默认取INPUT文本类型, 另支持TEXTAREA,FILE,PICTURE,TIME,TIMESPAN,LINK,PANEL等类型")
    private String type;

    @RequestParam(value = "表单字段展示值(可选参数)", example = " ", description = "默认展示value, 展示优先取这个值")
    private String displayValue;

    @RequestParam(value = "是否显示(可选参数)", example = " ", description = "默认展示, NO表示不展示")
    private String displayAble;

    @RequestParam(value = "是否重绘", example = " ", description = "默认根据逻辑判断, YES表示重绘")
    private String renderAble;
}
