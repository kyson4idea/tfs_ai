package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFlowTemplate;
import com.smy.tfs.api.dbo.TicketFormTemplate;
import com.smy.tfs.api.dto.TicketFlowTemplateDto;
import com.smy.tfs.api.service.ITicketFlowTemplateService;
import com.smy.tfs.biz.mapper.TicketFlowTemplateMapper;
import com.smy.tfs.common.utils.bean.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单流程模版表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFlowTemplateServiceImpl extends ServiceImpl<TicketFlowTemplateMapper, TicketFlowTemplate> implements ITicketFlowTemplateService {

    @Override
    public String save(TicketFlowTemplateDto ticketFlowTemplateDto) {
        TicketFormTemplate ticketFlowTemplate = new TicketFormTemplate();
        BeanUtils.copyProperties(ticketFlowTemplateDto, ticketFlowTemplate);

        return ticketFlowTemplate.getId();
    }
}
