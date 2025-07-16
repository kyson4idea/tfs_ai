package com.smy.tfs.biz.service;


import com.smy.tfs.api.dto.TicketDataDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.ApproveDealTypeEnum;

import java.util.List;

/**
 * 企微通知业务接口
 */
public interface INotificationBizService {

    public Response<String> SendDealCard(String cardTitle, TicketDataDto ticketDataDto, AccountInfo dealUser, ApproveDealTypeEnum dealType, List<AccountInfo> sendUsers, Boolean saveData, String dealOpinion);

    public Response<String> SendNotifyCard(String cardTitle, TicketDataDto ticketDataDto, String currentNodeDataID, AccountInfo dealUser, ApproveDealTypeEnum dealType, List<AccountInfo> sendUsers, Boolean saveData);
}

