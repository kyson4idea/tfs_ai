package com.smy.tfs.api.service;

import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.TicketFlowNodeDataDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;

import java.util.List;

/**
 * @author g90238
 * @description 贷后处理
 */
public interface IPostloanService {

    /**
     * 黑猫投诉工单 贷后处理 打标签
     *
     * @param sign
     * @param ticketEventTag
     * @param ticketDataId
     * @return
     */
    Response<String> addTagsForPostLoanTicketsCallback(String sign, String ticketEventTag, String ticketDataId);

    /**
     * 客服工单 添加标签
     *
     * @param sign
     * @param ticketEventTag
     * @param ticketDataId
     * @return
     */
    Response<String> addTagsForCustomerServiceTicketsCallback(String sign, String ticketEventTag, String ticketDataId);


    /**
     * 客服驳回到贷后时，自动派给最近处理人
     *
     * @param sign
     * @param ticketEventTag
     * @param ticketDataId
     * @return
     */
    Response<List<AccountInfo>> autoAssignToRecentHandlerCallback(String sign, String ticketEventTag, String ticketDataId);


    Response<String> addTagsForPostLoanTicketsCallback(TicketDataDto ticketData);

    Response<String> addTagsForCustomerServiceTicketsCallback(TicketDataDto ticketData);

    Response<List<AccountInfo>> autoAssignToRecentHandlerCallback(TicketDataDto ticketData);

}
