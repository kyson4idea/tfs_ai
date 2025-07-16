package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.enums.NodeStatusEnum;
import com.smy.tfs.api.service.ITicketFlowNodeDataService;
import com.smy.tfs.biz.mapper.TicketFlowNodeDataMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 工单流程节点数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFlowNodeDataServiceImpl extends ServiceImpl<TicketFlowNodeDataMapper, TicketFlowNodeData> implements ITicketFlowNodeDataService {

    @Override
    public boolean updateByIdAndStatus(TicketFlowNodeData update, String dealUserId) {
        Date date = new Date();
        update.setUpdateBy(dealUserId);
        update.setUpdateTime(date);
        return lambdaUpdate()
                .eq(TicketFlowNodeData::getId, update.getId())
                //防止终态更新为中间态，审批中和初始化状态才能更新成功
                .in(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVING.getCode(),NodeStatusEnum.APPROVE_INIT.getCode())
                .update(update);
    }
}
