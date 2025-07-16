package com.smy.tfs.api.dto.dynamic;

import com.smy.tfs.api.enums.FormItemTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 工单单组件数据对象 ticket_form_item_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TicketFormItemDataDynamicDto implements Serializable {

    private static final long serialVersionUID = -6355590427383244928L;
    /**
     * 组件排序
     */
    private int itemOrder;

    /**
     * 组件类型
     */
    private FormItemTypeEnum itemType;

    /**
     * 组件名称
     */
    private String itemLabel;

    /**
     * 组件值
     */
    private String itemValue;

}
