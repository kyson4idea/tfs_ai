package com.smy.tfs.biz.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketFlowEventData;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketBatchDto;
import com.smy.tfs.api.dto.out.TicketActionDto;
import com.smy.tfs.api.dto.ticket_act_service.DelTicketsParams;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.config.TfsSmyConstant;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.biz.utils.AjaxResultUtil;
import com.smy.tfs.common.annotation.Anonymous;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单数据
 * </p>
 *
 * @author yss
 * @since 2024-04-18
 */
@RestController
@ResponseBody
@Slf4j
public class TicketDataController {

    @Resource
    private ITicketDataActService ticketDataActService;

    @Resource
    private TicketDataApproveService ticketDataApproveService;

    @Resource
    private ITicketDataService ticketDataService;

    @Resource
    private TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;

    @Resource
    private ITicketFlowNodeDataService ticketFlowNodeDataService;

    @Resource
    private ITicketConfigService ticketConfigService;

    @Resource
    private ITicketAppService ticketAppService;

    @Resource
    private ITicketFlowEventDataService ticketFlowEventDataService;

    @Resource
    private TfsSmyConstant tfsSmyConstant;

    @Resource
    private ITicketAccountService accountService;

    @PostMapping("/ticketData/save")
    public AjaxResult save(TicketDataDto ticketDataDto) {

        return null;
    }

    /**
     * 工单数据列表（By不同用户维度）
     * 根据状态、工单号、工单名称
     *
     * @return
     */
    @PostMapping({"/ticketData/query", "/outside/ticketData/query"})
    public AjaxResult query() {

        Page<TicketDataDto> page = new Page<TicketDataDto>(null, 0, 0, 0);
        return AjaxResult.success(page);
    }

