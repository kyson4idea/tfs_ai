package com.smy.tfs.api.dto.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class SuperAdminQueryRspDto implements Serializable {
    private static final long serialVersionUID = 7133632729066522755L;

    /**
     * 工单id
     */
    private String id;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 模版id
     */
    private String templateId;

    /**
     * 工单模版类型名称
     */
    private String ticketTemplateName;

    /**
     * 工单状态
     */
    private String ticketStatus;

    /**
     * 申请人
     */
    private String applyUser;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 结单时间
     */
    private String ticketFinishTime;

    /**
     * 受理人
     */
    private String currentDealUsers;

    /**
     * 微信群聊Id
     */
    private String wxChatGroupId;

    /**
     * 通知触达方式
     */
    private String ticketMsgArriveType;

    /**
     * 是否展示催办按钮
     */
    private boolean showReminderButton;

    /**
     * 是否展示建群跟单按钮
     */
    private boolean showFollowButton;


    /**
     * 工单标签
     */
    private String tags;

    private String extend1;

    private String extend2;

    private String extend3;

    private String extend4;

    private String extend5;

    private String extend6;

    private String extend7;

    private String extend8;

    private String extend9;

    private String extend10;

    public SuperAdminQueryRspDto(Map<String,Object> map){
        String appName = "";
        if (Objects.nonNull(map.get("app_name"))) {
            appName = (String)map.get("app_name");
        }
        String templateName = "";
        if (Objects.nonNull(map.get("template_name"))) {
            templateName = (String)map.get("template_name");
        }
        String createTimeStr = "";
        if (Objects.nonNull(map.get("create_time"))) {
            createTimeStr = (String)map.get("create_time");
        }
        String updateTimeStr = "";
        if (Objects.nonNull(map.get("update_time"))) {
            updateTimeStr = (String)map.get("update_time");
        }
        String ticketFinishTimeStr = "";
        if (Objects.nonNull(map.get("ticket_finish_time"))) {
            ticketFinishTimeStr = (String)map.get("ticket_finish_time");
        }
        if (Objects.nonNull(map.get("id"))) {
            this.id = (String)map.get("id");
        }
        if (Objects.nonNull(map.get("app_id"))) {
            this.appId = (String)map.get("app_id");
        }
        this.appName = appName;
        if (Objects.nonNull(map.get("ticket_name"))) {
            this.ticketName = (String)map.get("ticket_name");
        }
        if (Objects.nonNull(map.get("template_id"))) {
            this.templateId = (String)map.get("template_id");
        }
        this.ticketTemplateName = templateName;
        if (Objects.nonNull(map.get("ticket_status"))) {
            this.ticketStatus = (String)map.get("ticket_status");
        }
        if (Objects.nonNull(map.get("apply_user"))) {
            this.applyUser = (String)map.get("apply_user");
        }
        this.createTime = createTimeStr;
        this.updateTime = updateTimeStr;
        this.ticketFinishTime = ticketFinishTimeStr;

        if (Objects.nonNull(map.get("current_deal_users"))) {
            this.currentDealUsers = (String)map.get("current_deal_users");
        }
        if (Objects.nonNull(map.get("wx_chat_group_id"))) {
            this.wxChatGroupId = (String)map.get("wx_chat_group_id");
        }
        if (Objects.nonNull(map.get("ticket_msg_arrive_type"))) {
            this.ticketMsgArriveType = (String)map.get("ticket_msg_arrive_type");
        }
        if (Objects.nonNull(map.get("tags"))) {
            this.tags = (String)map.get("tags");
        }
        if (Objects.nonNull(map.get("extend1"))) {
            this.extend1 = (String)map.get("extend1");
        }
        if (Objects.nonNull(map.get("extend2"))) {
            this.extend2 = (String)map.get("extend2");
        }
        if (Objects.nonNull(map.get("extend3"))) {
            this.extend3 = (String)map.get("extend3");
        }
        if (Objects.nonNull(map.get("extend4"))) {
            this.extend4 = (String)map.get("extend4");
        }
        if (Objects.nonNull(map.get("extend5"))) {
            this.extend5 = (String)map.get("extend5");
        }
        if (Objects.nonNull(map.get("extend6"))) {
            this.extend6 = (String)map.get("extend6");
        }
        if (Objects.nonNull(map.get("extend7"))) {
            this.extend7 = (String)map.get("extend7");
        }
        if (Objects.nonNull(map.get("extend8"))) {
            this.extend8 = (String)map.get("extend8");
        }
        if (Objects.nonNull(map.get("extend9"))) {
            this.extend9 = (String)map.get("extend9");
        }
        if (Objects.nonNull(map.get("extend10"))) {
            this.extend10 = (String)map.get("extend10");
        }
    }

}
