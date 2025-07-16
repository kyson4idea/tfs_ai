package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * @CreateDate 2024/4/28 16:55
 * @UpdateDate 2024/4/28 16:55
 */
@Data
public class BatchTicketFlowNodeApproveDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 工单ID列表(必填)
     */
    private List<String> ticketDataIdList;

    /**
     * 处理类型(必填)
     */
    private String dealType;

    /**
     * 处理类型描述
     */
    private String dealDescription;

    /**
     * 处理意见(必填)
     */
    private String dealOpinion;


//    /**
//     * 审批节点(必填)
//     */
//    private String dealNodeId;


    /**
     * 审批模式(选填)
     * 为空:默认模式，根据工单审批配置进行审批
     * DYNAMIC:动态模式，不校验审批人，且以节点维度审批
     */
    private String dealMode;
}
