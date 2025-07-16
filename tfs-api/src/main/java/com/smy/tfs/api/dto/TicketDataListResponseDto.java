package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TicketDataListResponseDto implements Serializable {
    private static final long serialVersionUID = 8218417709618523689L;
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
    private TicketDataStatusEnum ticketStatus;

    /**
     * 申请人
     */
    private String applyUser;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 结单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ticketFinishTime;

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
    private TicketMsgArriveTypeEnum ticketMsgArriveType;

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

    /**
     *
     *
     * @param ticketFormItemValues
     */
    public TicketDataListResponseDto toTicketDataListResponseDto(TicketFormItemValues ticketFormItemValues){
        this.id = ticketFormItemValues.getTicketDataId();
        this.ticketStatus = ticketFormItemValues.getTicketStatus();
        this.ticketName = ticketFormItemValues.getTicketName();
        this.templateId = ticketFormItemValues.getTemplateId();
        this.currentDealUsers = ticketFormItemValues.getCurrentDealUsers();
        this.ticketFinishTime = ticketFormItemValues.getTicketFinishTime();
        this.createTime = ticketFormItemValues.getCreateTime();
        this.updateTime = ticketFormItemValues.getUpdateTime();
        this.applyUser = ticketFormItemValues.getApplyUser();
        this.wxChatGroupId = ticketFormItemValues.getWxChatGroupId();
        this.ticketMsgArriveType = ticketFormItemValues.getTicketMsgArriveType();
        this.tags = ticketFormItemValues.getTags();
        return this;
    }

}
