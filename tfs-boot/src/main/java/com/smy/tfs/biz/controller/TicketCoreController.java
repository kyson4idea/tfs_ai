package com.smy.tfs.biz.controller;

import com.alibaba.fastjson2.JSONObject;
import com.smy.ncs.service.export.cust.request.ExportCustomerInfoRequest;
import com.smy.ncs.service.export.cust.response.CustomerOverdueInfoResponse;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.ConvertDataParamDto;
import com.smy.tfs.api.dto.dynamic.TicketDataDynamicDto;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.model.LoginUser;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 工单数据
 * </p>
 *
 * @author owen
 * @since 2024-05-07
 */
@RestController
@ResponseBody
public class TicketCoreController {

    @Resource
    private ITicketDataService ticketDataService;

    @GetMapping({"/ticketCore/getApplyId", "/outside/ticketCore/getApplyId"})
    public AjaxResult getApplyId(String appId) {
        var response = ticketDataService.getTicketApplyId(appId);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketCore/createTicket", "/outside/ticketCore/createTicket"})
    public AjaxResult createTicket(@RequestBody TicketDataStdDto ticketDataStdDto) {
        var loginUser = SecurityUtils.getLoginUser();
        var response = ticketDataService.createTicket(ticketDataStdDto, loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @GetMapping({"/ticketCore/getConvertData", "/outside/ticketCore/getConvertData"})
    public AjaxResult getConvertData(ConvertDataParamDto convertDto) {
        var loginUser = SecurityUtils.getLoginUser();

        var response = ticketDataService.getConvertData(convertDto, loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketCore/createTicketDynamic", "/outside/ticketCore/createTicketDynamic"})
    public AjaxResult createTicketDynamic(@RequestBody TicketDataDynamicDto dynamicDto) {
        var loginUser = SecurityUtils.getLoginUser();
        var response = ticketDataService.createTicketDynamic(dynamicDto, loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketCore/countFlowNode", "/outside/ticketCore/countFlowNode"})
    public AjaxResult countFlowNode(@RequestBody TicketDataStdDto ticketDataStdDto) {
        var response = ticketDataService.countFlowNode(ticketDataStdDto);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketCore/gotoFlowNode", "/outside/ticketCore/gotoFlowNode"})
    public AjaxResult gotoFlowNode(String ticketDataId, String currentNodeId, String gotoNodeId, String gotoNodeReason) {
        var loginUser = SecurityUtils.getLoginUser();
        AccountInfo accountInfo = new AccountInfo(loginUser.getSameOriginId(), loginUser.getUserType(), loginUser.getUsername(), loginUser.getNickName());
        var response = ticketDataService.gotoFlowNode(ticketDataId, currentNodeId, gotoNodeId, gotoNodeReason, accountInfo);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }
}
