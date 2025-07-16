package com.smy.tfs.biz.service.impl;

import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.service.impl
 * @Description: 工单审批服务（审批通过，审批驳回，撤回）
 * @CreateDate 2024/4/25 12:23
 * @UpdateDate 2024/4/25 12:23
 */
public class TicketDataApproveServiceInner {
    public static Response<String> addPreNodeParamsCheck(AddTicketFlowNodeDto nodeDto, AccountInfo accountInfo) {
        if (nodeDto == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "加签节点参数为空");
        }
        if (nodeDto.getApproveDto() == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "审批信息参数为空");
        }
        if ("BEFORE".equals(nodeDto.getAddNodeType()) && nodeDto.getAddNodeDataDto() == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "加签节点信息为空");
        }
        if (accountInfo == null) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "用户信息为空");
        }
        if (StringUtils.isAnyBlank(accountInfo.getAccountId(), accountInfo.getAccountName(), accountInfo.getAccountType(), accountInfo.getSameOriginId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "用户信息为空缺失");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "");
    }
}
