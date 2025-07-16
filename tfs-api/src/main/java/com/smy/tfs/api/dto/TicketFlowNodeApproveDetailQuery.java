package com.smy.tfs.api.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * @CreateDate 2024/4/22 9:51
 * @UpdateDate 2024/4/22 9:51
 */
@Data
@ToString
public class TicketFlowNodeApproveDetailQuery implements Serializable {


    private static final long serialVersionUID = 5067647091219124239L;
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

    private List<String> dealUserIdList;

    /**
     * 处理意见
     */
    private String dealOpinion;

    /**
     * 处理类型/审批结果（审批通过，审批驳回，审批驳回至上一节点）
     */
    private String dealType;

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
     * 同源用户uuid
     */
    private String sameOriginId;


}
