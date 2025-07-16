package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dbo.TicketFlowNodeExecutorData;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.api.service.ITicketFlowNodeExecutorDataService;
import com.smy.tfs.biz.mapper.TicketFlowNodeExecutorDataMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单流程节点执行人数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFlowNodeExecutorDataServiceImpl extends ServiceImpl<TicketFlowNodeExecutorDataMapper, TicketFlowNodeExecutorData> implements ITicketFlowNodeExecutorDataService {


    @Override
    public List<TicketFlowNodeExecutorData> selectAllNeedDealNodeData(String ticketDataId) {
        return lambdaQuery().eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDataId)
                .likeRight(TicketFlowNodeExecutorData::getExecutorType, "APPLY_")
                .list();
    }

    @Override
    public List<AccountInfo> selectAllNeedDealUser(String ticketDataId) {
        List<TicketFlowNodeExecutorData> flowNodeDataList = selectAllNeedDealNodeData(ticketDataId);
        List<String> executorStrList = flowNodeDataList.stream().map(TicketFlowNodeExecutorData::getExecutorList).collect(Collectors.toList());
        return AccountInfo.ToAccountInfoList(executorStrList);
    }

    @Override
    public void changeFlowNodeExecutorData(String ticketDateId, String currentNodeId, String newAccountInfoStr) {
        //先删除当前审批人，再插入新审批人
        List<TicketFlowNodeExecutorData> flowNodeExecutorDataList = lambdaQuery().eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDateId)
                .eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, currentNodeId)
                .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                .likeRight(TicketFlowNodeExecutorData::getExecutorType, "APPLY_")
                .list();
        if (CollUtil.isEmpty(flowNodeExecutorDataList)){
            return;
        }

        for (TicketFlowNodeExecutorData ticketFlowNodeExecutorData : flowNodeExecutorDataList) {
            ticketFlowNodeExecutorData.setDeleteTime(new Date());
        }

        TicketFlowNodeExecutorData newNode = new TicketFlowNodeExecutorData();
        newNode.setTemplateId(flowNodeExecutorDataList.get(0).getTemplateId());
        newNode.setTicketDataId(ticketDateId);
        newNode.setTicketFlowNodeDataId(currentNodeId);
        newNode.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST);
        newNode.setExecutorValue(newAccountInfoStr);
        newNode.setExecutorList(newAccountInfoStr);
        newNode.setExecutorDoneList("");
        flowNodeExecutorDataList.add(newNode);

        this.saveOrUpdateBatch(flowNodeExecutorDataList);
    }
}
