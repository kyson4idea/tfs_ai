package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.enums.TicketAccessPartyEnum;
import com.smy.tfs.api.enums.UserDealTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class FuzzyQueryReqDto implements Serializable {
    private static final long serialVersionUID = -5899972337060766780L;
    //业务ID
    private String appId;
    //模糊搜索值
    private String searchValue;
    //用户处理类型：UserDealTypeEnum枚举
    private UserDealTypeEnum userDealType;
    //工单接入方
    private TicketAccessPartyEnum ticketAccessParty;

    // 是否需要查询总条数
    private boolean needCount;

    private Integer pageNum = 1;

    private Integer pageSize = 10;

}
