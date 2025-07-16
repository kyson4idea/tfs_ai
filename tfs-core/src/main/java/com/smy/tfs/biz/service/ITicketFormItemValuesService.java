package com.smy.tfs.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.api.dto.AdvancedQueryDto;
import com.smy.tfs.api.dto.PageQueryTicketDataReqDto;
import com.smy.tfs.api.dto.TicketDataListResponseDto;
import com.smy.tfs.api.dto.TicketFormItemAttriDto;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * （表单项col和表单项value对应的平铺表） 服务类
 * </p>
 *
 * @author yss
 * @since 2024-05-10
 */
public interface ITicketFormItemValuesService extends IService<TicketFormItemValues> {

    List<TicketDataListResponseDto> selectTicketFormItemValuesList(AdvancedQueryDto advancedQueryDto);

    void syncTimeRangeTicketData(Timestamp startTimestamp,Timestamp endTimestamp);

    List<TicketFormItemValues> queryTicketDataListResponseDtoList(AdvancedQueryDto advancedQueryDto, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList);

    PageInfo<TicketFormItemValues> pageQueryTicketFormItemValuesList(PageQueryTicketDataReqDto pageQueryTicketDataReqDto, String user, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList);

    void syncTicketData(String ticketDataId);


}
