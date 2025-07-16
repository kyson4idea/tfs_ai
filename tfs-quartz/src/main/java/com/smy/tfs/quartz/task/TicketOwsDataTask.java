package com.smy.tfs.quartz.task;

import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.enums.NodeStatusEnum;
import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.api.service.ITicketAnalysisDataService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.api.service.ITicketFlowNodeDataService;
import com.smy.tfs.common.utils.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component("TicketOwsDataTask")
public class TicketOwsDataTask {

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private ITicketFlowNodeDataService ticketFlowNodeDataService;

    /**
     * 收集当前时间下上周的工单数据，并保存
     */
    public void finishTicketByType(String... flowNodeTemplateIds) {
        List<String> flowNodeTemplateIdList = Arrays.asList(flowNodeTemplateIds);
        List<TicketFlowNodeData> ticketFlowNodeDataList = ticketFlowNodeDataService.lambdaQuery()
                .isNull(TicketFlowNodeData::getDeleteTime)
                .in(TicketFlowNodeData::getTemplateId, flowNodeTemplateIdList)
                .eq(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVING).list();
    }
}
