package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.FormItemAdvancedSearchEnum;
import com.smy.tfs.api.enums.FormItemRequiredEnum;
import com.smy.tfs.api.enums.FormItemTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 工单表单组件数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
@Getter
@Setter
@TableName("ticket_form_item_data")
public class TicketFormItemData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -848695429968019151L;
    private String id;

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
    @Deprecated
    private String itemConfig;

    /**
     * 扩展组件配置,统一用itemConfigExt（text）代替itemConfig(varchar(5000))，
     */
    private String itemConfigExt;

    public boolean EqConfig() {
        try {
            log.info("id:{}, ticketDataID:{}, this.getItemConfigExt EqConfig result:{}, this.getItemConfig():{}, itemConfigExt:{}", id, ticketDataId, Objects.equals(this.itemConfig, this.getItemConfigExt()), itemConfig, this.getItemConfigExt());
            log.info("id:{}, ticketDataID:{}, old new EqConfig result:{}, old:{}, new:{}", id, ticketDataId, Objects.equals(this.itemConfig, itemConfigExt), this.itemConfig, itemConfigExt);
        } catch (Exception e) {
        }
        return Objects.equals(this.itemConfig, itemConfigExt);
    }

    /**
     * 组件值
     */
    private String itemValue;

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

    public TicketFormItemData() {

    }

    public TicketFormItemData(
            TicketFormItemTemplate ticketFormItemTemplate,
            String value,
            String id,
            String itemParentId,
            String ticketDataId,
            String ticketFormDataId) {
        this.id = id;
        this.itemParentId = itemParentId;
        this.ticketDataId = ticketDataId;
        this.ticketFormDataId = ticketFormDataId;
        this.templateId = ticketFormItemTemplate.getId();
        this.itemOrder = ticketFormItemTemplate.getItemOrder();
        this.itemType = ticketFormItemTemplate.getItemType();
        this.itemLabel = ticketFormItemTemplate.getItemLabel();
        //写入数据库
        this.itemConfig = "{}";
        this.itemConfigExt = ticketFormItemTemplate.getItemConfig();
        this.itemValue = value;
        this.itemRequired = ticketFormItemTemplate.getItemRequired();
        this.itemTips = ticketFormItemTemplate.getItemTips();
        this.itemAdvancedSearch = ticketFormItemTemplate.getItemAdvancedSearch();

        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
    }

    public TicketFormItemData(String id,
                              String ticketDataId,
                              String ticketFormDataId,
                              int itemOrder,
                              FormItemTypeEnum itemType,
                              String itemLabel,
                              String itemValue) {
        this.id = id;
        this.ticketDataId = ticketDataId;
        this.ticketFormDataId = ticketFormDataId;
        this.templateId = "-1";//无模版数据
        this.itemParentId = "";
        this.itemOrder = itemOrder;
        this.itemType = itemType;
        this.itemLabel = itemLabel;
        this.itemValue = itemValue;
        this.itemRequired = FormItemRequiredEnum.FALSE;
        this.itemConfig = "{}";
        this.itemConfigExt = "{}";
        this.itemTips = "";
        this.itemAdvancedSearch = FormItemAdvancedSearchEnum.FALSE;


        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
    }

    public String getItemConfigExt() {
        if ( null == itemConfigExt ) {
            return itemConfig;
        }
        return itemConfigExt;
    }
}
