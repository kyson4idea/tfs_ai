package com.smy.tfs.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.base.Response;


/**
 * <p>
 * 工单数据表 服务类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
public interface ITicketSlaNoticeService {
    //*****************工单 begin*****************//
    //获取工单申请编号
    public Response<String> notcie(String appid);



}
