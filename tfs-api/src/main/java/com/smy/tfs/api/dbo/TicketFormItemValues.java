package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * （表单项col和表单项value对应的平铺表）
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
@Getter
@Setter
@TableName("ticket_form_item_values")
public class TicketFormItemValues extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -7825964542560163166L;
    private String id;

    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 工单模版ID
     */
    private String templateId;

    /**
     * 组件值
     */
    private String formItemValue1;

    /**
     * 组件值
     */
    private String formItemValue2;

    /**
     * 组件值
     */
    private String formItemValue3;

    /**
     * 组件值
     */
    private String formItemValue4;

    /**
     * 组件值
     */
    private String formItemValue5;

    /**
     * 组件值
     */
    private String formItemValue6;

    /**
     * 组件值
     */
    private String formItemValue7;

    /**
     * 组件值
     */
    private String formItemValue8;

    /**
     * 组件值
     */
    private String formItemValue9;

    /**
     * 组件值
     */
    private String formItemValue10;

    /**
     * 组件值
     */
    private String formItemValue11;

    /**
     * 组件值
     */
    private String formItemValue12;

    /**
     * 组件值
     */
    private String formItemValue13;

    /**
     * 组件值
     */
    private String formItemValue14;

    /**
     * 组件值
     */
    private String formItemValue15;

    /**
     * 组件值
     */
    private String formItemValue16;

    /**
     * 组件值
     */
    private String formItemValue17;

    /**
     * 组件值
     */
    private String formItemValue18;

    /**
     * 组件值
     */
    private String formItemValue19;

    /**
     * 组件值
     */
    private String formItemValue20;

    /**
     * 组件值
     */
    private String formItemValue21;

    /**
     * 组件值
     */
    private String formItemValue22;

    /**
     * 组件值
     */
    private String formItemValue23;

    /**
     * 组件值
     */
    private String formItemValue24;

    /**
     * 组件值
     */
    private String formItemValue25;

    /**
     * 组件值
     */
    private String formItemValue26;

    /**
     * 组件值
     */
    private String formItemValue27;

    /**
     * 组件值
     */
    private String formItemValue28;

    /**
     * 组件值
     */
    private String formItemValue29;

    /**
     * 组件值
     */
    private String formItemValue30;

    /**
     * 当前处理人
     */
    private String currentDealUsers;

    /**
     * 处理完成的人
     */
    private String currentDoneUsers;

    /**
     * 所有抄送人
     */
    private String currentCcUsers;

    private String applyUser;

    /**
     * 工单状态：
草稿中
审批中
审批结束
     */
    private TicketDataStatusEnum ticketStatus;

    /**
     * 版本
     */
    private Integer version;

    private String ticketName;

    private Date ticketFinishTime;

    private String wxChatGroupId;

    private TicketMsgArriveTypeEnum ticketMsgArriveType;

    /**
     * 工单标签
     */
    private String tags;
    /**
     * 工单申请方式
     */
    private String applyTicketWays;

    private Date createTime;

    private Date updateTime;



    /**
     *
     * @param ticketData
     */
    public TicketFormItemValues toTicketFormItemValues(TicketData ticketData){
        this.ticketDataId = ticketData.getId();
        this.currentDealUsers = ticketData.getCurrentDealUsers();
        this.currentDoneUsers = ticketData.getCurrentDoneUsers();
        this.currentCcUsers = ticketData.getCurrentCcUsers();
        this.applyUser = ticketData.getApplyUser();
        this.ticketStatus = ticketData.getTicketStatus();
        this.ticketName = ticketData.getTicketName();
        this.ticketFinishTime = ticketData.getTicketFinishTime();
        this.version = ticketData.getVersion();
        this.templateId = ticketData.getTemplateId();
        this.wxChatGroupId = ticketData.getWxChatGroupId();
        this.ticketMsgArriveType = ticketData.getTicketMsgArriveType();
        this.tags = ticketData.getTags();
        this.applyTicketWays = ticketData.getApplyTicketWays();
        this.setCreateTime(ticketData.getCreateTime());
        this.setCreateBy(ticketData.getCreateBy()) ;
        this.setUpdateTime(ticketData.getUpdateTime()) ;
        this.setUpdateBy(ticketData.getUpdateBy());
        this.setDeleteTime(ticketData.getDeleteTime());
        return this;
    }



}
