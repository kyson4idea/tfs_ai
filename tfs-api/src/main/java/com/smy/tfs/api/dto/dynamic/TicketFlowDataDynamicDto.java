package com.smy.tfs.api.dto.dynamic;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 工单流程数据对象 ticket_flow_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Getter @Setter
@Data
public class TicketFlowDataDynamicDto implements Serializable {
    private static final long serialVersionUID = 3934031779027902346L;
    /*
     * 开始时抄送 Type:ID-Name
     */
    private String startCc;

    /*
     * 结束时抄送 Type:ID-Name
     */
    private String endCc;

    private List<TicketFlowNodeDataDynamicDto> ticketFlowNodeDataDynamicDtoList;
}
