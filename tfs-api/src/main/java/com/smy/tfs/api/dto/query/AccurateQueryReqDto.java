package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.dto.TicketFormItemAttriDto;
import com.smy.tfs.api.enums.TicketAccessPartyEnum;
import com.smy.tfs.api.enums.UserDealTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccurateQueryReqDto implements Serializable {
    private static final long serialVersionUID = 6943427197542900318L;
    //用户处理类型：UserDealTypeEnum枚举
    private UserDealTypeEnum userDealType;
    //工单所属模版列表
    private List<String> templateIdList;
    // 工单分类Id列表字符串
    private List<String> categoryIdList;
    //工單id
    private String ticketDataId;
    //申请人
    private String applyUser;
    //处理人
    private String currentDealUser;
    //对于系统工单的状态筛选列表
    private List<String> ticketStatusList;
    //工单标识列表
    private List<String> tagsList;
    //工單申请方式列表
    private List<String> applyTicketWaysList;
    //创建时间范围：["2025-01-04 23:59:59","2025-01-01 00:00:00"]
    private String[] createTime;
    // 工单模版表单项的List
    private List<TicketFormItemAttriDto> formItemList;
    //工单接入方
    private TicketAccessPartyEnum ticketAccessParty;

    private Integer pageNum = 0;

    private Integer pageSize = 10;


}
