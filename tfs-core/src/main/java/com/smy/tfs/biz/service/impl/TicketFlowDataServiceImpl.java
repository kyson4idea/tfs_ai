package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFlowData;
import com.smy.tfs.api.service.ITicketFlowDataService;
import com.smy.tfs.biz.mapper.TicketFlowDataMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单流程数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFlowDataServiceImpl extends ServiceImpl<TicketFlowDataMapper, TicketFlowData> implements ITicketFlowDataService {

}
