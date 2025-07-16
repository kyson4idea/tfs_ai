package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.framework.core.util.DateUtil;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class BusiTicketDataDto implements Serializable {
    private static final long serialVersionUID = -7951878568161385003L;
    /**
     * 工单id
     */
    private String ticketDataId;

    /**
     * 应用id
     */
    private String appId;


    /**
     * 工单名称
     */
    private String ticketName;

    /**
     * 模版id
     */
    private String templateId;

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
     * 通知触达方式
     */
    private TicketMsgArriveTypeEnum ticketMsgArriveType;

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

    public BusiTicketDataDto(TicketData ticketData) {
        this.ticketDataId = ticketData.getId();
        this.appId = ticketData.getAppId();
        this.templateId = ticketData.getTemplateId();
        this.ticketName = ticketData.getTicketName();
        this.ticketStatus = ticketData.getTicketStatus();
        this.applyUser = ticketData.getApplyUser();
        this.createTime = ticketData.getCreateTime();
        this.ticketFinishTime = ticketData.getTicketFinishTime();
        this.ticketMsgArriveType = ticketData.getTicketMsgArriveType();
        this.tags = ticketData.getTags();
        this.currentDealUsers = ticketData.getCurrentDealUsers();
        this.extend1 = ticketData.getExtend1();
        this.extend2 = ticketData.getExtend2();
        this.extend3 = ticketData.getExtend3();
        this.extend4 = ticketData.getExtend4();
        this.extend5 = ticketData.getExtend5();
        this.extend6 = ticketData.getExtend6();
        this.extend7 = ticketData.getExtend7();
        this.extend8 = ticketData.getExtend8();
        this.extend9 = ticketData.getExtend9();
        this.extend10 = ticketData.getExtend10();


    }

    public BusiTicketDataDto(Map<String,Object> map){
        String createTimeStr = "";
        if (Objects.nonNull(map.get("create_time"))) {
            createTimeStr = (String)map.get("create_time");
            this.createTime = DateUtil.parseDate(createTimeStr, "yyyy-MM-dd HH:mm:ss");
        }
        String ticketFinishTimeStr = "";
        if (Objects.nonNull(map.get("ticket_finish_time"))) {
            ticketFinishTimeStr = (String)map.get("ticket_finish_time");
            this.ticketFinishTime = DateUtil.parseDate(ticketFinishTimeStr, "yyyy-MM-dd HH:mm:ss");
        }
        if (Objects.nonNull(map.get("id"))) {
            this.ticketDataId = (String)map.get("id");
        }
        if (Objects.nonNull(map.get("app_id"))) {
            this.appId = (String)map.get("app_id");
        }
        if (Objects.nonNull(map.get("ticket_name"))) {
            this.ticketName = (String)map.get("ticket_name");
        }
        if (Objects.nonNull(map.get("template_id"))) {
            this.templateId = (String)map.get("template_id");
        }
        if (Objects.nonNull(map.get("ticket_status"))) {
            this.ticketStatus = TicketDataStatusEnum.getEnumByCode((String)map.get("ticket_status"));
        }
        if (Objects.nonNull(map.get("apply_user"))) {
            this.applyUser = (String)map.get("apply_user");
        }

        if (Objects.nonNull(map.get("current_deal_users"))) {
            this.currentDealUsers = (String)map.get("current_deal_users");
        }
        if (Objects.nonNull(map.get("ticket_msg_arrive_type"))) {
            this.ticketMsgArriveType = TicketMsgArriveTypeEnum.valueOf((String)map.get("ticket_msg_arrive_type"));
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
