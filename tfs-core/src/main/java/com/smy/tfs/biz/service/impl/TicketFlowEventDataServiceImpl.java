package com.smy.tfs.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFlowEventData;
import com.smy.tfs.api.dbo.TicketFlowNodeApproveDetail;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dto.TicketFlowNodeApproveDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.ITicketAppService;
import com.smy.tfs.api.service.ITicketDataService;
import com.smy.tfs.api.service.ITicketFlowEventDataService;
import com.smy.tfs.api.service.ITicketFlowNodeDataService;
import com.smy.tfs.biz.mapper.TicketFlowEventDataMapper;
import com.smy.tfs.biz.service.TicketDataApproveService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单流程动作数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Service
@Slf4j
public class TicketFlowEventDataServiceImpl extends ServiceImpl<TicketFlowEventDataMapper, TicketFlowEventData> implements ITicketFlowEventDataService {

    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private TicketDataApproveService ticketDataApproveService;
    @Resource
    private ITicketFlowNodeDataService ticketFlowNodeDataService;
    @Resource
    private TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    private ITicketFlowEventDataService ticketFlowEventDataService;
    @Resource
    private ITicketAppService ticketAppService;


    @Override
    public List<TicketFlowEventData> getEventList(String ticketDataId, String nodeDataId, String executeStep) {
        if (StringUtils.isBlank(nodeDataId)) {
            return new ArrayList<>();
        }
        return lambdaQuery()
                .eq(TicketFlowEventData::getTicketFlowNodeDataId, nodeDataId)
                .eq(StringUtils.isNotEmpty(ticketDataId), TicketFlowEventData::getTicketDataId, ticketDataId)
                .eq(StringUtils.isNotEmpty(executeStep), TicketFlowEventData::getExecuteStep, executeStep)
                .isNull(TicketFlowEventData::getDeleteTime)
                .list();
    }

    @Override
    public List<TicketFlowEventData> getTicketUpdateEventList(String ticketDataId) {
        if (StringUtils.isAnyBlank(ticketDataId)) {
            return new ArrayList<>();
        }
        return lambdaQuery()
                .eq(TicketFlowEventData::getTicketDataId, ticketDataId)
                .eq(TicketFlowEventData::getTicketFlowNodeDataId, "-1")
                .isNotNull(TicketFlowEventData::getDeleteTime)
                .list();
    }

    @Override
    public boolean updateBatchById(List<TicketFlowEventData> flowEventDataUpdateList, String dealUserId) {
        if (CollectionUtils.isEmpty(flowEventDataUpdateList)) {
            return true;
        }
        Date date = new Date();
        for (TicketFlowEventData flowEventData : flowEventDataUpdateList) {
            flowEventData.setUpdateBy(dealUserId);
            flowEventData.setUpdateTime(date);
        }
        return updateBatchById(flowEventDataUpdateList);
    }

    @Override
    public Response executeEventByFlowEventDataId(String ticketFlowEventDataId) {
        if (StringUtils.isEmpty(ticketFlowEventDataId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作ID(%s)为空", ticketFlowEventDataId));
        }
        //查询执行事件数据
        Optional<TicketFlowEventData> opt = this.lambdaQuery()
                .eq(TicketFlowEventData::getId, ticketFlowEventDataId)
                .isNull(TicketFlowEventData::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)对应节点数据不存在", ticketFlowEventDataId));
        }
        TicketFlowEventData ticketFlowEventData = opt.get();
        if (Objects.nonNull(ticketFlowEventData.getEventStatus()) && EventStatusEnum.EXECUTE_SUCCESS_FINAL == ticketFlowEventData.getEventStatus()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)的状态为成功，无需重试", ticketFlowEventDataId));
        }
        String tID = ticketFlowEventData.getTicketDataId();

