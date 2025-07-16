package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.dto.TicketFlowNodeApproveDetailQuery;
import com.smy.tfs.api.enums.TFSTableIdCode;
import com.smy.tfs.biz.mapper.TicketFlowNodeApproveDetailMapper;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 工单流程节点审批明细 服务实现类
 * </p>
 *
 * @author 01140
 * @since 2024-04-20
 */
@Service
public class TicketFlowNodeApproveDetailServiceImpl extends ServiceImpl<TicketFlowNodeApproveDetailMapper, TicketFlowNodeApproveDetail> implements TicketFlowNodeApproveDetailService {
    @Override
    public boolean add(TicketFlowNodeApproveDetail flowNodeApproveDetail, String dealUserId) {
        if (flowNodeApproveDetail == null){
            return false;
        }
        Date now = new Date();
        flowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        flowNodeApproveDetail.setCreateBy(dealUserId);
        flowNodeApproveDetail.setUpdateBy(dealUserId);
        flowNodeApproveDetail.setCreateTime(now);
        flowNodeApproveDetail.setUpdateTime(now);
        return save(flowNodeApproveDetail);
    }


    @Override
    public List<TicketFlowNodeApproveDetail> getList(TicketFlowNodeApproveDetail detail) {
        if (detail == null || StringUtils.isBlank(detail.getTicketDataId())){
            return new ArrayList<>();
        }

        return lambdaQuery()
                .eq(TicketFlowNodeApproveDetail::getTicketDataId, detail.getTicketDataId())
                .eq(detail.getTicketFlowNodeDataId() != null, TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, detail.getTicketFlowNodeDataId())
                .eq(detail.getDealUserId() != null, TicketFlowNodeApproveDetail::getDealUserId, detail.getDealUserId())
                .eq(detail.getDealType() != null, TicketFlowNodeApproveDetail::getDealType, detail.getDealType())
                .list();
    }

    @Override
    public Integer countByQuery(TicketFlowNodeApproveDetailQuery detailQuery) {
        if (detailQuery == null || StringUtils.isBlank(detailQuery.getTicketDataId())){
            return 0;
        }

        return lambdaQuery()
                .eq(TicketFlowNodeApproveDetail::getTicketDataId, detailQuery.getTicketDataId())
                .eq(detailQuery.getTicketFlowNodeDataId() != null, TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, detailQuery.getTicketFlowNodeDataId())
                .eq(detailQuery.getDealUserId() != null, TicketFlowNodeApproveDetail::getDealUserId, detailQuery.getDealUserId())
                .in(CollectionUtils.isNotEmpty(detailQuery.getDealUserIdList()), TicketFlowNodeApproveDetail::getDealUserId, detailQuery.getDealUserIdList())
                .eq(detailQuery.getDealType() != null, TicketFlowNodeApproveDetail::getDealType, detailQuery.getDealType())
                .count();
    }
    @Override
    public List<TicketFlowNodeApproveDetail> getListByQuery(TicketFlowNodeApproveDetailQuery detailQuery) {
        if (detailQuery == null || StringUtils.isBlank(detailQuery.getTicketDataId())){
            return new ArrayList<>();
        }

        return lambdaQuery()
                .eq(TicketFlowNodeApproveDetail::getTicketDataId, detailQuery.getTicketDataId())
                .eq(detailQuery.getTicketFlowNodeDataId() != null, TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, detailQuery.getTicketFlowNodeDataId())
                .eq(detailQuery.getDealUserId() != null, TicketFlowNodeApproveDetail::getDealUserId, detailQuery.getDealUserId())
                .in(CollectionUtils.isNotEmpty(detailQuery.getDealUserIdList()), TicketFlowNodeApproveDetail::getDealUserId, detailQuery.getDealUserIdList())
                .eq(detailQuery.getDealType() != null, TicketFlowNodeApproveDetail::getDealType, detailQuery.getDealType())
                .list();
    }
}
