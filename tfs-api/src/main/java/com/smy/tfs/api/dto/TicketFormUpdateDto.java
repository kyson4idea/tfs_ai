package com.smy.tfs.api.dto;


import com.smy.tfs.api.dto.dynamic.TicketFlowNodeStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单表单更新DTO
 */
@Data
public class TicketFormUpdateDto implements Serializable {
    private static final long serialVersionUID = -3264946615925645091L;
    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 处理类型描述
     */
    private String dealDescription;

    /**
     * 处理描述 格式为 {commentFileInfo："",commentTagInfo:[""],commentStrInfo:""}
     */
    private String dealOpinion;

    /**
     * 变更之后的表单项
     */
    private List<TicketFormItemStdDto> formItems;


    private List<TicketFlowNodeStdDto> flowNodes;

    /**
     * 审批模式，
     * 为空:默认模式，根据工单审批配置进行审批
     * DYNAMIC:动态模式，不校验修改成是审批人
     */
    @Deprecated
    private String updateMode;

    /**
     * 审批模式，
     * 为空:默认模式，根据工单审批配置进行审批
     * DYNAMIC:动态模式，不校验修改成是审批人
     */
    private String mode;
}
