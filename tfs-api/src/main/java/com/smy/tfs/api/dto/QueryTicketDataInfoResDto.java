package com.smy.tfs.api.dto;

import com.smy.framework.base.BaseElasticsearchEntity;
import com.smy.tfs.api.enums.TicketDataStatusEnum;
import com.smy.tfs.api.enums.TicketMsgArriveTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author yss
 * @since 2025-01-06
 */
@Getter
@Setter
public class QueryTicketDataInfoResDto extends BaseElasticsearchEntity implements Serializable {


    private static final long serialVersionUID = -5809720560420079543L;
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


}
