package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitStageCountRespDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long newCount;     // 新建工单数量

    private Long returnedCount; // 退回工单数量

    //private Long draftCount;   // 暂存工单数量

}
