package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFormItemTemplate;
import com.smy.tfs.api.enums.FormItemAdvancedSearchEnum;
import com.smy.tfs.api.enums.FormItemRequiredEnum;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 单组件模版对象 ticket_form_item_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFormItemTemplateDto implements Serializable {

    private static final long serialVersionUID = -6335394980515938281L;
    /**
     * $column.columnComment
     */
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
    private String itemType;

    /**
     * 类型配置
     */
    private String itemConfig;

    /**
     * 组件标题
     */
    private String itemLabel;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 组件显隐控制
     */
    private String itemVisibleRule;

    /**
     * 是否必传
     */
    private Boolean itemRequired;

    /**
     * 提示
     */
    private String itemTips;

    /**
     * 是否支持高级搜索：true:支持；false:不支持
     */
    private Boolean itemAdvancedSearch;


    public TicketFormItemTemplate toTicketFormItemTemplate(TicketFormItemTemplateDto ticketFormItemTemplateDto){
        TicketFormItemTemplate ticketFormItemTemplate = new TicketFormItemTemplate();
        ticketFormItemTemplate.setId(ticketFormItemTemplateDto.getId());
        ticketFormItemTemplate.setTicketTemplateId(ticketFormItemTemplateDto.getTicketTemplateId());
        ticketFormItemTemplate.setTicketFormTemplateId(ticketFormItemTemplateDto.getTicketFormTemplateId());
        ticketFormItemTemplate.setItemParentId(ticketFormItemTemplateDto.getItemParentId());
        ticketFormItemTemplate.setItemOrder(ticketFormItemTemplateDto.getItemOrder());
        ticketFormItemTemplate.setItemConfig(ticketFormItemTemplateDto.getItemConfig());
        ticketFormItemTemplate.setItemLabel(ticketFormItemTemplateDto.getItemLabel());
        ticketFormItemTemplate.setDeleteTime(ticketFormItemTemplateDto.getDeleteTime());
        ticketFormItemTemplate.setItemVisibleRule(ticketFormItemTemplateDto.getItemVisibleRule());
        ticketFormItemTemplate.setItemTips(ticketFormItemTemplateDto.getItemTips());
        String itemType = ticketFormItemTemplateDto.getItemType();
        if (ObjectHelper.isNotEmpty(itemType)) {
            ticketFormItemTemplate.setItemType(FormItemTypeEnum.getEnumByCode(itemType));
        }
        Boolean itemRequired = ticketFormItemTemplateDto.getItemRequired();
        if (ObjectHelper.isNotEmpty(itemRequired)) {
            ticketFormItemTemplate.setItemRequired(FormItemRequiredEnum.getEnumByBoolean(itemRequired));
        }
        Boolean itemAdvancedSearch = ticketFormItemTemplateDto.getItemAdvancedSearch();
        if (ObjectHelper.isNotEmpty(itemAdvancedSearch)) {
            ticketFormItemTemplate.setItemAdvancedSearch(FormItemAdvancedSearchEnum.getEnumByBoolean(itemAdvancedSearch));
        }
        return ticketFormItemTemplate;
    }
    public TicketFormItemTemplateDto (){

    };

    public TicketFormItemTemplateDto (TicketFormItemTemplate ticketFormItemTemplate) {
        Boolean itemRequired = null;
        if (ObjectHelper.isNotEmpty(ticketFormItemTemplate.getItemRequired())) {
            itemRequired = ticketFormItemTemplate.getItemRequired().getBooleanCode();
        }
        Boolean itemAdvancedSearch = null;
        if (ObjectHelper.isNotEmpty(ticketFormItemTemplate.getItemRequired())) {
            itemAdvancedSearch = ticketFormItemTemplate.getItemAdvancedSearch().getBooleanCode();
        }
        String itemType = null;
        if (ObjectHelper.isNotEmpty(ticketFormItemTemplate.getItemType())) {
            itemType = ticketFormItemTemplate.getItemType().getCode();
        }
        this.id = ticketFormItemTemplate.getId();
        this.ticketTemplateId = ticketFormItemTemplate.getTicketTemplateId();
        this.ticketFormTemplateId = ticketFormItemTemplate.getTicketFormTemplateId();
        this.itemParentId = ticketFormItemTemplate.getItemParentId();
        this.itemOrder = ticketFormItemTemplate.getItemOrder();
        this.itemConfig = ticketFormItemTemplate.getItemConfig();
        this.itemLabel = ticketFormItemTemplate.getItemLabel();
        this.deleteTime = ticketFormItemTemplate.getDeleteTime();
        this.itemVisibleRule = ticketFormItemTemplate.getItemVisibleRule();
        this.itemTips = ticketFormItemTemplate.getItemTips();
        this.itemType = itemType;
        this.itemRequired = itemRequired;
        this.itemAdvancedSearch = itemAdvancedSearch;

    }

}
