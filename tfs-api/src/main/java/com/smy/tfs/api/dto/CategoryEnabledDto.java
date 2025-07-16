package com.smy.tfs.api.dto;

import com.smy.tfs.api.enums.YESNOEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class CategoryEnabledDto implements Serializable {
    private static final long serialVersionUID = 1L;
    //业务id
    private String appId;

    //工单分类开启标识:"YES":开启，“NO”:关闭
    private YESNOEnum categoryEnabled;

}
