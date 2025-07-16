package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketFlowEventData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;

/**
 * <p>
 * 工单流程动作数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFlowEventDataService extends IService<TicketFlowEventData> {

    /**
     * 根据获取动作数据列表
     * @param nodeDataId 节点数据ID
     * @param executeStep 动作类型（ before:执行前 doing:执行中  done:执行后）
     * @return
     */
    List<TicketFlowEventData> getEventList(String ticketDataId, String nodeDataId, String executeStep);

    /**
     * 查询工单更新推送事件
     * @param ticketDataId
     * @return
     */
    List<TicketFlowEventData> getTicketUpdateEventList(String ticketDataId);
    boolean updateBatchById(List<TicketFlowEventData>flowEventDataUpdateList, String dealUserId);

    Response executeEventByFlowEventDataId(String ticketFlowEventDataId);

    void callBackRetry();
}
