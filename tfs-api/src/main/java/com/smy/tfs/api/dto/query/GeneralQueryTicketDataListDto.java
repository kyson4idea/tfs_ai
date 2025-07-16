package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class GeneralQueryTicketDataListDto implements Serializable {
    private static final long serialVersionUID = -8278102930586043948L;
    //工单编号
    private String id;
    //工单名称
    private String ticketName;
    //所属业务
    private String appName;
    //工单模版
    private String ticketTemplateName;
    //工单状态
    private String ticketStatus;
    //创建时间,格式"yyyy-MM-dd HH:mm:ss"
    private String createTime;
    //结单时间,格式"yyyy-MM-dd HH:mm:ss"
    private String ticketFinishTime;
    //申请人
    private String applyUser;
    //受理人
    private String currentDealUsers;
    //extend1
    private String extend1;
    ///extend2
    private String extend2;
    //extend3
    private String extend3;
    //extend4
    private String extend4;
    //extend5
    private String extend5;
    //extend6
    private String extend6;
    //extend7
    private String extend7;
    //extend8
    private String extend8;
    //extend9
    private String extend9;
    //extend10
    private String extend10;

}
