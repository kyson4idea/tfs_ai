package com.smy.tfs.api.constants;

import java.io.Serializable;

public class QWCardMD implements Serializable {
    private static final long serialVersionUID = 5877449289305893082L;

    //企微群卡片
    public static final String qwGroupCardStr =
            "工单平台为您创建工单跟进群，工单号:<font color=\"info\"> {{ticket_id}} </font>`{{app_name}}`  \n" +
                    " \n"+
                    "**内    容**  \n" +
                    ">工单名称：{{ticket_name}}  \n" +
                    "{{deal_content}}" +
                    "\n" +
                    "{{apply_user}}于<font color=\"comment\">{{apply_time}}</font>发起，请相关同学跟进处理。[查看详情]({{ticket_detail_url}})  ";

    //企微群卡片By Ticket App 发送失败处理消息
    public static final String qwGroupCardFailedStrByTicketApp =
                    "**内    容**  \n" +
                    ">工单号：{{ticket_id}}  \n" +
                    ">工单名称：{{ticket_name}}  \n" +
                    "{{apply_user}}于<font color=\"comment\">{{apply_time}}</font>发起，\n" +
                    "【{{node_name}}】接口回调失败，请相关同学跟进处理。[查看详情]({{ticket_detail_url}})  ";

    //企微群卡片By Ticket App，發送成功通知消息
    public static final String qwGroupCardSuccessStrByTicketApp =
                    "**内    容**  \n" +
                    ">工单号：{{ticket_id}}  \n" +
                    ">工单名称：{{ticket_name}}  \n" +
                    "【{{node_name}}】所有接口回调成功，[查看详情]({{ticket_detail_url}})  ";

    //企微审批卡片
    public static final String qwDealCardStr =
            "您好，你有新的工单:<font color=\"info\"> {{ticket_id}} </font>`{{app_name}}` 待处理  \n" +
                    "**内    容**  \n" +
                    ">工单名称：{{ticket_name}}  \n" +
                    "{{deal_content}}" +
                    "\n" +
                    "{{apply_user}}于<font color=\"comment\">{{apply_time}}</font>发起， [查看详情]({{ticket_detail_url}})";

}