        //查询工单数据
        var ticketDataOpt = ticketDataService.lambdaQuery()
                .isNull(TicketData::getDeleteTime)
                .eq(TicketData::getId, tID)
                .oneOpt();
        if (!ticketDataOpt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)对应的工单数据不存在", ticketFlowEventDataId));
        }
        TicketData ticketData = ticketDataOpt.get();
        if (TicketDataStatusEnum.WITHDRAW == ticketData.getTicketStatus()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)对应的工单(%s)已撤回，不能重试", ticketFlowEventDataId, ticketData.getId()));
        }

        //查询该事件不下同人的执行动作明细
        List<TicketFlowNodeApproveDetail> flowNodeApproveDetailList = ticketFlowNodeApproveDetailService.lambdaQuery()
                .eq(TicketFlowNodeApproveDetail::getTicketFlowEventDataId, ticketFlowEventDataId)
                .eq(TicketFlowNodeApproveDetail::getDealTypeCallback, DealTypeCallbackEnum.ACTION_FAILED)
                .orderByDesc(TicketFlowNodeApproveDetail::getCreateTime)
                .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                .list();
        if (CollectionUtils.isEmpty(flowNodeApproveDetailList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)明细不存在，不能重试", ticketFlowEventDataId));
        }
        //取最新的审批人进行审批回调
        TicketFlowNodeApproveDetail ticketFlowNodeApproveDetail = flowNodeApproveDetailList.get(0);

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("detail_id", ticketFlowNodeApproveDetail.getId());
        paramsMap.put("detail_user_id", ticketFlowNodeApproveDetail.getDealUserId());
        paramsMap.put("detail_user_name", ticketFlowNodeApproveDetail.getDealUserName());
        paramsMap.put("detail_user_type", ticketFlowNodeApproveDetail.getDealUserType());
        paramsMap.put("detail_opinion", ticketFlowNodeApproveDetail.getDealOpinion());
        paramsMap.put("detail_type", ticketFlowNodeApproveDetail.getDealType().getCode());
        paramsMap.put("detail_type_des", ticketFlowNodeApproveDetail.getDealTypeDescription());

        String userName = ticketFlowNodeApproveDetail.getDealUserName();
        String userId = ticketFlowNodeApproveDetail.getDealUserId();
        String userType = ticketFlowNodeApproveDetail.getDealUserType();
        AccountInfo accountInfo = new AccountInfo("", userType, userId, userName);

        //核心：执行动作
        var response = ticketDataApproveService.executeEventCore(tID, ticketData.getInterfaceKey(), ticketFlowEventData, paramsMap, accountInfo, null);
        if (!response.isSuccess()) {
            return response;
        }
        /**************************审批回调************************/
        Response approveCallBackResp = approveCallBack(ticketFlowEventData, accountInfo);
        if (!approveCallBackResp.isSuccess()) {
            return approveCallBackResp;
        }
        //该节点下面的所有事件都成功，则发送成功通知。
        String ticketFlowNodeDataId = ticketFlowEventData.getTicketFlowNodeDataId();
        List<TicketFlowEventData> ticketFlowEventDataLis = ticketFlowEventDataService.lambdaQuery()
                .eq(TicketFlowEventData::getTicketFlowNodeDataId, ticketFlowNodeDataId)
                .in(TicketFlowEventData::getEventStatus, Arrays.asList(EventStatusEnum.EXECUTE_FAILURE_MIDDLE, EventStatusEnum.EXECUTE_FAILURE))
                .isNull(TicketFlowEventData::getDeleteTime)
                .list();
        if (CollectionUtils.isEmpty(ticketFlowEventDataLis)) {
            Response<String> createQWGroupAndSendMsgByNodeResp = ticketAppService.createQWGroupAndSendMsgByNode(ticketFlowEventData.getTicketFlowNodeDataId(), CallBackMsgStatusEnum.SUCCESS_MSG_SENG);
            log.info("发送通知返回:{}", createQWGroupAndSendMsgByNodeResp);
        }
        return Response.success();
    }

    public Response approveCallBack(TicketFlowEventData ticketFlowEventData, AccountInfo accountInfo) {
        /**************************审批回调************************/
        //如果是通过后执行，拒绝后执行，完成后执行则无需走审批回调。
        ExecuteStepEnum eventStep = ticketFlowEventData.getExecuteStep();
        if (Objects.nonNull(eventStep) &&
                Arrays.asList(ExecuteStepEnum.DONE_PASS, ExecuteStepEnum.DONE_REJECT, ExecuteStepEnum.DONE_UPDATE,
                        ExecuteStepEnum.BEFORE_COMMENT, ExecuteStepEnum.FINISH, ExecuteStepEnum.DONE_ADD_NODE).contains(eventStep)) {
            return Response.success();
        }

        String ticketFlowEventDataId = ticketFlowEventData.getId();
        ApproveDealTypeEnum triggerType = ticketFlowEventData.getApproveDealType();
        if (Objects.isNull(triggerType)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)触发类型为空，请联系管理员", ticketFlowEventDataId));
        }

        //查询节点数据
        String ticketFlowNodeDataId = ticketFlowEventData.getTicketFlowNodeDataId();
        Optional<TicketFlowNodeData> ticketFlowNodeDataOpt = ticketFlowNodeDataService.lambdaQuery()
                .eq(TicketFlowNodeData::getId, ticketFlowNodeDataId)
                .oneOpt();
        if (!ticketFlowNodeDataOpt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("执行动作(%s)对应节点(%s)数据未查询到", ticketFlowEventDataId, ticketFlowNodeDataId));
        }
        TicketFlowNodeData ticketFlowNodeData = ticketFlowNodeDataOpt.get();
        String ticketDataId = ticketFlowEventData.getTicketDataId();
        String userName = accountInfo.getAccountName();
        String userId = accountInfo.getAccountId();
        String userType = accountInfo.getAccountType();
        TicketFlowNodeApproveDto approveDto = new TicketFlowNodeApproveDto();
        approveDto.setDealType(triggerType.getCode());
        approveDto.setTicketID(ticketDataId);
        //approveDto.setDealUserId(userId);
        Response response = ticketDataApproveService.approve(approveDto, userType, userId, userName);
        if (!response.isSuccess()) {
            return response;
        }

        //会签情况下，其余的审批人进行审批回调
        if (Objects.equals(ticketFlowNodeData.getAuditedMethod(), AuditedMethodEnum.AND)) {
            List<TicketFlowNodeApproveDetail> approveFailedList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, ticketFlowNodeDataId)
                    .eq(TicketFlowNodeApproveDetail::getDealTypeCallback, DealTypeCallbackEnum.ACTION_FAILED)
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .list();
            List<TicketFlowNodeApproveDetail> distinctApproveFailedList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(approveFailedList)) {
                distinctApproveFailedList = approveFailedList.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        detail -> detail.getTicketFlowEventDataId() + "-" + detail.getDealUserId(), // 定义键的生成方式
                                        Function.identity(), // 对象本身作为值
                                        (existing, replacement) -> existing), // 当键冲突时，保留现有的对象
                                map -> new ArrayList<>(map.values()) // 将Map的值转换回List
                        ));
            }

            List<TicketFlowNodeApproveDetail> approveSuccessList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, ticketFlowNodeDataId)
                    .eq(TicketFlowNodeApproveDetail::getDealTypeCallback, DealTypeCallbackEnum.ACTION_SUCCESS)
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .list();
            List<TicketFlowNodeApproveDetail> distinctApproveSuccessList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(approveSuccessList)) {
                distinctApproveSuccessList = approveSuccessList.stream()
                        .collect(Collectors.collectingAndThen(
                                Collectors.toMap(
                                        detail -> detail.getTicketFlowEventDataId() + "-" + detail.getDealUserId(), // 定义键的生成方式
                                        Function.identity(), // 对象本身作为值
                                        (existing, replacement) -> existing), // 当键冲突时，保留现有的对象
                                map -> new ArrayList<>(map.values()) // 将Map的值转换回List
                        ));
            }

            List<TicketFlowNodeApproveDetail> intersection = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(distinctApproveFailedList)
                    && CollectionUtils.isNotEmpty(distinctApproveSuccessList)) {
                List<String> successSet = distinctApproveSuccessList.stream()
                        .map(detail -> detail.getTicketFlowEventDataId() + "_" + detail.getDealUserId())
                        .collect(Collectors.toList());

                intersection = distinctApproveFailedList.stream()
                        .filter(detail -> !successSet.contains(detail.getTicketFlowEventDataId() + "_" + detail.getDealUserId()))
                        .collect(Collectors.toList());

            } else if (CollectionUtils.isNotEmpty(distinctApproveFailedList)
                    && CollectionUtils.isEmpty(distinctApproveSuccessList)) {
                intersection = distinctApproveFailedList;
            }
            if (CollectionUtils.isNotEmpty(intersection)) {
                for (TicketFlowNodeApproveDetail it : intersection) {
                    String dealUserName = it.getDealUserName();
                    String dealUserId = it.getDealUserId();
                    String dealUserType = it.getDealUserType();
                    TicketFlowNodeApproveDto ticketFlowNodeApproveDto = new TicketFlowNodeApproveDto();
                    ticketFlowNodeApproveDto.setDealType(triggerType.getCode());
                    ticketFlowNodeApproveDto.setTicketID(ticketDataId);
                    //ticketFlowNodeApproveDto.setDealUserId(userId);
                    Response approveResp = ticketDataApproveService.approve(ticketFlowNodeApproveDto, dealUserType, dealUserId, dealUserName);
                    if (!approveResp.isSuccess()) {
                        return approveResp;
                    }
                }
            }
        }
        return Response.success();

    }

    @Override
    public void callBackRetry() {
        //可以设置5分钟一次，一小时内
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        LocalDateTime agingHoursAgo = currentLocalDateTime.minusHours(1);
        Timestamp startTimestampTemp = Timestamp.valueOf(agingHoursAgo);
        Timestamp endTimestampTemp = Timestamp.valueOf(currentLocalDateTime);
        //找出一天内需重试的服务
        LambdaQueryWrapper<TicketFlowEventData> tfedLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tfedLambdaQueryWrapper.eq(TicketFlowEventData::getEventStatus, EventStatusEnum.EXECUTE_FAILURE_MIDDLE);
        tfedLambdaQueryWrapper.ge(TicketFlowEventData::getUpdateTime, startTimestampTemp);
        tfedLambdaQueryWrapper.le(TicketFlowEventData::getUpdateTime, endTimestampTemp);
        tfedLambdaQueryWrapper.and(LambdaQueryWrapper -> {
            LambdaQueryWrapper.like(TicketFlowEventData::getPushConfig, PushTypeEnum.AUTO_PUSH.getCode());
            LambdaQueryWrapper.notLike(TicketFlowEventData::getPushConfig, "attempt");
            LambdaQueryWrapper.or();
            LambdaQueryWrapper.isNull(TicketFlowEventData::getPushConfig);
        });
        List<TicketFlowEventData> ticketFlowEventDataList = ticketFlowEventDataService.getBaseMapper().selectList(tfedLambdaQueryWrapper);
        if (ObjectHelper.isEmpty(ticketFlowEventDataList)) {
            log.info("没有需自动重试的服务");
            return;
        }
        List<String> ticketDataIdList = ticketFlowEventDataList.stream().map(it -> it.getTicketDataId()).collect(Collectors.toList());
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery().isNull(TicketData::getDeleteTime).in(TicketData::getId, ticketDataIdList).list();
        if (ObjectHelper.isEmpty(ticketDataList)) {
            log.error("关联的工单记录不存在");
            return;
        }
        Iterator<TicketData> iterator = ticketDataList.iterator();
        while (iterator.hasNext()) {
            TicketData ticketData = iterator.next();
            if (TicketDataStatusEnum.WITHDRAW == ticketData.getTicketStatus()) {
                log.info("关联工单:{}已撤回，此事件无需重试", ticketData.getId());
                iterator.remove();
            }
        }
        Map<String, String> interfaceKeyMap = ticketDataList.stream()
                .map(it -> {
                    if (StringUtils.isEmpty(it.getInterfaceKey())) {
                        it.setInterfaceKey("");
                    }
                    return it;
                }).collect(Collectors.toMap(TicketData::getId, TicketData::getInterfaceKey));
        for (TicketFlowEventData ticketFlowEventData : ticketFlowEventDataList) {
            String ticketDataId = ticketFlowEventData.getTicketDataId();
            if (!interfaceKeyMap.containsKey(ticketDataId)) {
                log.info("关联工单:(id:{})不存在，此事件:(id:{})无需重试", ticketDataId, ticketFlowEventData.getId());
                continue;
            }
            String interfaceKey = interfaceKeyMap.get(ticketDataId);

            String pushConfig = ticketFlowEventData.getPushConfig();
            if (StringUtils.isEmpty(pushConfig)) {
                pushConfig = "{\"pushType\":\"AUTO_PUSH\",\"retryCount\":3,\"retryIntervalTime\":1}";
            }
            JSONObject pushConfigJson = JSONObject.parseObject(pushConfig);
            int retryCount = 0;
            int retryIntervalTime = 0;
            if (ObjectHelper.isNotEmpty(pushConfigJson.get("retryCount"))) {
                retryCount = (int) pushConfigJson.get("retryCount");
            }
            if (ObjectHelper.isNotEmpty(pushConfigJson.get("retryIntervalTime"))) {
                retryIntervalTime = (int) pushConfigJson.get("retryIntervalTime");
            }
            int attempt = 0;
            boolean success = false;
            //查询该事件不下同人的执行动作明细
            String ticketFlowEventDataId = ticketFlowEventData.getId();
            List<TicketFlowNodeApproveDetail> flowNodeApproveDetailList = ticketFlowNodeApproveDetailService.lambdaQuery()
                    .eq(TicketFlowNodeApproveDetail::getTicketFlowEventDataId, ticketFlowEventDataId)
                    .eq(TicketFlowNodeApproveDetail::getDealTypeCallback, DealTypeCallbackEnum.ACTION_FAILED)
                    .orderByDesc(TicketFlowNodeApproveDetail::getCreateTime)
                    .isNull(TicketFlowNodeApproveDetail::getDeleteTime)
                    .list();
            if (CollectionUtils.isEmpty(flowNodeApproveDetailList)) {
                log.error(String.format("执行动作(%s)明细不存在，不能重试", ticketFlowEventDataId));
                return;
            }
            //取最新的审批人进行审批回调
            TicketFlowNodeApproveDetail ticketFlowNodeApproveDetail = flowNodeApproveDetailList.get(0);
            String userName = ticketFlowNodeApproveDetail.getDealUserName();
            String userId = ticketFlowNodeApproveDetail.getDealUserId();
            String userType = ticketFlowNodeApproveDetail.getDealUserType();
            AccountInfo accountInfo = new AccountInfo("", userType, userId, userName);

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("detail_id", ticketFlowNodeApproveDetail.getId());
            paramsMap.put("detail_user_id", ticketFlowNodeApproveDetail.getDealUserId());
            paramsMap.put("detail_user_name", ticketFlowNodeApproveDetail.getDealUserName());
            paramsMap.put("detail_user_type", ticketFlowNodeApproveDetail.getDealUserType());
            paramsMap.put("detail_opinion", ticketFlowNodeApproveDetail.getDealOpinion());
            paramsMap.put("detail_type", ticketFlowNodeApproveDetail.getDealType().getCode());
            paramsMap.put("detail_type_des", ticketFlowNodeApproveDetail.getDealTypeDescription());

            while (attempt < retryCount && !success) {
                try {
                    var executeRes = ticketDataApproveService.executeEventCore(ticketDataId, interfaceKey, ticketFlowEventData, paramsMap, null, null);
                    attempt++; // 增加尝试次数
                    if (executeRes.getEnum().equals(BizResponseEnums.SUCCESS)) {
                        log.info("第{}次重试，成功返回", attempt);
                        success = true;
                        break;
                    }
                    log.info("第{}次重试，异常返回：{}", attempt, executeRes);
                } catch (Exception e) {
                    log.error(String.format("重试第%s次，异常原因：", attempt), e);
                }

                if (!success) {
                    // 如果不成功，则等待指定的间隔时间后再尝试
                    try {
                        if (retryIntervalTime > 0) {
                            TimeUnit.SECONDS.sleep(retryIntervalTime);
                        }
                    } catch (InterruptedException ie) {
                        // 处理等待期间的中断异常
                        Thread.currentThread().interrupt(); // 恢复中断状态
                        throw new RuntimeException("Retry sleep interrupted", ie);
                    }

                }
            }
            pushConfigJson.put("attempt", attempt);
            boolean updateFlag = ticketFlowEventDataService.lambdaUpdate()
                    .eq(TicketFlowEventData::getId, ticketFlowEventDataId)
                    .isNull(TicketFlowEventData::getDeleteTime)
                    .set(TicketFlowEventData::getPushConfig, pushConfigJson.toString())
                    .update();
            if (!updateFlag) {
                log.error("执行事件({})更新重试次数({}次)失败", ticketFlowEventDataId, attempt);
            }
            if (!success) {
                //如果重试后仍然失败，通知群
                Response response = ticketAppService.createQWGroupAndSendMsgByNode(ticketFlowEventData.getTicketFlowNodeDataId(), CallBackMsgStatusEnum.EXCEPTION_MSG_SENG);
                if (!response.isSuccess()) {
                    log.info("重试之后，发送异常消息，返回：{}", response);
                }
                continue;
            }
            //审批回调
            Response approveCallBackResp = approveCallBack(ticketFlowEventData, accountInfo);
            if (!approveCallBackResp.isSuccess()) {
                log.error("审批回调异常：{}", approveCallBackResp);
            }
        }
    }

}
