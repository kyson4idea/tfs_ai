package com.smy.tfs.api.dto.dynamic;

import com.smy.tfs.api.enums.AuditedMethodEnum;
import com.smy.tfs.api.enums.AuditedType;
import com.smy.tfs.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工单流程节点数据对象 ticket_flow_node_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data
public class TicketFlowNodeDataDynamicDto extends BaseEntity implements Serializable {


    private static final long serialVersionUID = -3938500883040100536L;
    private String name;

    private int order;

    /** 审批方式     会签     或签 */
    private AuditedMethodEnum auditedMethod;

    /** 审批类型    人工审核&自动审核&自动拒绝 */
    private AuditedType auditedType;

    /*执行人*/
    private List<TicketFlowNodeExcutorDynamicDto> excutorDtoList;
}
