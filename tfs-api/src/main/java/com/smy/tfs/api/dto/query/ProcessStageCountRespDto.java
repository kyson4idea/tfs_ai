package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessStageCountRespDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pendingCount;  // 待处理工单数量

    private Long ongoingCount;  // 处理中工单数量

    private Long completedCount; // 已完结工单数量

}
