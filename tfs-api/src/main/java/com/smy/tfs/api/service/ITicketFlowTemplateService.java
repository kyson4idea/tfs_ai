package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketFlowTemplate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dto.TicketFlowTemplateDto;

/**
 * <p>
 * 工单流程模版表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFlowTemplateService extends IService<TicketFlowTemplate> {
    String save(TicketFlowTemplateDto ticketFlowTemplateDto);

}
