package com.smy.tfs.api.dto.dynamic;

import com.smy.tfs.api.enums.ExecutorTypeEnum;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 工单流程节点执行人模版对象 ticket_flow_node_excutor_template
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data @Getter @Setter
public class TicketFlowNodeExcutorDynamicDto  implements Serializable {
    private static final long serialVersionUID = -6438968520327135905L;
    /* 执行者类型 */
    private ExecutorTypeEnum executorType;

    /** 执行者值 */
    private String executorValue;
}



