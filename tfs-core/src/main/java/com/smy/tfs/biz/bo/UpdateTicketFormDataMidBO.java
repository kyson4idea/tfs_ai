package com.smy.tfs.biz.bo;

import com.smy.tfs.api.dbo.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.bo
 * @Description:
 * 工单表单更新中间类
 * @CreateDate 2024/4/29 16:04
 * @UpdateDate 2024/4/29 16:04
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateTicketFormDataMidBO implements Serializable {
    /**
     * 更新工单信息
     */
    TicketData updateTicketData;
    /**
     *更新流程数据
     */
    TicketFlowData updateTicketFlowData;
    /**
     *新增流程节点数据
     */
    List<TicketFlowNodeData> newTicketFlowNodeDataList;
    /**
     *新增节点执行组数据
     */
    List<TicketFlowNodeExecutorData> newTicketFlowNodeExecutorDataList;
    /**
     *新增节点动作数据
     */
    List<TicketFlowNodeActionData> newTicketFlowNodeActionDataList;
    /**
     *新增事件数据
     */
    List<TicketFlowEventData> newTicketFlowEventDataList;
    /**
     *新增表单数据
     */
    TicketFormData newTicketFormData;
    /**
     *新增表单项数据
     */
    List<TicketFormItemData> newTicketFormItemDataList;
}
