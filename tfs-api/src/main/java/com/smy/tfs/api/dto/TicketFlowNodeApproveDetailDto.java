package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * @CreateDate 2024/4/22 9:51
 * @UpdateDate 2024/4/22 9:51
 */
@Data
@ToString
public class TicketFlowNodeApproveDetailDto implements Serializable {
    private static final long serialVersionUID = 5818541553679031032L;

    public TicketFlowNodeApproveDetailDto(){

    }
    public TicketFlowNodeApproveDetailDto(TicketFlowNodeApproveDetail detail){
        this.id = detail.getId();
        this.ticketDataId = detail.getTicketDataId();
        this.ticketFlowNodeDataId = detail.getTicketFlowNodeDataId();
        this.dealUserId = detail.getDealUserId();
        this.dealUserType =detail.getDealUserType();
        this.dealUserName = detail.getDealUserName();
        this.dealDescription = detail.getDealTypeDescription();
        this.dealOpinion = detail.getDealOpinion();
        this.dealType = detail.getDealType();
        this.ticketFlowEventDataId = detail.getTicketFlowEventDataId();
        if (Objects.nonNull(detail.getDealTypeCallback())) {
            this.dealTypeCallback = detail.getDealTypeCallback().getCode();
        }
        this.createTime = detail.getCreateTime();
        this.createBy = detail.getCreateBy();
        this.updateTime = detail.getUpdateTime();
        this.updateBy = detail.getUpdateBy();
        this.deleteTime = detail.getDeleteTime();
    }

    private String id;

    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 流程节点ID
     */
    private String ticketFlowNodeDataId;

    /**
     * 处理人ID
     */
    private String dealUserId;

    private String dealUserType;

    private String dealUserName;

    /**
     * 处理意见
     */
    private String dealOpinion;

    /**
     * 处理类型/审批结果（审批通过，审批驳回，审批驳回至上一节点）
     */
    private ApproveDealTypeEnum dealType;

    /**
     * 处理类型描述
     */
    private String dealDescription;

    /**
     * 回调执行动作的处理类型
     */
    private String dealTypeCallback;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 执行事件id
     */
    private String ticketFlowEventDataId;


}
