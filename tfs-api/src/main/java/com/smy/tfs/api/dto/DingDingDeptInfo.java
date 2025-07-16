package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DingDingDeptInfo implements Serializable {
    private static final long serialVersionUID = 3152417767135410310L;
    private Long deptId;
    private String deptName;
    private Long parentId;
}
