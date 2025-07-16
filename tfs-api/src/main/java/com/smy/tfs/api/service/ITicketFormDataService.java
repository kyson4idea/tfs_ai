package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketFormData;
import com.smy.tfs.api.dto.TicketFormDataDto;

/**
 * <p>
 * 工单表单数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketFormDataService extends IService<TicketFormData> {

    String save(TicketFormDataDto ticketFormDataDto);
}
