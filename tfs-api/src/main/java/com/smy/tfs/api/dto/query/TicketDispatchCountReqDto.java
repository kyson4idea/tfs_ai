package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TicketDispatchCountReqDto implements Serializable {
    private static final long serialVersionUID = 6943427197542900321L;
    // 业务id
    private String appId;
    // 账户type
    private String accountType;
    // 账户id列表
    private List<String> accountIdList;

    // 筛选更新开始时间
    private String updateStartTime;

    // 筛选更新结束时间
    private String updateEndTime;



}
