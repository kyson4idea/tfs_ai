package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFormTemplate;
import com.smy.tfs.api.dto.TicketFormItemTemplateDto;
import com.smy.tfs.api.dto.TicketFormTemplateDto;
import com.smy.tfs.api.service.ITicketFormTemplateService;
import com.smy.tfs.biz.mapper.TicketFormTemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工单表单模版表 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-04-22
 */
@Service
public class TicketFormTemplateServiceImpl extends ServiceImpl<TicketFormTemplateMapper, TicketFormTemplate> implements ITicketFormTemplateService {

    @Override
    public String save(TicketFormTemplateDto ticketFormTemplateDto) {
        String ticketFormTemplateId = ticketFormTemplateDto.getId();
        List<TicketFormItemTemplateDto> ticketFormItemTemplateDtoList= ticketFormTemplateDto.getTicketFormItemTemplateDtoList();

        return ticketFormTemplateId;
    }
}
