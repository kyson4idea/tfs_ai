package com.smy.tfs.quartz.task;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smy.ark.api.service.IArkService;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.AccountInfoDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.query.*;
import com.smy.tfs.api.dto.ticket_sla_service.Executor;
import com.smy.tfs.api.dto.ticket_sla_service.RemindMsg;
import com.smy.tfs.api.dto.ticket_sla_service.TicketSlaNoticeDto;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.bo.DubboServiceConfig;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.biz.service.TicketFlowNodeApproveDetailService;
import com.smy.tfs.common.utils.DateUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.config.DynamicDubboConsumer;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component("ticketSlaTask")
@Slf4j
public class TicketSlaTask {
    // 定义时间格式
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d+)([hm])$");
    @Resource
    private ITicketSlaConfigTemplateService ticketSlaConfigTemplateService;
    @Resource
    private IArkService arkService;
    @Resource
    private ITicketDataQueryService iTicketDataQueryService;
    @Resource
    private ITicketFlowNodeDataService iTicketFlowNodeDataService;
    @Resource
    private ITicketTemplateService ticketTemplateService;
    @Resource
    private DynamicDubboConsumer dynamicDubboConsumer;
    @Resource
    private ITicketAccountService ticketAccountService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private TfSJumpUrlProperties tfSJumpUrlProperties;
    @Resource
    private TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;
    @Resource
    private ITicketDataActService iTicketDataActService;
    @Resource
    private ITicketDataService ticketDataService;


    public void run(String templateId) {
        log.info("----------开始进行sla统计----------");
        String status = TicketSlaTemplateStatusEnum.OPEN.getCode();
        LambdaQueryWrapper<TicketSlaConfigTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TicketSlaConfigTemplate::getStatus, status);
        if (StringUtils.isNotEmpty(templateId)) {
            lambdaQueryWrapper.eq(TicketSlaConfigTemplate::getTicketTemplateId, templateId);
        }
        List<TicketSlaConfigTemplate> ticketSlaConfigTemplateList = ticketSlaConfigTemplateService.list(lambdaQueryWrapper);
        List<String> ticketTemplateIdList = new ArrayList<>();
        if (CollectionUtils.isEmpty(ticketSlaConfigTemplateList)) {
            ticketTemplateIdList = ticketSlaConfigTemplateList.stream().map(TicketSlaConfigTemplate::getTicketTemplateId).distinct().collect(Collectors.toList());
        }
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD_HH_MM_SS);
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        String currentTime = now.format(formatter);
        // 获取前30天时间
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        String thirtyDaysTime = thirtyDaysAgo.format(formatter);
        String userType = "ldap";
        String userId = "tfs_system";
        String userName = "tfs_system";
        List<SlaTagsQueryRspDto> slaTagsQueryRspDtoList ;

        //清除非sla配置模板的工单Tags
        do {
            SlaTagsQueryReqDto slaTagsQueryReqDto = new SlaTagsQueryReqDto();
            slaTagsQueryReqDto.setUpdateStartTime(thirtyDaysTime);
            slaTagsQueryReqDto.setUpdateEndTime(currentTime);
            slaTagsQueryReqDto.setTemplateIdList(ticketTemplateIdList);
            slaTagsQueryReqDto.setPageNum(1);
            slaTagsQueryReqDto.setPageSize(100);
            slaTagsQueryReqDto.setTagList(Arrays.asList("\"工单sla即将超时\"", "\"工单sla超时\""));
            List<SortDescriptor> sortDescriptorList = new ArrayList<>();
            sortDescriptorList.add(new SortDescriptor("create_time", "desc"));
            sortDescriptorList.add(new SortDescriptor("id.keyword", "desc"));
            slaTagsQueryReqDto.setSortDescriptorList(sortDescriptorList);
            //需要同时按照更新时间和id排序
            RemoteTableDataInfo tableDataInfo = iTicketDataQueryService.slaTagsQuery(slaTagsQueryReqDto, userType, userId, userName);
            if (Objects.isNull(tableDataInfo) || (200 != tableDataInfo.getCode() && 0 != tableDataInfo.getCode())) {
                log.error("根据条件({})查询工单列表异常:{}", JSONObject.toJSONString(slaTagsQueryReqDto), tableDataInfo.getMsg());
                break;
            }
            slaTagsQueryRspDtoList = tableDataInfo.getRows();
            if (CollectionUtils.isEmpty(tableDataInfo.getRows())) {
                log.info("非sla配置模板的tags标记的工单列表为空,无需清除");
                break;
            }
            for (SlaTagsQueryRspDto slaTagsQueryRspDto : slaTagsQueryRspDtoList) {
                TicketData ticketData = new TicketData();
                String ticketDataId =  slaTagsQueryRspDto.getId();
                ticketData.setId(ticketDataId);
                String tags = "[\"工单sla即将超时\",\"工单sla超时\"]";
                ticketData.setTags(tags);
                Response response = iTicketDataActService.delTags(ticketData,userType,userId,userName);
                if (!response.isSuccess()) {
                    log.error("非sla配置模板的tags标记的工单（ticketDataId:{}）清除tags:{} 异常:{}", ticketDataId, tags, response.getMsg());
                }
            }
        } while (CollectionUtils.isNotEmpty(slaTagsQueryRspDtoList));

        if (CollectionUtils.isEmpty(ticketSlaConfigTemplateList)) {
            log.warn("无开启的sla配置，无需统计");
            return;
        }

        //针对sla配置的超时工单进行提醒
        try {
            for (TicketSlaConfigTemplate ticketSlaConfigTemplate : ticketSlaConfigTemplateList) {
                Response checkParamsResp = checkParams(ticketSlaConfigTemplate);
                if (!checkParamsResp.isSuccess()) {
                    continue;
                }
                String configType = ticketSlaConfigTemplate.getConfigType();
                String ticketTemplateId = ticketSlaConfigTemplate.getTicketTemplateId();
                TicketTemplate ticketTemplate = ticketTemplateService.getById(ticketTemplateId);
                if (Objects.isNull(ticketTemplate) || StringUtils.isEmpty(ticketTemplate.getAppId())) {
                    log.error("模版(id:{})不存在，或者该模版所属业务(id:{})不存在:{}", ticketTemplateId, ticketTemplate.getAppId());
                    continue;
                }
                BusiQueryReqDto busiQueryReqDto = new BusiQueryReqDto();
                busiQueryReqDto.setAppIdList(Arrays.asList(ticketTemplate.getAppId()));
                busiQueryReqDto.setUpdateStartTime(thirtyDaysTime);
                busiQueryReqDto.setUpdateEndTime(currentTime);
                busiQueryReqDto.setTemplateIdList(Arrays.asList(ticketTemplateId));
                busiQueryReqDto.setTicketStatusList(Arrays.asList(TicketDataStatusEnum.APPLYING.getCode()));
                busiQueryReqDto.setUserDealType(BusiQueryUserDealTypeEnum.ALL_ALL);
                busiQueryReqDto.setPageNum(1);
                busiQueryReqDto.setPageSize(100);
                List<SortDescriptor> sortDescriptorList = new ArrayList<>();
                sortDescriptorList.add(new SortDescriptor("create_time","desc"));
                sortDescriptorList.add(new SortDescriptor("id.keyword","desc"));
                busiQueryReqDto.setSortDescriptorList(sortDescriptorList);
                List<BusiQueryRspDto> busiQueryRspDtoList ;
                do {
                    //需要同时按照更新时间和id排序
                    RemoteTableDataInfo tableDataInfo = iTicketDataQueryService.busiQuery(busiQueryReqDto, userType, userId, userName);
                    if (Objects.isNull(tableDataInfo) || (200 != tableDataInfo.getCode() && 0 != tableDataInfo.getCode())) {
                        log.error("根据条件({})查询工单列表异常:{}", JSONObject.toJSONString(busiQueryReqDto), tableDataInfo.getMsg());
                        break;
                    }
                    if(CollectionUtils.isEmpty(tableDataInfo.getRows())){
                        log.info("根据条件({})查询审批中的工单列表为空", JSONObject.toJSONString(busiQueryReqDto));
                        break;
                    }
                    busiQueryRspDtoList = tableDataInfo.getRows();
                    //时限
                    String timeoutStr = ticketSlaConfigTemplate.getTimeout();
                    Matcher timeoutMatcher = TIME_PATTERN.matcher(timeoutStr);
                    timeoutMatcher.find();
                    long timeout = Long.valueOf(timeoutMatcher.group(1)); // 提取数字部分
                    String timeoutUnit = timeoutMatcher.group(2).toLowerCase();    // 提取单位并转为小写
                    if (timeoutUnit.equals("h")) {
                        timeout = timeout * 60; // 小时转分钟
                    }
                    String remindConfigStr = ticketSlaConfigTemplate.getRemindConfig();
                    if (!JSONUtil.isJsonArray(remindConfigStr)) {
                        log.error(String.format("sla（id:%s）配置提醒规则remindConfig为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                        continue;
                    }
                    List<TicketSlaNoticeDto> ticketSlaNoticeDtoList = new ArrayList<>();
                    JSONArray jsonArray = JSON.parseArray(remindConfigStr);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if (Objects.isNull(jsonObject.getString("remindType"))) {
                            log.warn(String.format("sla（id:%s）配置提醒规则remindType为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                            continue;
                        }
                        String remindType = jsonObject.getString("remindType");
                        if ((remindType.equals("before") || remindType.equals("after")) && Objects.isNull(jsonObject.get("timeValue"))) {
                            log.warn(String.format("sla（id:%s）配置提醒规则timeValue为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                            continue;
                        }
                        if (Objects.isNull(jsonObject.get("timeValue"))) {
                            log.warn(String.format("sla（id:%s）配置提醒规则timeValue为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                            continue;
                        }
                        String timeValueStr = jsonObject.getString("timeValue");
                        // 正则表达式匹配数字 + h/m（不区分大小写）
                        Matcher timeValueMatcher = TIME_PATTERN.matcher(timeValueStr);
                        timeValueMatcher.find();
                        if (!timeValueMatcher.matches()) {
                            log.error(String.format("sla（id:%s）配置:无效的时间格式(%s)，请使用类似 '30h' 或 '30m' 的格式", ticketSlaConfigTemplate.getId(), timeValueStr));
                            continue;
                        }
                        long timeValue = Long.valueOf(timeValueMatcher.group(1)); // 提取数字部分
                        String timeValueUnit = timeValueMatcher.group(2).toLowerCase();    // 提取单位并转为小写
                        if (timeValueUnit.equals("h")) {
                            timeValue = timeValue * 60; // 小时转分钟
                        }
                        if (Objects.isNull(jsonObject.get("executors"))) {
                            log.error(String.format("sla（id:%s）配置提醒规则executors为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                            continue;
                        }
                        if (Objects.isNull(jsonObject.get("remindMsg"))) {
                            log.error(String.format("sla（id:%s）配置提醒规则remindMsg为空，配置出错，跳过...", ticketSlaConfigTemplate.getId()));
                            continue;
                        }
                        TicketSlaNoticeDto ticketSlaNoticeDto = new TicketSlaNoticeDto(remindType, timeValue, jsonObject.getString("executors"), jsonObject.getString("remindMsg"));
                        ticketSlaNoticeDtoList.add(ticketSlaNoticeDto);
                    }
                    switch (configType) {
                        case "FLOW_NODE":
                            String configTypeContent = ticketSlaConfigTemplate.getConfigTypeContent();
                            List<String> flowNodeTemplateIdList = Arrays.asList(configTypeContent.split(","));
                            for (BusiQueryRspDto busiQueryRspDto : busiQueryRspDtoList) {
                                String ticketDataId = busiQueryRspDto.getId();
                                LambdaQueryWrapper<TicketFlowNodeData> tfndLambdaQueryWrapper = new LambdaQueryWrapper<>();
                                tfndLambdaQueryWrapper.eq(TicketFlowNodeData::getTicketDataId, ticketDataId);
                                List<TicketFlowNodeData> ticketFlowNodeDataList = iTicketFlowNodeDataService.list(tfndLambdaQueryWrapper);
                                if (CollectionUtils.isEmpty(ticketFlowNodeDataList)) {
                                    log.error(String.format("异常：工单(id:%s)不存在审批节点，无法统计节点的sla", ticketDataId));
                                    continue;
                                }
                                List<TicketFlowNodeData> approvingTicketFlowNodeDataList = ticketFlowNodeDataList.stream()
                                        .filter(it -> NodeStatusEnum.APPROVING == it.getNodeStatus())
                                        .filter(it -> flowNodeTemplateIdList.contains(it.getTemplateId()))
                                        .collect(Collectors.toList());
                                if (CollectionUtils.isEmpty(approvingTicketFlowNodeDataList)) {
                                    log.info(String.format("工单(id:%s)的配置sla节点（%s）不在审批中，无需统计", ticketDataId, JSONObject.toJSONString(ticketFlowNodeDataList)));
                                    continue;
                                }
                                if (approvingTicketFlowNodeDataList.size()>1) {
                                    log.error(String.format("异常：工单(id:%s)在审批中的节点不只1个，无法统计节点的sla", ticketDataId));
                                    continue;
                                }
                                //正在审批的节点
                                TicketFlowNodeData approvingTicketFlowNodeData = approvingTicketFlowNodeDataList.get(0);
                                //正在审批的节点的上一个节点
                                String preNodeId = approvingTicketFlowNodeData.getPreNodeId();
                                if ("-1".equals(preNodeId)) {
                                    log.error(String.format("异常：工单(id:%s)审批中的节点上一个节点为-1，无法统计节点的sla", ticketDataId));
                                    continue;
                                }
                                Optional<TicketFlowNodeData> preNodeOpt = ticketFlowNodeDataList.stream()
                                        .filter(node -> node.getId().equals(preNodeId)) // 过滤条件：id等于preNodeId
                                        .findFirst();
                                if (!preNodeOpt.isPresent()) {
                                    log.error(String.format("异常：工单(id:%s)审批中的节点上一个节点(%s)不存在，无法统计节点的sla", ticketDataId, preNodeId));
                                    continue;
                                }
                                TicketFlowNodeData ticketFlowNodeData = preNodeOpt.get();
                                Date preNodeEndTime= ticketFlowNodeData.getUpdateTime();
                                Date nowDate = new Date();
                                long diffInMinutes = Math.abs(nowDate.getTime() - preNodeEndTime.getTime()) / (60 * 1000);
                                batchNotice(ticketSlaConfigTemplate.getId(), ticketSlaNoticeDtoList, busiQueryRspDto, timeout, diffInMinutes);
                            }
                            break;
                        case "TICKET":
                            for (BusiQueryRspDto busiQueryRspDto : busiQueryRspDtoList) {
                                String createTimeStr = busiQueryRspDto.getCreateTime();
                                Date createTime = DateUtils.parseDate(createTimeStr);
                                Date nowDate = DateUtils.getNowDate();
                                long diffInMinutes = Math.abs(nowDate.getTime() - createTime.getTime()) / (60 * 1000);
                                batchNotice(ticketSlaConfigTemplate.getId(), ticketSlaNoticeDtoList, busiQueryRspDto, timeout, diffInMinutes);
                            }
                            break;
                        default:
                            log.error(String.format("不支持的sla（id:%s）配置方式（%s）！", ticketSlaConfigTemplate.getId(), configType));
                    }
                    busiQueryReqDto.setPageNum(busiQueryReqDto.getPageNum() + 1);
                } while (CollectionUtils.isNotEmpty(busiQueryRspDtoList));
            }
        } catch (Exception e) {
            log.error("sla统计异常：{}", e);
        }
        log.info("----------结束进行sla统计----------");
    }

    private void batchNotice(String ticketSlaConfigTemplateId, List<TicketSlaNoticeDto> ticketSlaNoticeDtoList, BusiQueryRspDto busiQueryRspDto, long timeout, long diffInMinutes) {
         String ticketDataId = busiQueryRspDto.getId();
         if (CollectionUtils.isEmpty(ticketSlaNoticeDtoList)) {
             log.error("工单sla通知配置(ticketSlaConfigTemplateId:{})列表为空", ticketSlaConfigTemplateId);
             return;
         }
        List<String> addTagList = new ArrayList();
        for (TicketSlaNoticeDto ticketSlaNoticeDto : ticketSlaNoticeDtoList) {
            List<AccountInfoDto> accountInfoDtoList = getAccountInfoDtoList(ticketSlaConfigTemplateId, busiQueryRspDto, ticketSlaNoticeDto.getExecutorList());
            String noticeRemindType = ticketSlaNoticeDto.getRemindType();
            List<RemindMsg> remindMsgList = ticketSlaNoticeDto.getRemindMsgList();
            List<String> remindMsgTypeList = new ArrayList();
            if (CollectionUtils.isNotEmpty(remindMsgList)) {
                remindMsgTypeList = remindMsgList.stream().map(it -> it.getRemindMsgType()).collect(Collectors.toList());
            }
            switch (noticeRemindType) {
                case "before":
                    long beforeTimeValue = ticketSlaNoticeDto.getTimeValue();
                    if (timeout - diffInMinutes > 0 && timeout - diffInMinutes <= beforeTimeValue) {
                        String msg = String.format("您的工单（%s）%s分钟后即将超时，请及时处理。", ticketDataId, timeout - diffInMinutes);
                        if (CollectionUtils.isNotEmpty(remindMsgTypeList) && remindMsgTypeList.contains("TAG")) {
                            addTagList.add("工单sla即将超时");
                        }
                        notice(ticketSlaConfigTemplateId, accountInfoDtoList, msg, ticketDataId, ticketSlaNoticeDto.getRemindMsgList());
                    }
                    break;
                case "after":
                    long afterTimeValue = ticketSlaNoticeDto.getTimeValue();
                    if (diffInMinutes - timeout > 0 && diffInMinutes - timeout >= afterTimeValue) {
                        String msg = String.format("您的工单（%s）已超时%s分钟，请及时处理。", ticketDataId, diffInMinutes - timeout);
                        if (CollectionUtils.isNotEmpty(remindMsgTypeList) && remindMsgTypeList.contains("TAG")) {
                            addTagList.add("工单sla超时");
                        }
                        notice(ticketSlaConfigTemplateId, accountInfoDtoList, msg, ticketDataId, ticketSlaNoticeDto.getRemindMsgList());
                    }
                    break;
                case "current":
                    if (diffInMinutes - timeout >= 0) {
                        String msg = String.format("您的工单（%s）已超时，请及时处理。", ticketDataId);
                        if (CollectionUtils.isNotEmpty(remindMsgTypeList) && remindMsgTypeList.contains("TAG")) {
                            addTagList.add("工单sla超时");
                        }
                        notice(ticketSlaConfigTemplateId, accountInfoDtoList, msg, ticketDataId, ticketSlaNoticeDto.getRemindMsgList());
                    }
                    break;
                default:
                    log.error(String.format("不支持的sla（id:%s）通知配置方式（%s）！", ticketSlaConfigTemplateId, noticeRemindType));
            }
        }
        //查询现有工单的tags
        List<TicketData> ticketDataList = ticketDataService.lambdaQuery().eq(TicketData::getId, ticketDataId).list();
        if (CollectionUtils.isEmpty(ticketDataList)) {
            log.error("工单(ticketDataId:{})对象查询为空", ticketDataId);
            return;
        }
        TicketData ticketData = ticketDataList.get(0);
        List<String> existTagList = new ArrayList();
        if (StringUtils.isNotEmpty(ticketData.getTags())) {
            String existTagStr = ticketData.getTags().trim();
            if (existTagStr.startsWith("[") && existTagStr.endsWith("]")) {
                existTagList = JSONObject.parseObject(existTagStr, List.class);
            } else {
                existTagList = new ArrayList<>(Arrays.asList(existTagStr.split(",")));
            }
        }

        List<String> addExistTagList = new ArrayList();
        List<String> removeExistTagList = new ArrayList();
        if (CollectionUtils.isNotEmpty(addTagList)) {
            //如果existTagList里面有addTagList的值，则删除；如果addTagList里面有existTagList的值，则添加。
            if (addTagList.contains("工单sla超时") && !existTagList.contains("工单sla超时")) {
                addExistTagList.add("工单sla超时");
            } else if (!addTagList.contains("工单sla超时") && existTagList.contains("工单sla超时")) {
                removeExistTagList.add("工单sla超时");
            }
            if (addTagList.contains("工单sla即将超时") && !existTagList.contains("工单sla即将超时")) {
                addExistTagList.add("工单sla即将超时");
            } else if (!addTagList.contains("工单sla即将超时") && existTagList.contains("工单sla即将超时")) {
                removeExistTagList.add("工单sla即将超时");
            }
        } else {
            //如果addTagList没有值，则删除。
            removeExistTagList.add("工单sla超时");
            removeExistTagList.add("工单sla即将超时");
        }
        if (CollectionUtils.isNotEmpty(addExistTagList)) {
            List<String> addExistTagListWithoutDuplicates = addExistTagList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            addExistTagListWithoutDuplicates = addExistTagListWithoutDuplicates.stream().map(it->  "\"" + it + "\"").collect(Collectors.toList());
            TicketData td = new TicketData();
            td.setId(ticketDataId);
            td.setTags(addExistTagListWithoutDuplicates.toString());
            String userType = "ldap";
            String userId = "tfs_system";
            String userName = "tfs_system";
            Response response = iTicketDataActService.addTags(td, userType, userId, userName);
            if (!response.isSuccess()) {
                log.error("工单sla（ticketSlaConfigTemplateId:{},ticketDataId:{}）添加tags:{} 异常:{}", ticketSlaConfigTemplateId, ticketDataId, addExistTagListWithoutDuplicates.toString(), response.getMsg());
            }
        }
        if (CollectionUtils.isNotEmpty(removeExistTagList)) {
            List<String> removeExistTagListWithoutDuplicates = removeExistTagList.stream()
                    .distinct()
                    .collect(Collectors.toList());
            removeExistTagListWithoutDuplicates = removeExistTagListWithoutDuplicates.stream().map(it->  "\"" + it + "\"").collect(Collectors.toList());
            TicketData td = new TicketData();
            td.setId(ticketDataId);
            td.setTags(removeExistTagListWithoutDuplicates.toString());
            String userType = "ldap";
            String userId = "tfs_system";
            String userName = "tfs_system";
            Response response = iTicketDataActService.delTags(td, userType, userId, userName);
            if (!response.isSuccess()) {
                log.error("工单sla（ticketSlaConfigTemplateId:{},ticketDataId:{}）删除tags:{} 异常:{}", ticketSlaConfigTemplateId, ticketDataId, removeExistTagListWithoutDuplicates.toString(), response.getMsg());
            }
        }

    }

    private List<AccountInfoDto> getAccountInfoDtoList(String ticketSlaConfigTemplateId, BusiQueryRspDto busiQueryRspDto, List<Executor> executorList) {
        List<AccountInfoDto> accountInfoDtoAllList = new ArrayList<>();
        for (Executor executor : executorList) {
            List<AccountInfoDto> accountInfoDtoList = new ArrayList<>();
            String executorType = executor.getExecutorType();
            switch (executorType) {
                //发起人
                case "APPLYER":
                    accountInfoDtoList = AccountInfoDto.ToAccountInfoDtoList(busiQueryRspDto.getOriginalApplyUser());
                    break;
                //当前审批人
                case "APPROVER":
                    accountInfoDtoList = AccountInfoDto.ToAccountInfoDtoList(busiQueryRspDto.getOriginalCurrentDealUsers());
                    break;
                //审批人上级
                case "APPROVER_LEADER":
                    List<AccountInfoDto> approverList = AccountInfoDto.ToAccountInfoDtoList(busiQueryRspDto.getOriginalCurrentDealUsers());
                    for (AccountInfoDto approver : approverList) {
                        TicketRemoteAccountDto leader = ticketAccountService.getLeaderByTypeAndId(approver.getAccountType(), approver.getAccountId());
                        if (null == leader) {
                            ticketAccountService.notifyQwMsg(
                                    String.format("未找到用户【userType:%s userId:%s】上级，请及时处理！", approver.getAccountType(), approver.getAccountId()),
                                    Arrays.asList("songbing", "owen", "zhangzedong")
                            );
                        } else {
                            accountInfoDtoList.add(new AccountInfoDto(leader.getUserType(), leader.getUserId(), leader.getSameOriginId(), leader.getUserName(), leader.getQywxId()));
                        }
                    }
                    break;
                //当前流程节点全员
                case "CURRENT_FLOW_NODE_ALL":
                    List<AccountInfoDto> currentDealUserList = AccountInfoDto.ToAccountInfoDtoList(busiQueryRspDto.getOriginalCurrentDealUsers());
                    String currentNodeId = busiQueryRspDto.getCurrentNodeId();
                    List <AccountInfoDto> currentFlowNodeDoneUserList = new ArrayList<>();
                    if (StringUtils.isNotEmpty(currentNodeId)) {
                        List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailList = ticketFlowNodeApproveDetailService.lambdaQuery()
                                .eq(TicketFlowNodeApproveDetail::getTicketFlowNodeDataId, currentNodeId)
                                .in(TicketFlowNodeApproveDetail::getDealType, Arrays.asList(ApproveDealTypeEnum.PASS, ApproveDealTypeEnum.DISPATCH))
                                .list();
                        if (CollectionUtils.isNotEmpty(ticketFlowNodeApproveDetailList)) {
                            currentFlowNodeDoneUserList = ticketFlowNodeApproveDetailList.stream()
                                    .map(it -> new AccountInfoDto(null, it.getDealUserType(), it.getDealUserId(), it.getDealUserName(), null))
                                    .collect(Collectors.toList());
                        }
                    }
                    accountInfoDtoList.addAll(currentDealUserList);
                    accountInfoDtoList.addAll(currentFlowNodeDoneUserList);
                    break;
                //当前工单已审批全员
                case "CURRENT_TICKET_APPROVED_ALL":
                    accountInfoDtoList = AccountInfoDto.ToAccountInfoDtoList(busiQueryRspDto.getOriginalCurrentDoneUsers());
                    break;
                //指定成员
                case "APPLY_MEMBER_LIST":
                    String executorValue = executor.getExecutorValue();
                    accountInfoDtoList = AccountInfoDto.ToAccountInfoDtoList(executorValue);
                    if (CollectionUtils.isEmpty(accountInfoDtoList)) {
                        log.error(String.format("sla（id:%s）配置提醒规则executorValue转换为账户信息为空，配置出错，跳过...", ticketSlaConfigTemplateId));
                        break;
                    }
                    break;
            }
            accountInfoDtoAllList.addAll(accountInfoDtoList);
        }
        List<AccountInfoDto> distinctAccountInfoDtoAllList = AccountInfoDto.distinct(accountInfoDtoAllList);
        if (CollectionUtils.isNotEmpty(distinctAccountInfoDtoAllList)) {
            distinctAccountInfoDtoAllList = distinctAccountInfoDtoAllList.stream().map(it -> {
                if (StringUtils.isEmpty(it.getQywxId()) || StringUtils.isEmpty(it.getSameOriginId())) {
                    TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(it.getAccountId(), it.getAccountType());
                    if (ticketRemoteAccountDto != null && org.apache.commons.lang3.StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId())) {
                        it.setQywxId(ticketRemoteAccountDto.getQywxId());
                    }
                    if (ticketRemoteAccountDto != null && org.apache.commons.lang3.StringUtils.isNotEmpty(ticketRemoteAccountDto.getSameOriginId())) {
                        it.setSameOriginId(ticketRemoteAccountDto.getSameOriginId());
                    }
                }
                return it;
            }).collect(Collectors.toList());
        }
        return distinctAccountInfoDtoAllList;
    }

    private void notice(String ticketSlaConfigTemplateId, List<AccountInfoDto> accountInfoDtoList, String msg, String ticketDataId, List<RemindMsg> remindMsgList) {
        for (RemindMsg remindMsg : remindMsgList) {
            String remindMsgType = remindMsg.getRemindMsgType();
            switch (remindMsgType) {
                case "WECOM":
                    try {
                        log.info("企微通知sla消息内容：{}", msg);
                        NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
                        qwcardMsg.setTitle("工单（审批）超时");
                        qwcardMsg.setDescription(msg);
                        List<String> userIdList = accountInfoDtoList.stream().map(it -> it.getQywxId()).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(userIdList)) {
                            log.error("发送sla（ticketSlaConfigTemplateId:{}）企微通知账户信息为空", ticketSlaConfigTemplateId);
                            continue;
                        }
                        Map<String, String> linkMap = new HashMap<>();
                        StringBuilder jumpUrlStringBuilder = new StringBuilder(tfSJumpUrlProperties.getTicketDetailUrl()).append(ticketDataId);
                        String jumpUrl = jumpUrlStringBuilder.toString();
                        linkMap.put("工单详情", jumpUrl);
                        qwcardMsg.setLinkKeyMap(linkMap);
                        qwcardMsg.setUserIdList(userIdList);
                        qwcardMsg.setJumpUrl(jumpUrl);
                        notificationService.notifyQwCard(qwcardMsg);
                    } catch (Exception e) {
                        log.error("调用sla（ticketSlaConfigTemplateId:{}）企微通知服务异常：{}", ticketSlaConfigTemplateId, e);
                    }
                    break;
                case "INTERFACE":
                    log.info("dubbo服务通知sla消息内容：{}", msg);
                    String interfaceConfig = remindMsg.getInterfaceConfig();
                    var dubboServiceConfigRes = DubboServiceConfig.parseStrToDubboConfig(interfaceConfig);
                    if (dubboServiceConfigRes.getEnum() != BizResponseEnums.SUCCESS) {
                        log.error("发送sla（ticketSlaConfigTemplateId:{}）通知dubbo服务配置解析失败,配置内容：{}", ticketSlaConfigTemplateId, interfaceConfig);
                        continue;
                    }
                    var dubboServiceConfig = dubboServiceConfigRes.getData();
                    List<AccountInfo> accountInfoList = accountInfoDtoList.stream()
                            .map(it -> new AccountInfo(it.getSameOriginId(), it.getAccountType(), it.getAccountId(), it.getAccountName()))
                            .collect(Collectors.toList());
                    String accountInfoListStr = JSONObject.toJSONString(accountInfoList);
                    try {
                        Object invokeResult = dynamicDubboConsumer.invokeDubboService(dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), new Object[]{accountInfoListStr, msg}, dubboServiceConfig.getVersion(), dubboServiceConfig.getGroup());
                        if (invokeResult instanceof String) {
                            String invokerStr = JSONUtil.toJsonStr(invokeResult);
                            if (invokerStr.contains("code:0") || invokerStr.contains("\"code\":0") || invokerStr.contains("\"code\":\"0\"")) {
                                log.info("调用sla通知服务成功");
                            } else {
                                log.error("调用sla（ticketSlaConfigTemplateId:{}）通知服务失败, 接口名：{}, 方法名：{}, 入参：(账户：{},消息：{}), 返回结果：{}", ticketSlaConfigTemplateId, dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), accountInfoList, msg, invokerStr);
                            }
                        } else {
                            com.alibaba.fastjson.JSONObject invokeJsonObject = com.alibaba.fastjson.JSONObject.parseObject(JSONUtil.toJsonStr(invokeResult));
                            if (invokeJsonObject.containsKey("code") && "0".equals(invokeJsonObject.getString("code")) || "200".equals(invokeJsonObject.getString("code")) || 0 == invokeJsonObject.getIntValue("code") || 200 == invokeJsonObject.getIntValue("code")) {
                                log.info("调用sla通知服务成功");
                            } else {
                                log.error("调用sla（ticketSlaConfigTemplateId:{}）通知服务失败,接口名：{}, 方法名：{}, 入参：(账户：{},消息：{}), 返回结果：{}", ticketSlaConfigTemplateId, dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), accountInfoList, msg, JSONUtil.toJsonStr(invokeResult));
                            }
                        }
                    } catch (Exception ex) {
                        log.error("调用sla（ticketSlaConfigTemplateId:{}）通知服务异常,配置内容：{} 异常信息：{}", ticketSlaConfigTemplateId, dubboServiceConfig, ex);
                    }
                    break;
                default:
                    log.error(String.format("不支持的sla（id:%s）通知提醒方式（%s）！", ticketSlaConfigTemplateId, remindMsgType));
            }
        }
    }


    private Response checkParams(TicketSlaConfigTemplate ticketSlaConfigTemplate) {
        String executeTime = ticketSlaConfigTemplate.getStartEndTime();
        if (StringUtils.isEmpty(executeTime)) {
            String errMsg = String.format("sla（id:%s）执行时间未配置",ticketSlaConfigTemplate.getId());
            log.error(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        String[] timeRange = parseTimeRange(executeTime);
        // 解析开始时间和结束时间
        LocalTime startTime = LocalTime.parse(timeRange[0], TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(timeRange[1], TIME_FORMATTER);
        // 获取当前时间
        LocalTime currentTime = LocalTime.now();
        if (!startTime.isBefore(endTime)) {
            String errMsg = String.format("sla（id:%s）执行开始时间应小于执行结束时间",ticketSlaConfigTemplate.getId());
            log.error(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        boolean isCurrentTimeInRange = currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        // 判断当前时间是否在范围内
        if (!isCurrentTimeInRange) {
            String errMsg = String.format("当前时间不在sla（id:%s）执行时间范围(%s)内，不执行分配",ticketSlaConfigTemplate.getId(), ticketSlaConfigTemplate.getStartEndTime());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        String configType = ticketSlaConfigTemplate.getConfigType();
        if (StringUtils.isEmpty(configType)) {
            String errMsg = String.format("sla（id:%s）配置方式为空",ticketSlaConfigTemplate.getId());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        if (ConfigTypeEnums.FLOW_NODE.getCode().equals(configType) && StringUtils.isEmpty(ticketSlaConfigTemplate.getConfigTypeContent())) {
            String errMsg = String.format("当sla（id:%s）配置方式为流程节点时，配置方式内容不能为空", ticketSlaConfigTemplate.getId());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        String timeout = ticketSlaConfigTemplate.getTimeout();
        if (StringUtils.isEmpty(timeout)) {
            String errMsg = String.format("sla（id:%s）时限为空",ticketSlaConfigTemplate.getId());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        if (!isValidTime(timeout)) {
            String errMsg = String.format("sla（id:%s）时限格式（%s）异常", ticketSlaConfigTemplate.getId(), ticketSlaConfigTemplate.getTimeout());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        if (StringUtils.isEmpty(ticketSlaConfigTemplate.getRemindConfig())) {
            String errMsg = String.format("sla（id:%s）提醒规则为空",ticketSlaConfigTemplate.getId());
            log.warn(errMsg);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, errMsg);
        }
        return Response.success();
    }

    private static  boolean isValidTime(String time) {
        String regex = "^[1-9]\\d*[hm]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }


    private String[] parseTimeRange(String timeRangeStr) {
        if (timeRangeStr == null || !timeRangeStr.startsWith("[") || !timeRangeStr.endsWith("]")) {
            throw new IllegalArgumentException("时间范围格式应为[\"HH:mm:ss\", \"HH:mm:ss\"]");
        }
        String cleaned = timeRangeStr.substring(1, timeRangeStr.length() - 1)
                .replace("\"", "")
                .replace(" ", "");
        return cleaned.split(",");
    }

}
