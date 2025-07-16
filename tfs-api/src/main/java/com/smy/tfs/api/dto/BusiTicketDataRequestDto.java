package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

//只给催收用
@Data
public class BusiTicketDataRequestDto implements Serializable {
    private static final long serialVersionUID = 8953961130798996357L;
    //工单所属应用
    private String appId;
    //工单所属模版列表
    private List<String> templateIdList;
    // 筛选创建开始时间
    private Date createStartTime;
    // 筛选创建结束时间
    private Date createEndTime;
    //自然人客户号
    private String extend1;
    //工单类型
    private String extend2;
}
