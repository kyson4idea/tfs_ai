package com.smy.tfs.api.dto;

import com.smy.tfs.api.enums.FormItemTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 工单模版表单项数据 TicketFormItemIdValue
 *
 * @author yss
 * @date 2024-04-11
 */
@Data
public class TicketFormItemAttriDto implements Serializable {
    private static final long serialVersionUID = -1645536871495911351L;
    /** item的id值 */
    private String formItemId;
    /** item的value值 */
    private String formItemValue;
    /** item的Type值 */
    private FormItemTypeEnum formItemType;
    /** item的Label值 */
    private String formItemLabel;

}
