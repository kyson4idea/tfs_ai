package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BusiCommonESQueryReqDto implements Serializable {
    private static final long serialVersionUID = 7932697146676285343L;

    // 工单所属业务列表
    private List<String> appIdList;

    // 工单所属模版列表
    private List<String> templateIdList;

    // 对于系统工单的状态筛选列表
    private List<String> ticketStatusList;

    // 条件列表
    private List<BusiESCompareInfo> busiESCompareInfoList;

    //排序描述实体列表
    private List<SortDescriptor> sortDescriptorList;

    private Integer pageNum = 0;

    private Integer pageSize = 10;
}
