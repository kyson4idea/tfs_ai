package com.smy.tfs.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketFormItemIdColMapping;
import com.smy.tfs.biz.mapper.TicketFormItemIdColMappingMapper;
import com.smy.tfs.biz.service.ITicketFormItemIdColMappingService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * （表单项id和对应的列名映射关系表） 服务实现类
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
@Service
public class TicketFormItemIdColMappingServiceImpl extends ServiceImpl<TicketFormItemIdColMappingMapper, TicketFormItemIdColMapping> implements ITicketFormItemIdColMappingService {

}
