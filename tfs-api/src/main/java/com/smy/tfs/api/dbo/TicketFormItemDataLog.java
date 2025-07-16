package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.*;
import com.smy.tfs.api.enums.FormItemAdvancedSearchEnum;
import com.smy.tfs.api.enums.FormItemRequiredEnum;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author g90238
 */
@Data
@TableName("ticket_form_item_data_log")
public class TicketFormItemDataLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 工单组件表ID（ticket_form_item_data中的id）
     */
    private String ticketFormItemDataId;

    /**
     * 工单数据ID
     */
    private String ticketDataId;

    /**
     * 工单表单数据ID
     */
    private String ticketFormDataId;

    /**
     * 模板ID
     */
    private String templateId;


    /**
     * 组件父ID
     */
    private String itemParentId;

    /**
     * 组件排序
     */
    private int itemOrder;

    /**
     * 组件类型 input:单行文本	textarea:多行文本 inputNumber:数字inputMoney:金额select:单选selectMultiple:多选time日期 timeSpan:日期区间picture:图片file:附件phone:电话group:明细
     */
    private FormItemTypeEnum itemType;

    /**
     * 组件名称
     */
    private String itemLabel;


    /**
     * 类型配置 例如日期format等
     */
    private String itemConfig;

//    /**
//     * 扩展组件配置,统一用itemConfigExt（text）代替itemConfig(varchar(5000))，
//     */
//    private String itemConfigExt;

    /**
     * 组件值
     */
    private String itemValue;

    /**
     * 是否必传
     */
    private String itemRequired;

    /**
     * 提示
     */
    private String itemTips;

    /**
     * 是否支持高级搜索：true:支持；false:不支持
     */
    private String itemAdvancedSearch;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 操作类型
     */
    @TableField(fill = FieldFill.INSERT)
    private String operType;

    /**
     * 操作时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date operTime;

    private String operId;

    /**
     * 操作人
     */
    private String operBy;

}
