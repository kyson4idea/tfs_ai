package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketFormTemplate;
import com.smy.tfs.api.dto.TicketFormTemplateDto;

/**
 * <p>
 * 工单表单模版表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFormTemplateService extends IService<TicketFormTemplate> {
      String save(TicketFormTemplateDto ticketFormTemplateDto);
}
