package com.smy.tfs.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.dto.TicketFlowNodeApproveDetailQuery;

import java.util.List;

/**
 * <p>
 * 工单流程节点审批明细 服务类
 * </p>
 *
 * @author 01140
 * @since 2024-04-20
 */
public interface TicketFlowNodeApproveDetailService extends IService<TicketFlowNodeApproveDetail> {

    /**
     * 新增节点审批记录
     * @param flowNodeApproveDetail
     * @return
     */
    boolean add(TicketFlowNodeApproveDetail flowNodeApproveDetail, String dealUserId);

    /**
     * 查询审批记录列表
     * @param detail
     * @return
     */
    List<TicketFlowNodeApproveDetail> getList(TicketFlowNodeApproveDetail detail);
    Integer countByQuery(TicketFlowNodeApproveDetailQuery detailQuery);

    List<TicketFlowNodeApproveDetail> getListByQuery(TicketFlowNodeApproveDetailQuery detailQuery);
}
