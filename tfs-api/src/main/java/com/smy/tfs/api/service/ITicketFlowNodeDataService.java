package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 工单流程节点数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFlowNodeDataService extends IService<TicketFlowNodeData> {



    boolean updateByIdAndStatus(TicketFlowNodeData update, String dealUserId);
}
