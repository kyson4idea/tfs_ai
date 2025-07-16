package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.enums.TicketAccessPartyEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AdvancedQueryDto implements Serializable {

    private static final long serialVersionUID = 6736467221646986826L;

    //工单id
    private String ticketDataId;
    //申请人
    private String applyUser;
    //处理人
    private String currentDealUser;

    // 筛选创建开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createStartTime;
    // 筛选创建结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createEndTime;

    // 筛选更新开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateStartTime;

    // 筛选更新结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateEndTime;

    //工单分类
//    private String tags;
//    //工单创建方式
//    private String applyTicketWays;
//    //工单分类
//    private String categoryIdListStr;
    //工单标识列表
    private List<String> tagsList;
    //工單申请方式列表
    private List<String> applyTicketWaysList;

    private List<Integer> categoryIdList;


    //我发起的
    private boolean createdByMe;

    //需要我处理的
    private boolean needHandleByMe;

    //工单所属模版
    private String templateId;

    //分类对应的模版筛选范围
    private List<String> templateIdList;

    //当前操作人
    private String currentUserInfo;

    //对于系统工单的状态筛选
    private String ticketStatusStr;

    //对于系统工单的状态筛选列表
    private List<String> ticketStatusList;

    //对于与人相关的工单状态筛选
    private String ticketStatusForUserStr;

    //模糊搜索值
    private String searchValue;

    // 工单模版表单项的List
    private List<TicketFormItemAttriDto> formItemList;

    private Integer pageNum;

    private Integer pageSize;

    //工单接入方
    private TicketAccessPartyEnum ticketAccessParty;


}
