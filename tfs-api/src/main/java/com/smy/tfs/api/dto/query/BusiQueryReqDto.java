package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.enums.BusiQueryUserDealTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BusiQueryReqDto implements Serializable {
    private static final long serialVersionUID = 6943427197542900321L;

    // 模糊搜索值
    private String searchValue;

    // 工单所属业务列表
    private List<String> appIdList;

    // 工单所属模版列表
    private List<String> templateIdList;

    // 对于系统工单的状态筛选列表
    private List<String> ticketStatusList;

    // 筛选创建开始时间
    private String createStartTime;

    // 筛选创建结束时间
    private String createEndTime;

    // 筛选结单开始时间
    private String finishStartTime;

    // 筛选结单结束时间
    private String finishEndTime;

    // 申请人
    private List<String> applyUserList;

    // 处理人
    private List<String> currentDealUserList;

    // 工单分类code列表字符串
    private List<Integer> categoryIdList;

    // 筛选更新开始时间
    private String updateStartTime;

    // 筛选更新结束时间
    private String updateEndTime;

    /**
     * 客户号
     */
    private String extend1;

    /**
     * 联系方式：手机号
     */
    private String extend2;


    // 是否需要查询总条数
    private boolean needCount;

    private Integer pageNum = 0;

    private Integer pageSize = 10;

    //处理类型
    private BusiQueryUserDealTypeEnum userDealType;

    // 条件列表
    private List<List<BusiCompareInfo>> busiCompareInfoList;

    //排序描述实体列表
    private List<SortDescriptor> sortDescriptorList;


}
