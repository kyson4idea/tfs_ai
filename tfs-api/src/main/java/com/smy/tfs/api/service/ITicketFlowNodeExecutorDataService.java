package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketFlowNodeExecutorData;
import com.smy.tfs.api.dto.base.AccountInfo;

import java.util.List;

/**
 * <p>
 * 工单流程节点执行人数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFlowNodeExecutorDataService extends IService<TicketFlowNodeExecutorData> {
    /**
     * 获取当前工单所有需要待审批的流程节点
     *
     * @param ticketDataId
     * @return
     */
    List<TicketFlowNodeExecutorData> selectAllNeedDealNodeData(String ticketDataId);

    /**
     * 获取当前工单所有需要审批的人
     *
     * @param ticketDataId
     * @return
     */
    List<AccountInfo> selectAllNeedDealUser(String ticketDataId);

    /**
     * 更新流程节点执行人数据
     *
     * @param ticketDateId
     * @param currentNodeId
     * @param newAccountInfoStr
     */
    void changeFlowNodeExecutorData(String ticketDateId, String currentNodeId, String newAccountInfoStr);
}