    /**
     * 根据银行卡查询工单状态
     *
     * @return
     */
    @GetMapping({"/ticketData/getStatusByBusinessKey", "/outside/ticketData/getStatusByBusinessKey"})
    public AjaxResult getStatusByBusinessKey(String businessKey, String ticketStatus, Integer limit) {

        var response = ticketDataService.selectTicketStatusByBusinessKey(businessKey, ticketStatus, limit);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    /**
     * 根据业务号查询工单
     *
     * @return
     */
    @GetMapping({"/ticketData/getTicketByBusinessKey", "/outside/ticketData/getTicketByBusinessKey"})
    public AjaxResult getTicketByBusinessKey(String businessKey, String templateIdOrCode, Integer limit) {

        var response = ticketDataService.selectTicketByBusinessKey(businessKey, templateIdOrCode, limit);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    /**
     * 根据id查询工单数据详情
     *
     * @return
     */
    @GetMapping({"/ticketData/getFullTicketDataById", "/outside/ticketData/getFullTicketDataById"})
    public AjaxResult getById(String id) {
        var response = ticketDataService.selectFullTicketDataById(new ReqParam(id));
        if (response.isSuccess()) {
            TicketDataDto data = response.getData();
            data.DealUserDistinct();
            data.buildTags();
            data.DealHasApprovalAuth(SecurityUtils.getSameOriginIdOrDefault(null));
        }
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @GetMapping({"/ticketData/getDataByIdWithoutAuth"})
    public AjaxResult getDataByIdWithoutAuth(String id) {
        var response = ticketDataService.selectFullTicketDataById(new ReqParam(id));
        if (response.isSuccess()) {
            TicketDataDto data = response.getData();
            data.DealUserDistinct();
            data.buildTags();
            data.DealHasApprovalAuth(SecurityUtils.getSameOriginIdOrDefault(null));
        }
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketData/act"})
    public AjaxResult act(@RequestBody TicketActionDto actionDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        var response = ticketDataApproveService.act(actionDto, userType, userId, userName);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    @PostMapping({"/ticketData/delTickets"})
    public AjaxResult delTickets(@RequestBody DelTicketsParams params) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        var response = ticketDataActService.delTickets(params, userType, userId, userName);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }


    //审批（通过/驳回）
    @PostMapping({"/ticketData/approve", "/outside/ticketData/approve"})
    public AjaxResult approve(@RequestBody TicketFlowNodeApproveDto approveDto, HttpServletRequest httpServletRequest) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        if (approveDto != null && StringUtils.isEmpty(approveDto.getMode()) && httpServletRequest != null && StringUtils.isNotEmpty(httpServletRequest.getHeader("tfs-mode"))) {
            approveDto.setMode(httpServletRequest.getHeader("tfs-mode"));
        }
        var response = ticketDataApproveService.approve(approveDto, userType, userId, userName);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    //审批（通过/驳回）
    @PostMapping({"/ticketData/batchApprove", "/outside/ticketData/batchApprove"})
    public AjaxResult batchApprove(@RequestBody BatchTicketFlowNodeApproveDto batchTicketFlowNodeApproveDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        /*调试*/
//        userId="y01781";
//        userName="殷沙沙";
//        userType="ldap";
        var response = ticketDataApproveService.batchApprove(batchTicketFlowNodeApproveDto, userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 加签
     */
    @PostMapping({"/ticketData/addTicketFlowNodeData", "/outside/ticketData/addTicketFlowNodeData"})
    public AjaxResult addTicketFlowNodeData(@RequestBody AddTicketFlowNodeDto addTicketFlowNodeDto, HttpServletRequest httpServletRequest) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        String originId = loginUser.getSameOriginId();
        if (addTicketFlowNodeDto != null && StringUtils.isEmpty(addTicketFlowNodeDto.getMode()) && httpServletRequest != null && StringUtils.isNotEmpty(httpServletRequest.getHeader("tfs-mode"))) {
            addTicketFlowNodeDto.setMode(httpServletRequest.getHeader("tfs-mode"));
        }
        AccountInfo accountInfo = new AccountInfo(originId, userType, userId, userName);
        var response = ticketDataApproveService.addTicketFlowNodeData(addTicketFlowNodeDto, accountInfo);
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }

    //催办
    @PostMapping({"/ticketData/batchReminderTicket", "/outside/ticketData/batchReminderTicket"})
    public AjaxResult batchReminderTicket(@RequestBody TicketDataDto ticketDataDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        if (CollUtil.isEmpty(ticketDataDto.getIdList())) {
            return AjaxResult.error("请选择要催办的工单");
        }
        var response = ticketDataService.urgeTicketByIdList(ApproveDealTypeEnum.URGE, ticketDataDto.getIdList(), userType, userId, userName, ApproveDealTypeEnum.URGE.getDesc());
        Integer code = ObjectHelper.isNotEmpty(response.getCode()) ? Integer.valueOf(response.getCode()) : Integer.valueOf(BizResponseEnums.UNKNOW_EXCEPTION_CODE.getCode());
        return new AjaxResult(code, response.getMsg(), response.getData());
    }


    //建群跟单
    @PostMapping({"/ticketData/batchFollowTicket", "/outside/ticketData/batchFollowTicket"})
    public AjaxResult batchFollowTicket(@RequestBody TicketDataDto ticketDataDto, String userType, String userId, String userName) {

        if (CollectionUtils.isEmpty(ticketDataDto.getIdList())) {
            return AjaxResult.error("请选择要建群跟单的工单");
        }
        String originId = "";
        if (StringUtils.isEmpty(userType) || StringUtils.isEmpty(userId)) {
            var loginUser = SecurityUtils.getLoginUser();
            userName = loginUser.getNickName();
            userId = loginUser.getUsername();
            userType = loginUser.getUserType();
            originId = loginUser.getSameOriginId();
        }
        AccountInfo accountInfo = new AccountInfo(originId, userType, userId, userName);
        List<AccountInfo> accountInfoList = new ArrayList<>();
        accountInfoList.add(accountInfo);
        Response<String> response = ticketDataService.createQWGroupByIdList(ticketDataDto.getIdList(), accountInfoList);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/ticketData/joinQWGroup", "/outside/ticketData/joinQWGroup"})
    public AjaxResult joinQWGroup(String ticketDataID, String userType, String userID) {

        if (StringUtils.isEmpty(ticketDataID)) {
            return AjaxResult.error("工单号不能为空");
        }
        if (StringUtils.isEmpty(userType) || StringUtils.isEmpty(userID)) {
            var loginUser = SecurityUtils.getLoginUser();
            userID = loginUser.getUsername();
            userType = loginUser.getUserType();
        }
        Response<String> response = ticketDataService.joinQWGroup(ticketDataID, userType, userID);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    //撤销
    @PostMapping({"/ticketData/batchRevokeTicket", "/outside/ticketData/batchRevokeTicket"})
    public AjaxResult batchRevokeTicket(@RequestBody TicketDataDto ticketDataDto) {

        if (CollUtil.isEmpty(ticketDataDto.getIdList())) {
            return AjaxResult.error("请选择要撤销的工单");
        }
        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        Response<String> response = ticketDataService.withdrawTicketByIdList(ticketDataDto.getIdList(), userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/ticketData/dispatchTicket", "/outside/ticketData/dispatchTicket"})
    public AjaxResult dispatchTicket(@Valid @RequestBody TicketDispatchDto ticketDispatchDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        Response<String> response = ticketDataService.dispatchTicket(ticketDispatchDto, userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/ticketData/receiveTicket", "/outside/ticketData/receiveTicket"})
    public AjaxResult receiveTicket(@Valid @RequestBody ReceiveTicketDto receiveTicketDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        TicketDispatchDto ticketDispatchDto = new TicketDispatchDto();
        if (null == receiveTicketDto
                || (StringUtils.isEmpty(receiveTicketDto.getId()) && StringUtils.isEmpty(receiveTicketDto.getIdList()))
        ) {
            throw new ServiceException("工单id为空");
        }
        List idList = receiveTicketDto.getIdList();
        if (CollectionUtils.isEmpty(idList)) {
            idList = new ArrayList();
        }
        if (StringUtils.isNotEmpty(receiveTicketDto.getId())) {
            idList.add(receiveTicketDto.getId());
        }
        BatchTicketDispatchDto batchTicketDispatchDto = new BatchTicketDispatchDto();
        batchTicketDispatchDto.setTicketDataIdList(idList);
        batchTicketDispatchDto.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST.getCode());
        batchTicketDispatchDto.setAccountType(userType);
        batchTicketDispatchDto.setAccountIdList(Arrays.asList(userId));
        Response<TicketBatchDto> response = ticketDataService.batchDispatchTicket(batchTicketDispatchDto, userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/ticketData/batchDispatchTicket", "/outside/ticketData/batchDispatchTicket"})
    public AjaxResult batchDispatchTicket(@Valid @RequestBody BatchTicketDispatchDto batchTicketDispatchDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        Response<TicketBatchDto> response = ticketDataService.batchDispatchTicket(batchTicketDispatchDto, userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping({"/ticketData/updateTicketFormData", "/outside/ticketData/updateTicketFormData"})
    public AjaxResult updateTicketFormData(@RequestBody TicketFormUpdateDto ticketFormUpdateDto) {

        return updateTicket(ticketFormUpdateDto);
    }

    @PostMapping({"/ticketData/updateTicket"})
    public AjaxResult updateTicket(@RequestBody TicketFormUpdateDto ticketFormUpdateDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        Response response = ticketDataService.updateTicketFormData(ticketFormUpdateDto, userType, userId, userName);
        if (response.isSuccess()) {
            return AjaxResult.success(response.getMsg(), response.getData());
        } else {
            return AjaxResult.error(response.getMsg(), response.getData());
        }
    }

    @Anonymous
    @PostMapping({"/ticketData/comment", "/outside/ticketData/comment"})
    public AjaxResult comment(@Valid @RequestBody AddTicketFlowNodeCommentDto commentDto) {

        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        String sameOriginId = loginUser.getSameOriginId();

        AccountInfo accountInfo = new AccountInfo(sameOriginId, userType, userId, userName);
        Response<String> response = ticketDataService.comment(commentDto, accountInfo);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @GetMapping({"/ticketData/getTicketConfig", "/outside/ticketData/getTicketConfig"})
    public AjaxResult getTicketConfig(String ticketDataID) {

        var response = ticketConfigService.selectTicketConfig(ticketDataID);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/ticketData/updateTicketConfig")
    public AjaxResult updateTicketConfig(TicketConfigDto ticketConfig) {

        if (ticketConfig == null) {
            return new AjaxResult(1, "ticketConfig 参数为空", null);
        }
        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        var response = ticketConfigService.updateTicketConfig(ticketConfig.ToTicketConfig(), userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/ticketData/createTicketConfig")
    public AjaxResult createTicketConfig(TicketConfigDto ticketConfig) {

        if (ticketConfig == null) {
            return new AjaxResult(1, "ticketConfig 参数为空", null);
        }
        var loginUser = SecurityUtils.getLoginUser();
        String userName = loginUser.getNickName();
        String userId = loginUser.getUsername();
        String userType = loginUser.getUserType();
        var response = ticketConfigService.createTicketConfig(ticketConfig.ToTicketConfig(), userType, userId, userName);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/ticketData/executeEvent")
    public AjaxResult executeEvent(String ticketDataId, String flowNodeDataId, String executeStep) {

        var response = ticketDataApproveService.executeEvent(ticketDataId, flowNodeDataId, executeStep, null, null, null);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    @PostMapping("/ticketData/executeEventKf")
    public AjaxResult executeEventKf(String ticketDataId, String nodeTemplateID) {

        if (StringUtils.isAnyEmpty(ticketDataId, nodeTemplateID)) {
            return AjaxResult.error("ticketDataId、nodeTemplateID不能为空");
        }
        List<TicketFlowNodeData> nodeDataList = ticketFlowNodeDataService.lambdaQuery()
                .isNull(TicketFlowNodeData::getDeleteTime)
                .eq(TicketFlowNodeData::getTemplateId, nodeTemplateID)
                .eq(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVE_PASS)
                .list();
        if (CollectionUtils.isNotEmpty(nodeDataList)) {
            for (TicketFlowNodeData nodeData : nodeDataList) {
                if (Objects.equals(ticketDataId, nodeData.getTicketDataId()) || "ALL".equals(ticketDataId)) {
                    List<TicketFlowEventData> flowEventDataList = ticketFlowEventDataService.lambdaQuery()
                            .isNull(TicketFlowEventData::getDeleteTime)
                            .eq(TicketFlowEventData::getTicketFlowNodeDataId, nodeData.getId())
                            .list();
                    if (CollectionUtils.isEmpty(flowEventDataList)) {
                        TicketFlowEventData ticketFlowEventData = new TicketFlowEventData();
                        ticketFlowEventData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_EVENT_DATA));
                        ticketFlowEventData.setTicketDataId(nodeData.getTicketDataId());
                        ticketFlowEventData.setEventStatus(EventStatusEnum.WAIT_EXECUTE);
                        ticketFlowEventData.setTicketFlowNodeDataId(nodeData.getId());
                        ticketFlowEventData.setEventType(EventTypeEnum.DUBBO_SERVICE);
                        ticketFlowEventData.setEventConfig("com.smy.ows.api.service.TfsWorksheetSyncService?methods=close");
                        ticketFlowEventData.setEventTag("{{detail_id}}{{detail_user_id}}{{detail_user_name}}{{detail_user_type}}{{detail_opinion}}{{detail_type}}{{detail_type_des}}");
                        ticketFlowEventData.setExecuteStep(ExecuteStepEnum.BEFORE_PASS);
                        ticketFlowEventData.setTemplateId("-1");
                        ticketFlowEventData.setPushConfig("{\"pushType\":\"AUTO_PUSH\",\"retryCount\":3,\"retryIntervalTime\":3}");
                        ticketFlowEventData.setApproveDealType(ApproveDealTypeEnum.PASS);
                        if (ticketFlowEventDataService.save(ticketFlowEventData)) {
                            List<TicketFlowEventData> ticketFlowEventData1 = new ArrayList<>();
                            ticketFlowEventData1.add(ticketFlowEventData);
                            HashMap<String, String> paramsMap = new HashMap<>();
                            //补齐
                            paramsMap.put("node_name", "贷后关单");
                            paramsMap.put("detail_user_id", "7089");
                            paramsMap.put("detail_user_name", "系统账号（流转贷后）");
                            paramsMap.put("detail_user_type", "kefu");
                            paramsMap.put("detail_opinion", "统一工单程序补单");
                            paramsMap.put("detail_type", ApproveDealTypeEnum.PASS.getCode());
                            paramsMap.put("detail_type_des", "统一工单程序补单");
                            Response<String> exeResp = ticketDataApproveService.executeEventList(nodeData.getTicketDataId(), "", ticketFlowEventData1, paramsMap, null, null);
                            if (!exeResp.isSuccess()) {
                                log.error("执行动作异常:{}", exeResp.getMsg());
                            }
                        } else {
                            log.error("保存失败{}", JSONObject.toJSONString(ticketFlowEventData));
                        }
                    }
                }
            }
            return AjaxResultUtil.success();
        }
        return AjaxResultUtil.responseToAjaxResult(null);
    }

    @PostMapping({"/ticketData/executeEventByFlowEventDataId", "/outside/ticketData/executeEventByFlowEventDataId"})
    public AjaxResult executeEventByFlowEventDataId(String ticketFlowEventDataId) {

        Response response = ticketFlowEventDataService.executeEventByFlowEventDataId(ticketFlowEventDataId);
        return AjaxResultUtil.responseToAjaxResult(response);
    }

    /**
     * 仅用于测试企微通知
     *
     * @param ticketFlowNodeDataId
     * @return
     */
    @PostMapping("/ticketData/doCreateQWGroupByTicketApp")
    public AjaxResult doCreateQWGroupByTicketApp(String ticketFlowNodeDataId) {

        Response<String> ticketAppServiceResp = ticketAppService.createQWGroupAndSendMsgByNode(ticketFlowNodeDataId, CallBackMsgStatusEnum.SUCCESS_MSG_SENG);
        return AjaxResultUtil.responseToAjaxResult(ticketAppServiceResp);
    }

    /**
     * @param appId
     * @return
     */
    @GetMapping({"/ticketData/getAdminAccount", "/outside/ticketData/getAdminAccount"})
    public AjaxResult getAdminAccount(String appId) {

        Optional<TicketApp> opt = ticketAppService.lambdaQuery()
                .eq(TicketApp::getId, appId)
                .isNull(TicketApp::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            String errInfo = String.format("相关业务(%s)不存在", opt.get().getAppName());
            log.info(errInfo);
            return AjaxResult.error(errInfo);
        }

        //获取应用管理员
        TicketApp ticketApp = opt.get();
        List<AccountInfo> adminAccountList = AccountInfo.ToAccountInfoList(ticketApp.getAppAdminUsers());
        String superAdmin = tfsSmyConstant.getSuperAdmin();
        if (ObjectHelper.isNotEmpty(superAdmin)) {
            //获取超级管理员
            List<AccountInfo> superAdminList = AccountInfo.ToAccountInfoList(superAdmin);
            if (CollectionUtils.isNotEmpty(superAdminList)) {
                for (AccountInfo accountInfo : superAdminList) {
                    TicketRemoteAccountDto accountDto = accountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                    if (accountDto != null && org.apache.commons.lang3.StringUtils.isNotBlank(accountDto.getQywxId())) {
                        accountInfo.setAccountName(accountDto.getUserName());
                        accountInfo.setSameOriginId(accountDto.getSameOriginId());
                    }
                }
            }
            adminAccountList.addAll(superAdminList);
        }
        //去重
        List<AccountInfo> uniqAdminAccountList = adminAccountList.stream().distinct().collect(Collectors.toList());
        return AjaxResult.success(uniqAdminAccountList);
    }

    /**
     * 是否是业务管理员
     *
     * @param appAdminDto
     * @return
     */
    @GetMapping({"/ticketData/isAppAdmin", "/outside/ticketData/isAppAdmin"})
    public AjaxResult getIsAppAdmin(AppAdminDto appAdminDto) {

        if (null == appAdminDto || StringUtils.isEmpty(appAdminDto.getAppId())) {
            return AjaxResult.error("请检查入参");
        }
//        Optional<TicketApp> opt = ticketAppService.lambdaQuery()
//                .eq(TicketApp::getId, appAdminDto.getAppId())
//                .isNull(TicketApp::getDeleteTime)
//                .oneOpt();
//        if (!opt.isPresent()) {
//            String errInfo = String.format("相关业务(%s)不存在", opt.get().getAppName());
//            log.info(errInfo);
//            return AjaxResult.error(errInfo);
//        }
//        //获取当前用户的同源id
//        String sameOriginId = SecurityUtils.getLoginUser().getSameOriginId();
//        String sameOriginIdStr = String.format("\"sameOriginId\":\"%s\"", sameOriginId);
//        //获取应用管理员
//        TicketApp ticketApp = opt.get();
//        String appAdminUsers = ticketApp.getAppAdminUsers();
//        if (StringUtils.isNotEmpty(appAdminUsers) && appAdminUsers.contains(sameOriginIdStr)) {
//            appAdminDto.setIsAppAdmin(Boolean.TRUE);
//            return AjaxResult.success(appAdminDto);
//        }
//        appAdminDto.setIsAppAdmin(Boolean.FALSE);
        appAdminDto.setIsAppAdmin(Boolean.TRUE);
        return AjaxResult.success(appAdminDto);
    }

}
