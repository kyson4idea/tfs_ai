package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单分类模版关系表
 * </p>
 *
 * @author yss
 * @since 2024-11-04
 */
@Getter
@Setter
@TableName("ticket_category_template")
public class TicketCategoryTemplate extends TfsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单分类id
     */
    private String id;

    /**
     * 工单模版id
     */
    private String templateId;

    /**
     * 一级分类id
     **/
    private String oneCategoryId;

    /**
     * 二级分类id
     */
    private String twoCategoryId;

    /**
     * 三级分类id
     *
     */
    private String threeCategoryId;

}
