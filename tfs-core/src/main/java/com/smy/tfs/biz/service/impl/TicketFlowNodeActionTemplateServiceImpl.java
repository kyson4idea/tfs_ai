package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFlowNodeActionData;
import com.smy.tfs.api.dbo.TicketFlowNodeActionTemplate;
import com.smy.tfs.api.service.ITicketFlowNodeActionDataService;
import com.smy.tfs.api.service.ITicketFlowNodeActionTemplateService;
import com.smy.tfs.biz.mapper.TicketFlowNodeActionDataMapper;
import com.smy.tfs.biz.mapper.TicketFlowNodeActionTemplateMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单流程节点执行人数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFlowNodeActionTemplateServiceImpl extends ServiceImpl<TicketFlowNodeActionTemplateMapper, TicketFlowNodeActionTemplate> implements ITicketFlowNodeActionTemplateService {

}
