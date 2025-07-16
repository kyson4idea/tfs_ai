package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.DefaultExecutorTypeEnum;
import com.smy.tfs.api.enums.DeptLevelEnum;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单流程节点执行人模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_executor_template")
public class TicketFlowNodeExecutorTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -6899318924821671477L;
    private String id;

    /**
     * 工单流程节点模版ID
     */
    private String ticketFlowNodeTemplateId;

    /**
     * 审批人-上级 sLeader
     * 审批人-用户组 sGroup
     * 审批人-指定成员数组 sMemberList
     * 审批人-提交本人 sSelf
     * 抄送人-上级 cLeader
     * 抄送人-用户组 cGroup
     * 抄送人-指定成员数组 cMemberList
     * 抄送人-提交本人 cSelf
     */
    private ExecutorTypeEnum executorType;

    //案例:
    // executorType是:sMemberList  executorValue是:域账号-ID-NAME&域账号-ID-NAME,
    // executorType是:sGroup       executorValue是:101&102,
    // executorType是:sLeader      executorValue是:域账号-ID-Name
    // executorType是:sSelf      executorValue是:域账号-ID-Name
    // executorType是:APPLY_EXTERNAL_APPROVER executorValue是:{external_config:"com.smy.razor_core.tools.service.IOpenService?methods=getData&version=1.0.0&group=default",external_tag:""}
    private String executorValue;

    //工单模版ID
    private String ticketTemplateId;

    /**当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]*/
    private String groupValue;

    private DefaultExecutorTypeEnum defaultExecutorType;

    private String defaultExecutorValue;

//    /**
//     *  部门层级
//     */
//    private DeptLevelEnum deptLevel;




}
