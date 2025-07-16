package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class TicketDataQueryResult implements Serializable {
    private static final long serialVersionUID = 8218417709618523690L;
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
    /**
     * 工单模版表单项的List
     */
    private List<TicketFormItemAttriDto> formItemList;

    private Date craeteTime;

    private Date updateTime;



    /**
     *
     * @param ticketFormItemValues
     */
    public TicketDataQueryResult(TicketFormItemValues ticketFormItemValues){
        this.id = ticketFormItemValues.getTicketDataId();
        this.ticketStatus = ticketFormItemValues.getTicketStatus();
        this.ticketName = ticketFormItemValues.getTicketName();
        this.templateId = ticketFormItemValues.getTemplateId();
        this.currentDealUsers = ticketFormItemValues.getCurrentDealUsers();
        this.ticketFinishTime = ticketFormItemValues.getTicketFinishTime();
        this.createTime = ticketFormItemValues.getCreateTime();
        this.applyUser = ticketFormItemValues.getApplyUser();
        this.wxChatGroupId = ticketFormItemValues.getWxChatGroupId();
        this.ticketMsgArriveType = ticketFormItemValues.getTicketMsgArriveType();
        this.tags = ticketFormItemValues.getTags();
        this.craeteTime = ticketFormItemValues.getCreateTime();
        this.updateTime = ticketFormItemValues.getUpdateTime();
    }

}
