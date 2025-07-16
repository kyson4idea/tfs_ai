package com.smy.tfs.api.service;

import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dto.BatchFinishTicketsDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.BatchDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.ticket_act_service.DelTicketsParams;
import com.smy.tfs.api.dto.ticket_sla_service.NewTag;

import java.util.List;

public interface ITicketDataActService {

    public Response<String> delTickets(DelTicketsParams params, String userType, String userId, String userName);

    public Response batchFinishTickets(BatchFinishTicketsDto batchFinishTicketsDto, String userType, String userId, String userName);

    /**
     * 批量生成工单申请id
     *
     * @param appid
     * @param n     需要生成工单申请id的个数
     * @return
     */
    public Response<List<String>> getTicketApplyIdList(String appid, Long n);

    /**
     * 批量创建工单
     *
     * @param ticketDataStdDtoList
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public Response<List<BatchDto>> batchCreateTicket(List<TicketDataStdDto> ticketDataStdDtoList, String userType, String userId, String userName);

    /**
     * 新增工单tags
     *
     * @param ticketData
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public Response addTags(TicketData ticketData, String userType, String userId, String userName);

    /**
     * 清除工单tags
     *
     * @param ticketData
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public Response delTags(TicketData ticketData, String userType, String userId, String userName);


    /**
     * 新增业务工单tags
     *
     * @param sign
     * @param tranDataStr
     * @param ticketDataId
     * @return
     */
    public Response addBusiTags(String sign, String tranDataStr, String ticketDataId);

    /**
     * 添加 tags
     *
     * @param ticketDataId
     * @param tags
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response addTags(String ticketDataId, List<String> tags, String userType, String userId, String userName);

    /**
     * 删除 tags
     *
     * @param ticketDataId
     * @param tags
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response delTags(String ticketDataId, List<String> tags, String userType, String userId, String userName);

    /**
     * 添加新标签
     *
     * @param ticketDataId
     * @param newTags
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response addNewTags(String ticketDataId, List<NewTag> newTags, String userType, String userId, String userName);

    /**
     * 删除新标签
     *
     * @param ticketDataId
     * @param delValues
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    Response delNewTags(String ticketDataId, List<NewTag> delValues, String userType, String userId, String userName);
}
