package com.smy.tfs.api.dto;

import com.smy.tfs.api.enums.CategoryStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

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
@NoArgsConstructor
public class BatchUpdateTicketCategoryStatusDto implements Serializable {

    private static final long serialVersionUID = 5630610384606897794L;

    /**
     * 工单分类id
     */
    private List<Integer> idList;
    /**
     * 状态
     */
    private CategoryStatusEnum status;
}
