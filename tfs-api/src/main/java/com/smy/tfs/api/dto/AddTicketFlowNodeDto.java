package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.api.dto
 * @Description:
 * 加签参数
 * @CreateDate 2024/4/28 16:44
 * @UpdateDate 2024/4/28 16:44
 */
@Data
public class AddTicketFlowNodeDto implements Serializable {

    private static final long serialVersionUID = -6147989235151012290L;
    /**
     * 默认后加签，前加签为："BEFORE"
     */
    private String addNodeType;
    /**
     * 审批节点信息
     */
    private TicketFlowNodeApproveDto approveDto;
    /**
     * 添加节点的信息
     */
    private AddTicketFlowNewNodeDto addNodeDataDto;

    /**
     * 模式
     * 默认模式：无限制
     * strict模式：加签节点只有 审批+评论
     */
    private String mode;
}
