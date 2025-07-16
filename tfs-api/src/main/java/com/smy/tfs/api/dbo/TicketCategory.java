package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.CategoryLevelEnum;
import com.smy.tfs.api.enums.CategoryStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单分类表
 * </p>
 *
 * @author yss
 * @since 2024-11-04
 */
@Getter
@Setter
@TableName("ticket_category")
public class TicketCategory extends TfsBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单分类id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 工单分类code
     */
    private String code;

    /**
     * 工单分类名称
     */
    private String name;

    /**
     * 工单分类上级code
     **/
    private String superiorCode;

    /**
     * 工单分类状态
     */
    private CategoryStatusEnum status;

    /**
     * 工单分类层级
     *
     */
    private CategoryLevelEnum categoryLevel;

    /**
     * 所属业务
     *
     */
    private String appId;

    /**
     * 排序
     *
     */
    private Integer sort;

    /**
     * 模版id
     */
    private String templateId;

}
