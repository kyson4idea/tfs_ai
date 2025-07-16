package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketConfig;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketDataDynamicDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;

import java.util.List;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketConfigService extends IService<TicketConfig> {

    public Response<TicketConfig> selectTicketConfig(String ticketDataId);

    public Response<String> createTicketConfig(TicketConfig ticketConfig,String userType,String userId,String userName);

    public Response<String> updateTicketConfig(TicketConfig ticketConfig,String userType,String userId,String userName);
}
