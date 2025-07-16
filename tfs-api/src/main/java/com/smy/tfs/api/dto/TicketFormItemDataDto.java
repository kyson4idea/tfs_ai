package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFormItemData;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import com.smy.tfs.common.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 工单单组件数据对象 ticket_form_item_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Getter @Setter
public class TicketFormItemDataDto  implements Serializable{
    private static final long serialVersionUID = -2875677689224702134L;
    /**     * $column.columnComment     */
    private String id;

    /**     * 工单数据ID     */
    private String ticketDataId;

    /**     * 工单表单数据ID     */
    private String ticketFormDataId;

    private String templateId;

    /**     * 组件排序     */
    private int itemOrder;

    private String itemParentId;

    /**     * 组件类型     */
    private FormItemTypeEnum itemType;

    /**     * 组件名称     */
    private String itemLabel;
    /**
     * 类型配置 例如日期format等
     */
    private String itemConfig;

    /**     * 组件值     */
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

    /**     * $column.columnComment     */
    private Date deleteTime;

    private String createBy;

    /**     * 创建时间     */
    private Date createTime;

    /**     * 更新者     */
    private String updateBy;

    /**     * 更新时间     */
    private Date updateTime;

    public  TicketFormItemDataDto(){

    }
    public TicketFormItemDataDto(TicketFormItemData ticketFormItemData){
        this.id=ticketFormItemData.getId();
        this.itemParentId=ticketFormItemData.getItemParentId();
        this.ticketDataId=ticketFormItemData.getTicketDataId();
        this.ticketFormDataId=ticketFormItemData.getTicketFormDataId();
        this.templateId=ticketFormItemData.getTemplateId();
        this.itemOrder=ticketFormItemData.getItemOrder();
        this.itemType=ticketFormItemData.getItemType();
        this.itemLabel=ticketFormItemData.getItemLabel();
        this.itemConfig = ticketFormItemData.getItemConfigExt();
        ticketFormItemData.EqConfig();
        this.itemValue=ticketFormItemData.getItemValue();
        this.itemTips=ticketFormItemData.getItemTips();
        this.deleteTime=ticketFormItemData.getDeleteTime();
        this.createBy=ticketFormItemData.getCreateBy();
        this.createTime=ticketFormItemData.getCreateTime();
        this.updateBy=ticketFormItemData.getUpdateBy();
        this.updateTime=ticketFormItemData.getUpdateTime();
    }
}
