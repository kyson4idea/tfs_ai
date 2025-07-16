package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class SortDescriptor implements Serializable {
    private static final long serialVersionUID = 8732710975646672726L;
    // 排序字段
    private String field;

    // 排序方向 (asc/desc)
    private String direction;

    // 构造方法
    public SortDescriptor() {
    }

    public SortDescriptor(String field, String direction) {
        this.field = field;
        this.direction = direction;
    }

}