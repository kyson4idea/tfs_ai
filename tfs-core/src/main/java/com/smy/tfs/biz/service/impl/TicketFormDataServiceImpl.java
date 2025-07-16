package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFormData;
import com.smy.tfs.api.dto.TicketFormDataDto;
import com.smy.tfs.api.service.ITicketFormDataService;
import com.smy.tfs.biz.mapper.TicketFormDataMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 工单表单数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
public class TicketFormDataServiceImpl extends ServiceImpl<TicketFormDataMapper, TicketFormData> implements ITicketFormDataService {


    @Override
    public String save(TicketFormDataDto ticketFormDataDto) {
        return null;
    }
}
