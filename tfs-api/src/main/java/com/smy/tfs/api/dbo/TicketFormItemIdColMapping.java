package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * （表单项id和对应的列名映射关系表）
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
@Getter
@Setter
@TableName("ticket_form_item_id_col_mapping")
public class TicketFormItemIdColMapping extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -5853987993572702701L;
    private String id;

    /**
     * 工单模版ID
     */
    private String ticketTemplateId;

    /**
     * 组件id
     */
    private String formItemId;

    /**
     * 表单项id值对应的列名，例如form_item_value1
     */
    private String formItemValueCol;

    /**
     * 版本
     */
    private Integer version;


}
