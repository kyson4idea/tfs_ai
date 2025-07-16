package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.enums.BusiQueryUserDealTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SlaTagsQueryReqDto implements Serializable {
    private static final long serialVersionUID = 6943427197542900311L;

    // 工单所属模版列表
    private List<String> templateIdList;

    // 筛选更新开始时间
    private String updateStartTime;

    // 筛选更新结束时间
    private String updateEndTime;

    // tags列表
    private List<String> tagList;

    private Integer pageNum = 0;

    private Integer pageSize = 10;
    //排序描述实体列表
    private List<SortDescriptor> sortDescriptorList;


}
