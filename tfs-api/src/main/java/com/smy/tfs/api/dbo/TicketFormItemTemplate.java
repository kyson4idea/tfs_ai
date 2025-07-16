package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import com.smy.tfs.api.enums.FormItemAdvancedSearchEnum;
import com.smy.tfs.api.enums.FormItemRequiredEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 表单组件模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_form_item_template")
public class TicketFormItemTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 6477024712270318592L;
    private String id;

    /**
     * 工单模版ID
     */
    private String ticketTemplateId;

    /**
     * 工单表单模版ID
     */
    private String ticketFormTemplateId;

    /**
     * 父ID
     */
    private String itemParentId;

    /**
     * 组件排序
     */
    private Integer itemOrder;

    /**
     * 组件类型：input:单行文本	textarea:多行文本 inputNumber:数字inputMoney:金额select:单选selectMultiple:多选time日期 timeSpan:日期区间picture:图片file:附件phone:电话group:明细
     */
    private FormItemTypeEnum itemType;

    /**
     * 类型配置 例如日期format等
     */
    private String itemConfig;

    /**
     * 当前字段显隐规则
     */
    private String itemVisibleRule;

    /**
     * 表单项标签
     */
    private String itemLabel;

    /**
     * 是否必传
     */
    private FormItemRequiredEnum itemRequired;

    /**
     * 提示
     */
    private String itemTips;

    /**
     * 是否支持高级搜索：true:支持；false:不支持
     */
    private FormItemAdvancedSearchEnum itemAdvancedSearch;

}
