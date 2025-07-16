package com.smy.tfs.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 *
 */
@Getter
@AllArgsConstructor
public enum CategoryLevelEnum implements Serializable {
    ONE_LEVEL("ONE_LEVEL", "一级分类"),
    TWO_LEVEL("TWO_LEVEL","二级分类"),
    THREE_LEVEL("THREE_LEVEL","三级分类"),
    FOUR_LEVEL("FOUR_LEVEL","四级分类"),
    FIVE_LEVEL("FIVE_LEVEL","五级分类"),
    ;

    private String code;
    private String desc;
}
