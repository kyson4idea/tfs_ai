package com.smy.tfs.biz.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.fsp.client.urlsign.UrlSignUtil;
import com.smy.tfs.api.constants.SFunctionMap;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.CompareInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.*;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.biz.bo.TicketTemplateAllBO;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.util.BusiTicketDataUtil;
import com.smy.tfs.common.utils.AesUtil;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
public class TicketDataServiceInner {

    @Resource
    private static AccountReturnComponent accountReturnComponent;

    public static Response TicketDataStdDtoParamCheck(
            TicketDataStdDto ticketDataStdDto,
            TicketData updateTicketData
    )
    {

        if (ticketDataStdDto == null || StringUtils.isBlank(ticketDataStdDto.getApplyId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        if (CollectionUtils.isEmpty(ticketDataStdDto.getFormItems())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单数据不能为空");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response TicketDataStdDtoCreatedParamCheck(
            TicketDataStdDto ticketDataStdDto,
            TicketData updateTicketData
    )
    {

        if (ticketDataStdDto == null || StringUtils.isBlank(ticketDataStdDto.getApplyId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "applyId 参数为空, applyId 需通过获取工单号接口生成");
        }
        if (StringUtils.isBlank(ticketDataStdDto.getTicketTemplateId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "ticketTemplateId 参数为空. ticketTemplateId可以是工单自动生成的ID，也可以是手动配置的CODE");
        }
        if (CollectionUtils.isEmpty(ticketDataStdDto.getFormItems())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单数据不能为空");
        }
        if (updateTicketData.getTicketStatus() != TicketDataStatusEnum.INIT) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单申请编号ID:%s ，状态:%s不正确", updateTicketData.getId(), updateTicketData.getTicketStatus().toString()));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "成功");
    }


    public static Response<String> GotoFlowNodeParamCheck(TicketData ticketData, String currentNodeId){

        if (ticketData == null) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "工单数据不存在");
        }
        if (ticketData.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 状态不在审批中", ticketData.getId()));
        }
        if (!Objects.equals(ticketData.getCurrentNodeId(), currentNodeId)) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 处理节点：%s 不匹配 %s ", ticketData.getId(), ticketData.getCurrentNodeId(), currentNodeId));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response<List<TicketFlowNodeData>> BuildDoneFlowNodeDataList(TicketDataDto ticketDataDto){

        List<TicketFlowNodeData> ticketFlowNodeDataList = new ArrayList<>();
        return new Response<>(ticketFlowNodeDataList, BizResponseEnums.SUCCESS, "成功");
    }

    // A->B->C->D    a->b->B->C-D（C退回到B，新增B） a->b->A->B->C-D（C退回到A，新增A->B）
    public static Response<List<TicketFlowNodeData>> BuildNextNewFlowNodeDataList(TicketData ticketData, List<TicketFlowNodeData> allTicketFlowNodeDataList, String gotoNodeId){

        List<TicketFlowNodeData> ticketFlowNodeDataList = new ArrayList<>();
        if (StringUtils.isEmpty(ticketData.getBaseFlow())) {
            return new Response<>(ticketFlowNodeDataList, BizResponseEnums.SUCCESS, "成功");
        }
        if (CollectionUtils.isEmpty(allTicketFlowNodeDataList)) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "数据节点为空");
        }
        String currentNodeId = ticketData.getCurrentNodeId();
        TicketFlowNodeData currentNode = allTicketFlowNodeDataList.stream().filter(x -> x.getId().equals(currentNodeId)).findFirst().orElse(null);
        if (currentNode == null) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 节点：%s 不存在", ticketData.getId(), currentNodeId));
        }
        String currentTemplateID = currentNode.getTemplateId();

        List<String> baseNodeList = JSONArray.parseArray(ticketData.getBaseFlow(), String.class);
        if (CollectionUtils.isEmpty(baseNodeList)) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 基础路径为空", ticketData.getId()));
        }
        if (gotoNodeId.startsWith("-") && gotoNodeId.length() > 1) {
            int preStep = Integer.parseInt(gotoNodeId.substring(1));
            int currentStep = -1;
            for (int i = 0; i < baseNodeList.size(); i++) {
                if (baseNodeList.get(i).startsWith(currentNodeId + "-")) {
                    currentStep = i;
                    break;
                }
                if (i == baseNodeList.size() - 1) {
                    return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 节点：%s 不在基础路径中", ticketData.getId(), currentNodeId));
                }
            }
            if (currentStep - preStep < 0) {
                return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 节点：%s 最大退回节点数是：%s ", ticketData.getId(), currentNodeId, currentStep));
            }
            gotoNodeId = baseNodeList.get(currentStep - preStep);
            gotoNodeId = gotoNodeId.substring(0, gotoNodeId.indexOf("-"));
        }
        for (String baseNode : baseNodeList) {
            int spIndex = baseNode.indexOf("-");
            String nodeId = baseNode.substring(0, spIndex);
            String nodeName = baseNode.substring(spIndex + 1, baseNode.length());
            TicketFlowNodeData thisNode = allTicketFlowNodeDataList.stream().filter(x -> x.getId().equals(nodeId)).findFirst().orElse(null);
            if (thisNode == null) {
                return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应工单ID:%s 节点：%s 不存在", ticketData.getId(), nodeId));
            }
            if (currentTemplateID.equals(thisNode.getTemplateId())) {
                break;
            }
            if (nodeId.equals(gotoNodeId) || CollectionUtils.isNotEmpty(ticketFlowNodeDataList)) {
                ticketFlowNodeDataList.add(thisNode);
            }
        }
        return new Response<>(ticketFlowNodeDataList, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response<List<TicketFlowNodeData>> BuildNextOldFlowNodeDataList(TicketDataDto ticketDataDto){

        List<TicketFlowNodeData> ticketFlowNodeDataList = new ArrayList<>();
        return new Response<>(ticketFlowNodeDataList, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response TicketDataDynamicDtoParamCheck(TicketDataDynamicDto ticketDataDynamicDto, TicketData updateTicketData){

        if (ticketDataDynamicDto == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        if (ticketDataDynamicDto.getTicketFormDataDynamicDto() == null
                || ticketDataDynamicDto.getTicketFormDataDynamicDto().getTicketFormItemDataDtoList() == null
                || ticketDataDynamicDto.getTicketFormDataDynamicDto().getTicketFormItemDataDtoList().isEmpty()
        )
        {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单数据不能为空");
        }
        if (ticketDataDynamicDto.getTicketFlowDataDynamicDto() == null
                || ticketDataDynamicDto.getTicketFlowDataDynamicDto().getTicketFlowNodeDataDynamicDtoList() == null
                || ticketDataDynamicDto.getTicketFlowDataDynamicDto().getTicketFlowNodeDataDynamicDtoList().isEmpty()
        )
        {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程数据不能为空");
        }
        if (updateTicketData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单申请编号ID:%s ，数据不存在", ticketDataDynamicDto.getId()));
        }
        if (updateTicketData.getTicketStatus() == TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.IDEMPOTENT, String.format("对应工单申请编号ID:%s ，状态正在申请中", updateTicketData.getId(), updateTicketData.getTicketStatus().toString()));
        }
        if (updateTicketData.getTicketStatus() != TicketDataStatusEnum.INIT) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单申请编号ID:%s ，状态:%s不正确", updateTicketData.getId(), updateTicketData.getTicketStatus().toString()));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "成功");
    }

    public static void TicketDataDynamicDtoDataFill(
            TicketDataDynamicDto dynamicDto,
            TicketData updateTicketData,
            TicketFormData newTicketFormData,
            List<TicketFormItemData> newTicketFormItemDataList,
            TicketFlowData newTicketFlowData,
            List<TicketFlowNodeData> newTicketFlowNodeDataList,
            List<TicketFlowNodeExecutorData> newTicketFlowNodeExecutorDataList
    )
    {
        //填充TicketData
        updateTicketData.setTicketStatus(TicketDataStatusEnum.APPLYING);
        updateTicketData.setAppId(dynamicDto.getAppId());
        updateTicketData.setTicketName(dynamicDto.getTicketName());
        updateTicketData.setTicketMsgArriveType(TicketMsgArriveTypeEnum.NULL);
        updateTicketData.setTicketFormChangeFlag(YESNOEnum.NO);
        updateTicketData.setUpdateBy("sys");
        updateTicketData.setUpdateTime(new Date());
        //填充TicketFormData
        newTicketFormData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_DATA));
        newTicketFormData.setTicketDataId(updateTicketData.getId());
        newTicketFormData.setTemplateId(updateTicketData.getTemplateId());
        newTicketFormData.setCreateBy("sys");
        newTicketFormData.setUpdateBy("sys");
        newTicketFormData.setCreateTime(new Date());
        newTicketFormData.setUpdateTime(new Date());
        //填充TicketFormItemData
        List<TicketFormItemDataDynamicDto> ticketFormItemDataDtoList = dynamicDto.getTicketFormDataDynamicDto().getTicketFormItemDataDtoList();
        for (var item : ticketFormItemDataDtoList) {
            TicketFormItemData ticketFormItemData = new TicketFormItemData();
            ticketFormItemData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA));
            ticketFormItemData.setTicketDataId(updateTicketData.getId());
            ticketFormItemData.setTicketFormDataId(newTicketFormData.getId());
            ticketFormItemData.setTemplateId("-1");//动态数据 templateId为-1
            ticketFormItemData.setItemOrder(item.getItemOrder());
            ticketFormItemData.setItemType(item.getItemType());
            ticketFormItemData.setItemConfig("-1");
            ticketFormItemData.setItemConfigExt("-1");
            ticketFormItemData.setItemValue(item.getItemValue());
            ticketFormItemData.setItemLabel(item.getItemLabel());
            ticketFormItemData.setCreateBy("sys");
            ticketFormItemData.setUpdateBy("sys");
            ticketFormItemData.setCreateTime(new Date());
            ticketFormItemData.setUpdateTime(new Date());
            newTicketFormItemDataList.add(ticketFormItemData);
        }
        //填充TicketFlowData
        TicketFlowDataDynamicDto ticketFlowDataDynamicDto = dynamicDto.getTicketFlowDataDynamicDto();
        newTicketFlowData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_DATA));
        newTicketFlowData.setTicketDataId(updateTicketData.getId());
        newTicketFlowData.setTemplateId("-1");//动态数据 templateId为-1
        newTicketFlowData.setStartCc(ticketFlowDataDynamicDto.getStartCc());
        newTicketFlowData.setEndCc(ticketFlowDataDynamicDto.getEndCc());
        newTicketFlowData.setCreateBy("sys");
        newTicketFlowData.setUpdateBy("sys");
        newTicketFlowData.setCreateTime(new Date());
        newTicketFlowData.setUpdateTime(new Date());
        //填充TicketFlowNodeData
        List<TicketFlowNodeDataDynamicDto> ticketFlowNodeDataDtoList = dynamicDto.getTicketFlowDataDynamicDto().getTicketFlowNodeDataDynamicDtoList();
        ticketFlowNodeDataDtoList.sort(Comparator.comparing(TicketFlowNodeDataDynamicDto::getOrder));
        String preNodeId = "-1";
        for (var item : ticketFlowNodeDataDtoList) {
            TicketFlowNodeData ticketFlowNodeData = new TicketFlowNodeData();
            ticketFlowNodeData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA));
            if (StringUtils.isEmpty(item.getName())) {
                ticketFlowNodeData.setNodeName("审批节点");
            } else {
                ticketFlowNodeData.setNodeName(item.getName());
            }
            ticketFlowNodeData.setPreNodeId(preNodeId);//动态数据 templateId为-1
            ticketFlowNodeData.setTemplateId("-1");//动态数据 templateId为-1
            ticketFlowNodeData.setTicketDataId(updateTicketData.getId());
            ticketFlowNodeData.setTicketFlowDataId(newTicketFlowData.getId());
            ticketFlowNodeData.setAuditedMethod(item.getAuditedMethod());
            ticketFlowNodeData.setAuditedType(item.getAuditedType());
            ticketFlowNodeData.setNodeStatus(NodeStatusEnum.APPROVE_INIT);
            ticketFlowNodeData.setNodeOrder(item.getOrder());
            ticketFlowNodeData.setCreateBy("sys");
            ticketFlowNodeData.setUpdateBy("sys");
            ticketFlowNodeData.setCreateTime(new Date());
            ticketFlowNodeData.setUpdateTime(new Date());
            preNodeId = ticketFlowNodeData.getId();
            newTicketFlowNodeDataList.add(ticketFlowNodeData);
            //填充TicketFlowNodeExecutorData
            List<TicketFlowNodeExcutorDynamicDto> ticketFlowNodeExecutorDynamicDtos = item.getExcutorDtoList();
            for (var executor : ticketFlowNodeExecutorDynamicDtos) {
                TicketFlowNodeExecutorData ticketFlowNodeExecutorData = new TicketFlowNodeExecutorData();
                ticketFlowNodeExecutorData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA));
                ticketFlowNodeExecutorData.setTemplateId("-1");
                ticketFlowNodeExecutorData.setTicketDataId(updateTicketData.getId());
                ticketFlowNodeExecutorData.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
                ticketFlowNodeExecutorData.setExecutorType(executor.getExecutorType());
                ticketFlowNodeExecutorData.setExecutorValue(executor.getExecutorValue());
                ticketFlowNodeExecutorData.setExecutorList(ticketFlowNodeExecutorData.ToUserList(executor.getExecutorType(), executor.getExecutorValue()));
                ticketFlowNodeExecutorData.setExecutorDoneList("");
                ticketFlowNodeExecutorData.setCreateBy("sys");
                ticketFlowNodeExecutorData.setUpdateBy("sys");
                ticketFlowNodeExecutorData.setCreateTime(new Date());
                ticketFlowNodeExecutorData.setUpdateTime(new Date());
                newTicketFlowNodeExecutorDataList.add(ticketFlowNodeExecutorData);
                if (ticketFlowNodeData.getPreNodeId().equals("-1")) {//TicketCurrent数据渲染
                    if (ticketFlowNodeExecutorData.getExecutorList() != null) {
                        switch (ticketFlowNodeExecutorData.getExecutorType()) {
                            case APPLY_MEMBER_LIST:
                            case APPLY_GROUP:
                            case APPLY_LEADER:
                            case APPLY_SELF:
                            case APPLY_DEPT_MANAGERS:
                            case APPLY_EXTERNAL_APPROVER:
                                List<AccountInfo> existDealUsers = AccountInfo.ToAccountInfoList(updateTicketData.getCurrentDealUsers());
                                existDealUsers = existDealUsers == null ? new ArrayList<>() : existDealUsers;
                                List<AccountInfo> dealUsers = AccountInfo.ToAccountInfoList(ticketFlowNodeExecutorData.getExecutorList());
                                if (CollectionUtils.isNotEmpty(dealUsers)) {
                                    existDealUsers.addAll(dealUsers);
                                    updateTicketData.setCurrentDealUsers(AccountInfo.ToAccountInfoListStr(existDealUsers));
                                }
                            default:
                        }
                    }
                    updateTicketData.setCurrentNodeId(ticketFlowNodeData.getId());
                    updateTicketData.setCurrentNodeName(ticketFlowNodeData.getNodeName());
                }
            }
        }
    }

    public static Response<TicketDataDto> ToTicketDataDto(
            TicketApp ticketAppDbo,
            TicketData ticketData,
            TicketFormData ticketFormData,
            List<TicketFormItemData> ticketFormItemDataList,
            TicketFlowData ticketFlowData,
            List<TicketFlowNodeData> ticketFlowNodeDataList,
            List<TicketFlowNodeActionData> ticketFlowNodeActionDataList,
            List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList,
            List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailDboList,
            ReqParam reqParam
    )
    {

        if (ticketData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据不存在");
        }
        TicketDataDto ticketDataDto = new TicketDataDto(ticketData);
        ticketDataDto.setAppName(ticketAppDbo.getAppName());
//        try {
//            String accountUserInfoForSearch = SecurityUtils.getOriginUserInfoForSearch();
//            String currentDealUsers = ticketDataDto.getCurrentDealUsers();
//            if (StrUtil.isNotBlank(currentDealUsers) && StrUtil.isNotBlank(accountUserInfoForSearch)) {
//              ticketDataDto.setHasApprovalAuth(currentDealUsers.contains(accountUserInfoForSearch));
//            }
//        } catch (Exception e) {
//            log.warn("用户未登录，后端无法判断是否有审批权限" + e.getMessage());
//        }
        if (ticketFormData != null) {
            TicketFormDataDto ticketFormDataDto = new TicketFormDataDto(ticketFormData);
            if (CollectionUtils.isNotEmpty(ticketFormItemDataList)) {
                List<TicketFormItemDataDto> ticketFormItemDataDtoList = new ArrayList<>();
                for (var item : ticketFormItemDataList) {
                    TicketFormItemDataDto ticketFormItemDataDto = new TicketFormItemDataDto(item);
                    ticketFormItemDataDtoList.add(ticketFormItemDataDto);
                }
                ticketFormDataDto.setTicketFormItemDataDtoList(ticketFormItemDataDtoList);
            }
            ticketDataDto.setTicketFormDataDto(ticketFormDataDto);
        }
        if (ticketFlowData != null) {
            TicketFlowDataDto ticketFlowDataDto = new TicketFlowDataDto(ticketFlowData);
            if (CollectionUtils.isNotEmpty(ticketFlowNodeDataList)) {
                Map<String, List<TicketFlowNodeExecutorDataDto>> executorMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(ticketFlowNodeExecutorDataList)) {
                    for (var executor : ticketFlowNodeExecutorDataList) {
                        if (executorMap.containsKey(executor.getTicketFlowNodeDataId())) {
                            executorMap.get(executor.getTicketFlowNodeDataId()).add(new TicketFlowNodeExecutorDataDto(executor));
                        } else {
                            List<TicketFlowNodeExecutorDataDto> executorList = new ArrayList<>();
                            executorList.add(new TicketFlowNodeExecutorDataDto(executor));
                            executorMap.put(executor.getTicketFlowNodeDataId(), executorList);
                        }
                    }
                }
                Map<String, List<TicketFlowNodeActionDataDto>> actionMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(ticketFlowNodeActionDataList)) {
                    for (var action : ticketFlowNodeActionDataList) {
                        if (actionMap.containsKey(action.getTicketFlowNodeDataId())) {
                            actionMap.get(action.getTicketFlowNodeDataId()).add(new TicketFlowNodeActionDataDto(action));
                        } else {
                            List<TicketFlowNodeActionDataDto> actionList = new ArrayList<>();
                            actionList.add(new TicketFlowNodeActionDataDto(action));
                            actionMap.put(action.getTicketFlowNodeDataId(), actionList);
                        }
                    }
                }
                Map<String, List<TicketFlowNodeApproveDetailDto>> approveDetailMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(ticketFlowNodeApproveDetailDboList)) {
                    for (var approveDetail : ticketFlowNodeApproveDetailDboList) {
                        //公网环境附件地址转换：
                        if (reqParam != null && "YES".equals(reqParam.getPublicNet())) {
                            resetTicketDataDealFileToPublicNet(approveDetail);
                        }

                        if (approveDetailMap.containsKey(approveDetail.getTicketFlowNodeDataId())) {
                            approveDetailMap.get(approveDetail.getTicketFlowNodeDataId()).add(new TicketFlowNodeApproveDetailDto(approveDetail));
                        } else {
                            List<TicketFlowNodeApproveDetailDto> approveDetailList = new ArrayList<>();
                            approveDetailList.add(new TicketFlowNodeApproveDetailDto(approveDetail));
                            approveDetailMap.put(approveDetail.getTicketFlowNodeDataId(), approveDetailList);
                        }
                    }
                }
                List<TicketFlowNodeDataDto> ticketFlowNodeDataDtoList = new ArrayList<>();
                for (TicketFlowNodeData item : ticketFlowNodeDataList) {
                    TicketFlowNodeDataDto ticketFlowNodeDataDto = new TicketFlowNodeDataDto(item);
                    ticketFlowNodeDataDto.setActionList(actionMap.get(item.getId()));
                    ticketFlowNodeDataDto.setExcutorList(executorMap.get(item.getId()));
                    ticketFlowNodeDataDto.setApproveDetailList(approveDetailMap.get(item.getId()));
                    ticketFlowNodeDataDtoList.add(ticketFlowNodeDataDto);
                }
                ticketFlowDataDto.setTicketFlowNodeDataDtoList(ticketFlowNodeDataDtoList);
            }
            ticketDataDto.setTicketFlowDataDto(ticketFlowDataDto);
        }
        return new Response<>(ticketDataDto, BizResponseEnums.SUCCESS, "成功");
    }


    private static void resetTicketDataDealFileToPublicNet(TicketFlowNodeApproveDetail ticketFlowNodeApproveDetail){

        try {
            String dealOpinion = ticketFlowNodeApproveDetail.getDealOpinion();
            if (!JSONUtil.isTypeJSON(dealOpinion)) {
                //处理结果不是json对象，直接返回
                return;
            }
            cn.hutool.json.JSONObject dealOptionObject = JSONUtil.parseObj(dealOpinion);
            String fileJsonKey = ticketFlowNodeApproveDetail.getDealType().getCode().toLowerCase() + "FileInfo";
            if (dealOptionObject.containsKey(fileJsonKey)) {
                cn.hutool.json.JSONArray fileInfoList = dealOptionObject.getJSONArray(fileJsonKey);
                cn.hutool.json.JSONArray newFileInfoList = JSONUtil.createArray();
                for (Object fileInfo : fileInfoList) {
                    cn.hutool.json.JSONObject fileInfoObject = JSONUtil.parseObj(fileInfo);
                    String fileUrl = fileInfoObject.getStr("url", "");
                    if (StrUtil.isBlank(fileUrl)) {
                        newFileInfoList.add(fileInfoObject);
                        continue;

                    }
                    fileUrl = UrlSignUtil.urlSignBuilder().url(fileUrl).isPublic(true).getSignUrl();
                    fileInfoObject.set("url", fileUrl);
                    newFileInfoList.add(fileInfoObject);
                }

                dealOptionObject.set(fileJsonKey, newFileInfoList.toString());
                ticketFlowNodeApproveDetail.setDealOpinion(dealOptionObject.toString());
            }
        } catch (Exception e) {
            log.error("附件地址转换失败,源内容：{},原因：{}", ticketFlowNodeApproveDetail.getDealOpinion(), e.getMessage());
        }
    }


    public static Response<String> CreateTicketParamCheck(
            String applyId,
            TicketData updateTicketData, TicketTemplateAllBO ticketTemplateAllBO
    )
    {

        if (updateTicketData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单申请编号ID:%s ，数据不存在", applyId));
        }
        if (updateTicketData.getTicketStatus() != TicketDataStatusEnum.INIT && updateTicketData.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单申请编号ID:%s ，状态:%s不正确", applyId, updateTicketData.getTicketStatus().toString()));
        }
        if (ticketTemplateAllBO == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "总模板不存在");
        }
        if (ticketTemplateAllBO.getTicketTemplate() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模板不存在");
        }
        if (!TicketTemplateStatusEnum.ENABLE.equals(ticketTemplateAllBO.getTicketTemplate().getTicketStatus())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单模版未启用");
        }
        if (ticketTemplateAllBO.getTicketFlowTemplate() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程模板不存在");
        }
        if (CollectionUtils.isEmpty(ticketTemplateAllBO.getTicketFlowNodeTemplateList())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程节点模板不存在");
        }
        if (ticketTemplateAllBO.getTicketFormTemplate() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单模板不存在");
        }
        if (CollectionUtils.isEmpty(ticketTemplateAllBO.getTicketFormItemTemplateList())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单组件不存在");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response<List<TicketFlowNodeActionData>> buildTicketFlowNodeActionDataList(
            String ticketDataId,
            String ticketFlowDataId,
            List<TicketFlowNodeActionTemplate> actionTemplateList,
            List<TicketFlowNodeData> ticketFlowNodeDataList
    )
    {

        if (StringUtils.isAnyEmpty(ticketDataId, ticketFlowDataId)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失1");
        }
        if (CollectionUtils.isEmpty(actionTemplateList)) {
            return new Response<>(null, BizResponseEnums.SUCCESS, "模版为空");
        }
        Map<String, TicketFlowNodeData> ticketNodeTemplateIDData = new HashMap<>();
        if (CollectionUtils.isNotEmpty(ticketFlowNodeDataList)) {
            for (var item : ticketFlowNodeDataList) {
                ticketNodeTemplateIDData.putIfAbsent(item.getTemplateId(), item);
            }
        }
        List<TicketFlowNodeActionData> actionDataList = new ArrayList<>();
        for (var actionTemplate : actionTemplateList) {
            //全量动作里面，部分节点通过条件可能已经没有节点了
            if (ticketNodeTemplateIDData.get(actionTemplate.getTicketFlowNodeTemplateId()) != null) {
                actionDataList.add(new TicketFlowNodeActionData(
                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_ACTION_DATA),
                                ticketDataId,
                                actionTemplate,
                                ticketNodeTemplateIDData.get(actionTemplate.getTicketFlowNodeTemplateId())
                        )
                );
            }
        }
        return new Response<>(actionDataList, BizResponseEnums.SUCCESS, "成功");
    }

    public static Response<List<TicketFlowNodeData>> buildTicketFlowNodeDataList(
            String ticketDataId,
            String ticketFlowDataId,
            List<TicketFormItemStdDto> formItems,
            List<TicketFormItemTemplate> formItemTemplateList,
            List<TicketFlowNodeTemplate> flowNodeTemplateList,
            List<TicketFlowNodeRuleTemplate> flowNodeRuleTemplateList,
            List<TicketFlowNodeExecutorTemplate> flowNodeExecutorTemplateList
    )
    {

        Map<String, TicketFormItemTemplate> formItemTemplateCodeMap = new HashMap<>();
        Map<String, TicketFormItemTemplate> formItemTemplateIDMap = new HashMap<>();
        Map<String, TicketFormItemTemplate> formItemTemplateNameMap = new HashMap<>();
        for (var item : formItemTemplateList) {
            formItemTemplateIDMap.put(item.getId(), item);
            formItemTemplateNameMap.put(item.getItemLabel(), item);
            if (StringUtils.isNotEmpty(item.getItemConfig())) {
                String itemCode = JSONObject.parseObject(item.getItemConfig()).getString("itemCode");
                if (StringUtils.isNotEmpty(itemCode)) {
                    formItemTemplateCodeMap.put(itemCode, item);
                }
            }
        }
        Map<String, TicketFormItemStdDto> formItemMap = new HashMap<>();
        for (var item : formItems) {
            if (formItemTemplateIDMap.containsKey(item.getTemplateId())) {
                formItemMap.put(formItemTemplateIDMap.get(item.getTemplateId()).getId(), item);
                continue;
            }
            if (formItemTemplateCodeMap.containsKey(item.getTemplateId())) {
                formItemMap.put(formItemTemplateCodeMap.get(item.getTemplateId()).getId(), item);
                continue;
            }
            if (formItemTemplateNameMap.containsKey(item.getTemplateId())) {
                formItemMap.put(formItemTemplateNameMap.get(item.getTemplateId()).getId(), item);
                continue;
            }
        }
        // Map<节点ID,节点模板>
        Map<String, TicketFlowNodeTemplate> flowNodeTemplateMap = new HashMap<>();
        // Map<前置节点ID,List<节点>>
        Map<String, List<TicketFlowNodeTemplate>> flowPreNodeTemplateMapList = new HashMap<>();
        if (CollectionUtils.isNotEmpty(flowNodeTemplateList)) {
            for (var item : flowNodeTemplateList) {
                flowNodeTemplateMap.put(item.getId(), item);
                String[] preNodeIds = item.getPreNodeId().split(",");
                for (var preNodeId : preNodeIds) {
                    if (!StringUtils.isEmpty(preNodeId)) {
                        if (flowPreNodeTemplateMapList.containsKey(preNodeId)) {
                            flowPreNodeTemplateMapList.get(preNodeId).add(item);
                        } else {
                            List<TicketFlowNodeTemplate> list = new ArrayList<>();
                            list.add(item);
                            flowPreNodeTemplateMapList.put(preNodeId, list);
                        }
                    }
                }
            }
        }
        //Map<节点ID,规则>
        Map<String, TicketFlowNodeRuleTemplate> flowNodeRuleTemplateMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(flowNodeRuleTemplateList)) {
            for (var item : flowNodeRuleTemplateList) {
                flowNodeRuleTemplateMap.put(item.getTicketFlowNodeTemplateId(), item);
            }
        }
        List<TicketFlowNodeTemplate> nextNodes = flowPreNodeTemplateMapList.get("-1");
        if (CollectionUtils.isEmpty(nextNodes)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程头节点模板不存在");
        }
        List<TicketFlowNodeData> result = new ArrayList<>();
        int maxCount = 1000;
        while (true) {
            maxCount--;
            if (maxCount <= 0) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程节点模板循环引用");
            }
            if (CollectionUtils.isEmpty(nextNodes)) {
                break;
            }
            nextNodes.sort(Comparator.comparing(TicketFlowNodeTemplate::getNodeOrder));
            boolean isAddNode = false;
            for (var node : nextNodes) {
                if (isAddNode) {
                    break;
                } else {
                    isAddNode = false;
                }
                if (!flowNodeRuleTemplateMap.containsKey(node.getId())) {
                    TicketFlowNodeData newDataNode = new TicketFlowNodeData(
                            node,
                            SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA),
                            result.isEmpty() ? "-1" : result.get(result.size() - 1).getId(),
                            ticketDataId,
                            ticketFlowDataId);
                    result.add(newDataNode);
                    nextNodes = flowPreNodeTemplateMapList.get(node.getId());
                    break;
                }
                TicketFlowNodeRuleTemplate ruleTemplate = flowNodeRuleTemplateMap.get(node.getId());
                var accountRsp = CompareInfo.getTwoDList(ruleTemplate.getRuleInfoList());
                if (!BizResponseEnums.SUCCESS.getCode().equals(accountRsp.getCode())) {
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单流程节点规则解析失败");
                }
                List<List<CompareInfo>> compareList = accountRsp.getData();
                String templateCompareValueStr = "";
                for (var compareInfos : compareList) {
                    var compareResult = true;
                    for (var templateCompareInfo : compareInfos) {
                        if (compareResult == false) {
                            break;
                        }
                        Object templateCompareValue = templateCompareInfo.getCompareValue();
                        String compareID = templateCompareInfo.getCompareId();
                        var beCompareInfo = formItemMap.get(compareID);
                        if (beCompareInfo == null) {
                            compareResult = false;
                            break;
                        }
                        String inputCompareValue = beCompareInfo.getValue();
                        if (inputCompareValue == null) {
                            inputCompareValue = "";
                        }
                        switch (templateCompareInfo.getCompareType()) {
                            case INPUT_CONTAIN_ANY:
                                String inputContainPartStr = String.valueOf(templateCompareValue);
                                boolean partContains = false;
                                if (StringUtils.isNotEmpty(inputContainPartStr)) {
                                    String[] inputContainPartArr = inputContainPartStr.split(",");
                                    for (var inputContainPart : inputContainPartArr) {
                                        if (StringUtils.isNotEmpty(inputContainPart) && inputCompareValue.contains(inputContainPart)) {
                                            partContains = true;
                                            break;
                                        }
                                    }
                                }
                                compareResult = compareResult & partContains;
                                break;
                            case INPUT_CONTAIN:
                                compareResult = compareResult & inputCompareValue.contains(String.valueOf(templateCompareValue));
                                break;
                            case INPUT_NOT_CONTAIN:
                                compareResult = compareResult & !inputCompareValue.contains(String.valueOf(templateCompareValue));
                                break;
                            case EQUAL:
                                compareResult = compareResult & Objects.equals(inputCompareValue, templateCompareValue.toString());
                                break;
                            case NOT_EQUAL:
                                compareResult = compareResult & !Objects.equals(inputCompareValue, templateCompareValue.toString());
                                break;
                            case GREATER:
                                try {
                                    Double longBeCompareValue = Double.valueOf(inputCompareValue);
                                    Double compareValueLong = Double.valueOf(String.valueOf(templateCompareValue));
                                    compareResult = compareResult & (longBeCompareValue > compareValueLong);
                                } catch (Exception e) {
                                    compareResult = false;
                                }
                                break;
                            case GREATER_EQUAL:
                                try {
                                    Double longBeCompareValue = Double.valueOf(inputCompareValue);
                                    Double compareValueLong = Double.valueOf(String.valueOf(templateCompareValue));
                                    compareResult = compareResult & (longBeCompareValue >= compareValueLong);
                                } catch (Exception e) {
                                    compareResult = false;
                                }
                                break;
                            case LESS:
                                try {
                                    Double longBeCompareValue = Double.valueOf(inputCompareValue);
                                    Double compareValueLong = Double.valueOf(String.valueOf(templateCompareValue));
                                    compareResult = compareResult & (longBeCompareValue < compareValueLong);
                                } catch (Exception e) {
                                    compareResult = false;
                                }
                                break;
                            case LESS_EQUAL:
                                try {
                                    Double longBeCompareValue = Double.valueOf(inputCompareValue);
                                    Double compareValueLong = Double.valueOf(String.valueOf(templateCompareValue));
                                    compareResult = compareResult & (longBeCompareValue <= compareValueLong);
                                } catch (Exception e) {
                                    compareResult = false;
                                }
                                break;
                            case CHOOSED:
                                compareResult = compareResult & (Objects.equals(inputCompareValue, templateCompareValue));
                                break;
                            case NO_CHOOSED:
                                compareResult = compareResult & (!Objects.equals(inputCompareValue, templateCompareValue));
                                break;
                            case CASCADER_CHOOSED:
                                if (null != templateCompareValue) {
                                    templateCompareValueStr = BusiTicketDataUtil.objToString(templateCompareValue);
                                }
                                compareResult = compareResult & (Objects.equals(inputCompareValue, templateCompareValueStr));
                                break;
                            case NO_CASCADER_CHOOSED:
                                if (null != templateCompareValue) {
                                    templateCompareValueStr = BusiTicketDataUtil.objToString(templateCompareValue);
                                }
                                compareResult = compareResult & (!Objects.equals(inputCompareValue, templateCompareValueStr));
                                break;
                            case CONTAIN_ALL://包含所有：输入值1,2,3-比较值1,2
                                List<String> inputCompareValueList = JSONArray.parseArray(inputCompareValue, String.class);
                                List<String> templateCompareValueList = (ArrayList) templateCompareValue;
                                compareResult = compareResult & (inputCompareValueList.containsAll(templateCompareValueList));
                                break;
                            case CONTAIN_ANY://包含其一：输入值1,3-比较值1,2
                                inputCompareValueList = JSONArray.parseArray(inputCompareValue, String.class);
                                templateCompareValueList = (ArrayList) templateCompareValue;
                                compareResult = compareResult & (inputCompareValueList.stream().anyMatch(templateCompareValueList::contains));
                                break;
                            case INCLUDE_ALL://被包含所有：输入值1-比较值1,2
                                inputCompareValueList = JSONArray.parseArray(inputCompareValue, String.class);
                                if (CollectionUtils.isEmpty(inputCompareValueList)) {
                                    compareResult = false;
                                    break;
                                }
                                templateCompareValueList = (ArrayList) templateCompareValue;
                                compareResult = compareResult & (templateCompareValueList.containsAll(inputCompareValueList));
                                break;
                            case INCLUDE_ANY://被包含其一：输入值1,3-比较值1,2
                                inputCompareValueList = JSONArray.parseArray(inputCompareValue, String.class);
                                if (CollectionUtils.isEmpty(inputCompareValueList)) {
                                    compareResult = false;
                                    break;
                                }
                                templateCompareValueList = (ArrayList) templateCompareValue;
                                compareResult = compareResult & (templateCompareValueList.stream().anyMatch(inputCompareValueList::contains));
                                break;
                            default:
                                throw new NotImplementedException(String.format("未实现的比较类型:%s", templateCompareInfo.getCompareType().toString()));
                        }
                    }
                    if (compareResult) {
                        TicketFlowNodeData newDataNode = new TicketFlowNodeData(
                                node,
                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA),
                                result.isEmpty() ? "-1" : result.get(result.size() - 1).getId(),
                                ticketDataId,
                                ticketFlowDataId);
                        result.add(newDataNode);
                        nextNodes = flowPreNodeTemplateMapList.get(node.getId());
                        isAddNode = true;
                        break;
                    }
                }
                if (isAddNode == false && nextNodes.indexOf(node) == nextNodes.size() - 1) {
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "所有节点规则均不满足要求，最后节点应为默认节点");
                }
            }
        }
        return new Response<>(result, BizResponseEnums.SUCCESS, "成功");
    }


    public static Response<List<TicketFlowEventData>> buildTicketFlowNodeEventDataList(
            String ticketDataId,
            String currentNodeID,
            List<TicketFlowNodeData> ticketFlowNodeDataList,
            List<TicketFlowEventTemplate> ticketFlowEventTemplateList
    )
    {
        //Map<节点ID,List<事件>>
        Map<String, List<TicketFlowEventTemplate>> flowEventTemplateMap = new HashMap<>();
        for (var event : ticketFlowEventTemplateList) {
            if (flowEventTemplateMap.containsKey(event.getTicketFlowNodeTemplateId())) {
                flowEventTemplateMap.get(event.getTicketFlowNodeTemplateId()).add(event);
            } else {
                List<TicketFlowEventTemplate> list = new ArrayList<>();
                list.add(event);
                flowEventTemplateMap.put(event.getTicketFlowNodeTemplateId(), list);
            }
        }
        List<TicketFlowEventData> ticketFlowEventDataList = new ArrayList<>();
        for (var node : ticketFlowNodeDataList) {
            List<TicketFlowEventTemplate> eventList = flowEventTemplateMap.get(node.getTemplateId());
            if (CollectionUtils.isNotEmpty(eventList)) {
                for (var event : eventList) {
                    ticketFlowEventDataList.add(new TicketFlowEventData(
                            event,
                            SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_EVENT_DATA),
                            ticketDataId,
                            Objects.equals(currentNodeID, node.getId()) ? EventStatusEnum.WAIT_EXECUTE : EventStatusEnum.INIT,
                            node.getId()
                    ));
                }
            }
        }
        return new Response<>(ticketFlowEventDataList, BizResponseEnums.SUCCESS, "成功");
    }


    public static Response<List<TicketFormItemData>> buildTicketFormItemDataList(
            String ticketDataId,
            TicketFormData ticketFormData,
            List<TicketFormItemTemplate> formItemTemplateList,
            List<TicketFormItemStdDto> formItems
    )
    {

        Map<String, String> codeIdNameToPanelIDMap = new HashMap<>();
        Map<String, TicketFormItemTemplate> formItemTemplateCodeMap = new HashMap<>();
        Map<String, TicketFormItemTemplate> formItemTemplateIDMap = new HashMap<>();
        Map<String, TicketFormItemTemplate> formItemTemplateNameMap = new HashMap<>();
        for (var item : formItemTemplateList) {
            formItemTemplateIDMap.putIfAbsent(item.getId(), item);
            formItemTemplateNameMap.putIfAbsent(item.getItemLabel(), item);
            if (StringUtils.isNotEmpty(item.getItemConfig())) {
                String itemCode = (String) JSONObject.parseObject(item.getItemConfig()).get("itemCode");
                if (StringUtils.isNotEmpty(itemCode)) {
                    formItemTemplateCodeMap.putIfAbsent(itemCode, item);
                }
            }
        }

        //暂缓
        List<TicketFormItemStdDto> formItemStdDtos = addItemsWithDefaultValue(formItems, formItemTemplateIDMap, formItemTemplateCodeMap, formItemTemplateNameMap);

        List<TicketFormItemData> ticketFormItemDataList = new ArrayList<>();
        int temItemOrder = 10000;
        for (var item : formItemStdDtos) {
            if (StringUtils.isEmpty(item.getTemplateId())) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "表单项模版ID不能为空");
            }
            TicketFormItemTemplate templateInfo = formItemTemplateIDMap.get(item.getTemplateId());
            if (templateInfo == null) {
                templateInfo = formItemTemplateCodeMap.get(item.getTemplateId());
            }
            if (templateInfo == null) {
                templateInfo = formItemTemplateNameMap.get(item.getTemplateId());
            }
            if (templateInfo != null) {
                String id = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA);
                String itemValue = item.getValue() == null ? "" : item.getValue();
                String itemConfig = templateInfo.getItemConfig();
                JSONObject itemConfigJsonObj = new JSONObject();
                if (StringUtils.isNotEmpty(itemConfig)) {
                    itemConfigJsonObj = JSONObject.parseObject(itemConfig);
                }
                if (itemConfigJsonObj.containsKey("isEncrypted")) {
                    Boolean isEncrypted = (Boolean) itemConfigJsonObj.get("isEncrypted");
                    if (isEncrypted) {
                        try {
                            String ciphertext = AesUtil.encrypt(itemValue);
                            itemConfigJsonObj.put("ciphertext", ciphertext);
                            templateInfo.setItemConfig(JSONUtil.toJsonStr(itemConfigJsonObj));
                        } catch (Exception e) {
                            String errorTips = String.format("表单项(id为{})的明文(%s)加密失败", id, itemValue);
                            log.error(errorTips, e);
                            return Response.error(BizResponseEnums.DES_ERROR, errorTips);
                        }
                        itemValue = com.smy.tfs.common.utils.StringUtils.toMaskString(itemValue);
                    }
                }
//                if(hsItemConfig != null && !hsItemConfig.isEmpty()){
//                    for (Map.Entry<String, String> entry : hsItemConfig.entrySet()) {
//                        itemConfigJsonObj.put(entry.getKey(), entry.getValue());
//                    }
//                }
                if (StringUtils.isNotEmpty(item.getDisplayValue())) {
                    itemConfigJsonObj.put("displayValue", item.getDisplayValue());
                }
                if (StringUtils.isNotEmpty(item.getDisplayAble())) {
                    itemConfigJsonObj.put("displayAble", item.getDisplayAble());
                }
                templateInfo.setItemConfig(JSONObject.toJSONString(itemConfigJsonObj));
                ticketFormItemDataList.add(new TicketFormItemData(
                        templateInfo,
                        itemValue,
                        id,
                        "",
                        ticketDataId,
                        ticketFormData.getId()
                ));
                if (templateInfo.getItemType() == FormItemTypeEnum.GROUP) {
                    JSONArray cardArr = JSONArray.parseArray(item.getValue());
                    if (!cardArr.isEmpty()) {
                        for (int i = 0; i < cardArr.size(); i++) {
                            Map<String, Object> map = (Map<String, Object>) cardArr.get(i);
                            if (!map.isEmpty()) {
                                for (String key : map.keySet()) {
                                    TicketFormItemTemplate templateInfoInner = formItemTemplateIDMap.get(key);
                                    if (templateInfoInner == null) {
                                        templateInfoInner = formItemTemplateCodeMap.get(key);
                                    }
                                    if (templateInfoInner == null) {
                                        templateInfoInner = formItemTemplateNameMap.get(key);
                                    }
                                    if (templateInfoInner != null) {
                                        ticketFormItemDataList.add(new TicketFormItemData(
                                                templateInfoInner,
                                                map.get(key).toString(),
                                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA),
                                                id + ":" + i,
                                                ticketDataId,
                                                ticketFormData.getId()
                                        ));
                                    } else {
                                        TicketFormItemData innerItemData = new TicketFormItemData(
                                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA),
                                                ticketDataId,
                                                ticketFormData.getId(),
                                                temItemOrder,
                                                FormItemTypeEnum.INPUT,
                                                key,
                                                map.get(key).toString()
                                        );
                                        temItemOrder++;
                                        innerItemData.setItemParentId(id + ":" + i);
                                        ticketFormItemDataList.add(innerItemData);
                                    }
                                }
                            }
                        }
                    }
                } else if (templateInfo.getItemType() == FormItemTypeEnum.PANEL && StringUtils.isNotEmpty(item.getValue())) {
                    List<String> fieldList = JSONUtil.parseArray(item.getValue()).toList(String.class);
                    for (String field : fieldList) {
                        if (StringUtils.isNotEmpty(field)) {
                            codeIdNameToPanelIDMap.putIfAbsent(field, id);
                        }
                    }
                }
            } else {
                //判断item.getTemplateId() 为全部数字
                if (item.getTemplateId().matches("[0-9]+")) {
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应模版ID:%s，组件模版不存在", item.getTemplateId()));
                }
                String itemId = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA);
                TicketFormItemData itemData = new TicketFormItemData(
                        itemId,
                        ticketDataId,
                        ticketFormData.getId(),
                        temItemOrder,
                        FormItemTypeEnum.getEnumByCodeOrDefault(item.getType(), FormItemTypeEnum.INPUT),
                        item.getTemplateId(),
                        item.getValue()
                );
                temItemOrder++;
                JSONObject itemConfigJsonObj = new JSONObject();
//                if(hsItemConfig != null && !hsItemConfig.isEmpty()){
//                    for (Map.Entry<String, String> entry : hsItemConfig.entrySet()) {
//                        itemConfigJsonObj.put(entry.getKey(), entry.getValue());
//                    }
//                }
                if (StringUtils.isNotEmpty(item.getDisplayValue())) {
                    itemConfigJsonObj.put("displayValue", item.getDisplayValue());
                }
                if (StringUtils.isNotEmpty(item.getDisplayAble())) {
                    itemConfigJsonObj.put("displayAble", item.getDisplayAble());
                }
                itemData.setItemConfig("{}");
                itemData.setItemConfigExt(JSONObject.toJSONString(itemConfigJsonObj));
                ticketFormItemDataList.add(itemData);
                if (FormItemTypeEnum.GROUP == itemData.getItemType()) {
                    JSONArray cardArr = JSONArray.parseArray(item.getValue());
                    if (!cardArr.isEmpty()) {
                        for (int i = 0; i < cardArr.size(); i++) {
                            Map<String, Object> map = (Map<String, Object>) cardArr.get(i);
                            if (!map.isEmpty()) {
                                for (String key : map.keySet()) {
                                    TicketFormItemTemplate templateInfoInner = formItemTemplateIDMap.get(key);
                                    if (templateInfoInner == null) {
                                        templateInfoInner = formItemTemplateCodeMap.get(key);
                                    }
                                    if (templateInfoInner == null) {
                                        templateInfoInner = formItemTemplateNameMap.get(key);
                                    }
                                    if (templateInfoInner != null) {
                                        ticketFormItemDataList.add(new TicketFormItemData(
                                                templateInfoInner,
                                                map.get(key).toString(),
                                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA),
                                                itemId + ":" + i,
                                                ticketDataId,
                                                ticketFormData.getId()
                                        ));
                                    } else {
                                        //待处理
                                        TicketFormItemData innerItemData = new TicketFormItemData(
                                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_ITEM_DATA),
                                                ticketDataId,
                                                ticketFormData.getId(),
                                                temItemOrder,
                                                FormItemTypeEnum.INPUT,
                                                key,
                                                map.get(key).toString()
                                        );
                                        temItemOrder++;
                                        innerItemData.setItemParentId(itemId + ":" + i);
                                        ticketFormItemDataList.add(innerItemData);
                                    }
                                }
                            }
                        }
                    }
                } else if (FormItemTypeEnum.PANEL == itemData.getItemType() && StringUtils.isNotEmpty(itemData.getItemValue())) {
                    List<String> fieldList = JSONUtil.parseArray(item.getValue()).toList(String.class);
                    for (String field : fieldList) {
                        if (StringUtils.isNotEmpty(field)) {
                            codeIdNameToPanelIDMap.putIfAbsent(field, itemData.getId());
                        }
                    }
                }
                temItemOrder++;
            }
        }
        if (ticketFormItemDataList.size() > 0 && codeIdNameToPanelIDMap.size() > 0) {
            for (TicketFormItemData itemData : ticketFormItemDataList) {
                if (StringUtils.isEmpty(itemData.getItemParentId())) {
                    if (codeIdNameToPanelIDMap.containsKey(itemData.getTemplateId())) {
                        itemData.setItemParentId(codeIdNameToPanelIDMap.get(itemData.getTemplateId()));
                        continue;
                    }
                    if (codeIdNameToPanelIDMap.containsKey(itemData.getItemLabel())) {
                        itemData.setItemParentId(codeIdNameToPanelIDMap.get(itemData.getItemLabel()));
                        continue;
                    }
                    String itemConfig = itemData.getItemConfigExt();
                    itemData.EqConfig();
                    if (StringUtils.isNotEmpty(itemConfig)) {
                        JSONObject itemJO = JSONObject.parseObject(itemConfig);
                        String itemCode = itemJO.getString("itemCode");
                        if (StringUtils.isNotEmpty(itemCode)) {
                            if (codeIdNameToPanelIDMap.containsKey(itemCode)) {
                                itemData.setItemParentId(codeIdNameToPanelIDMap.get(itemCode));
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return new Response<>(ticketFormItemDataList, BizResponseEnums.SUCCESS, "成功");
    }

    private static List<TicketFormItemStdDto> addItemsWithDefaultValue(List<TicketFormItemStdDto> formItems,
                                                                       Map<String, TicketFormItemTemplate> formItemTemplateIDMap,
                                                                       Map<String, TicketFormItemTemplate> formItemTemplateCodeMap,
                                                                       Map<String, TicketFormItemTemplate> formItemTemplateNameMap)
    {

        List<TicketFormItemStdDto> resFormItems = new ArrayList<>(formItems);

        if (CollUtil.isEmpty(formItemTemplateIDMap) &&
                CollUtil.isEmpty(formItemTemplateCodeMap) &&
                CollUtil.isEmpty(formItemTemplateNameMap))
        {
            return resFormItems;
        }
        try {
            // 合并两个Map的key集合，排除已存在的templateId
            Set<String> allTemplateKeys = new HashSet<>();

            Optional.ofNullable(formItemTemplateIDMap).ifPresent(map -> allTemplateKeys.addAll(map.keySet()));
            Optional.ofNullable(formItemTemplateCodeMap).ifPresent(map -> allTemplateKeys.addAll(map.keySet()));
            Optional.ofNullable(formItemTemplateNameMap).ifPresent(map -> allTemplateKeys.addAll(map.keySet()));

            // 获取已存在的模板ID集合
            Set<String> existingTemplateIds = resFormItems.stream()
                    .filter(item -> StrUtil.isNotBlank(item.getTemplateId()) && StrUtil.isNotEmpty(item.getValue()))
                    .map(TicketFormItemStdDto::getTemplateId)
                    .collect(Collectors.toSet());

            resFormItems = resFormItems.stream()
                    .filter(item -> StrUtil.isNotBlank(item.getTemplateId()) &&
                            existingTemplateIds.contains(item.getTemplateId()))
                    .collect(Collectors.toList());

            List<TicketFormItemTemplate> beforeDistinct = allTemplateKeys.stream()
                    .filter(key -> !existingTemplateIds.contains(key))
                    .map(key -> {
                        // 按优先级从三个Map中查找template
                        TicketFormItemTemplate template = formItemTemplateIDMap.get(key);
                        if (template != null) {
                            String templateId = template.getId();
                            if (StrUtil.isNotBlank(templateId) && existingTemplateIds.contains(templateId)) {
                                return null;
                            }
                            return template;
                        }

                        template = formItemTemplateNameMap.get(key);
                        if (template != null) {
                            String templateId = template.getId();
                            String templateLabel = template.getItemLabel();
                            if ((StrUtil.isNotBlank(templateId) && existingTemplateIds.contains(templateId)) ||
                                    (StrUtil.isNotBlank(templateLabel) && existingTemplateIds.contains(templateLabel)))
                            {
                                return null;
                            }
                            return template;
                        }

                        template = formItemTemplateCodeMap.get(key);
                        if (template != null) {

                            String templateId = template.getId();
                            String itemCode = "";
                            if (StrUtil.isNotBlank(template.getItemConfig())) {
                                itemCode = (String) JSONObject.parseObject(template.getItemConfig()).get("itemCode");
                            }
                            if ((StrUtil.isNotBlank(templateId) && existingTemplateIds.contains(templateId)) ||
                                    (StrUtil.isNotBlank(itemCode) && existingTemplateIds.contains(itemCode)))
                            {
                                return null;
                            }
                            return template;
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .filter(template -> template.getItemConfig() != null && hasDefaultValue(template.getItemConfig()))
                    .collect(Collectors.toList());

            List<TicketFormItemTemplate> afterDistinct = new ArrayList<>(beforeDistinct.stream()
                    .collect(Collectors.toMap(
                            TicketFormItemTemplate::getId,  // 根据ticketFormTemplateId作为key
                            template -> template,                              // value就是template本身
                            (existing, replacement) -> existing                // 如果有重复，保留第一个
                    ))
                    .values());

            List<TicketFormItemStdDto> missingFormItems = afterDistinct.stream()
                    .filter(template -> StrUtil.isNotBlank(template.getId()))
                    .map(template -> createDefaultFormItem(template.getId(), template))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            resFormItems.addAll(missingFormItems);
        } catch (Exception e) {
            log.error("addItemsWithDefaultValue formItemsInfo {} ERROR: {}", resFormItems, e.getMessage());
        }
        return resFormItems;
    }

    /**
     * 判断itemConfig中是否含有defaultValue字段
     */
    private static boolean hasDefaultValue(String itemConfig){

        if (StrUtil.isBlank(itemConfig)) {
            return false;
        }
        try {
            JSONObject config = JSON.parseObject(itemConfig);
            if (config == null || !config.containsKey("defaultValue")) {
                return false;
            }

            String defaultValue = config.getString("defaultValue");
            return StrUtil.isNotBlank(defaultValue);
        } catch (Exception e) {
            return false;
        }
    }

    private static TicketFormItemStdDto createDefaultFormItem(
            String templateId,
            TicketFormItemTemplate template)
    {

        TicketFormItemStdDto formItemDto = new TicketFormItemStdDto();
        formItemDto.setTemplateId(templateId);

        // 从itemConfig中获取defaultValue
        if (StrUtil.isNotBlank(template.getItemConfig())) {
            JSONObject itemConfig = JSON.parseObject(template.getItemConfig());
            String defaultValue = itemConfig.getString("defaultValue");
            formItemDto.setValue(defaultValue);

            if (itemConfig.containsKey("displayValue")) {
                formItemDto.setDisplayValue(itemConfig.getString("displayValue"));
            }

            if (itemConfig.containsKey("displayAble")) {
                formItemDto.setDisplayAble(itemConfig.getString("displayAble"));
            }

            return formItemDto;
        }
        return null;
    }

    public static Response<Map<String, String>> getItemIdColMap(List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList){
        //最高版本的模板项<表单项id值,表单项id值对应的列名>映射Map
        Map<String, String> itemIdColMap = new HashMap<>();
        if (ObjectHelper.isNotEmpty(ticketFormItemIdColMappingList)) {
            Integer maxVersion = 1;
            List<Integer> versionList = ticketFormItemIdColMappingList.stream().map(it -> it.getVersion()).collect(Collectors.toList());
            if (ObjectHelper.isNotEmpty(versionList)) {
                maxVersion = Collections.max(versionList);
            }
            for (TicketFormItemIdColMapping t : ticketFormItemIdColMappingList) {
                if (maxVersion.equals(t.getVersion())) {
                    itemIdColMap.put(t.getFormItemId(), t.getFormItemValueCol());
                }
            }
        }
        return new Response().success(itemIdColMap);
    }

    public static Response<List<TicketFormItemAttriDto>> getTicketFormItemAttriDtoList(AdvancedQueryDto advancedQueryDto, Map<String, String> itemIdColMap, Map<String, FormItemTypeEnum> itemIdTypeMap){
        //TicketFormItemIdValueDto为{formItemId,formItemValue,formItemType}
        List<TicketFormItemAttriDto> ticketFormItemAttriDtoList = advancedQueryDto.getFormItemList();
        if (ObjectHelper.isNotEmpty(ticketFormItemAttriDtoList)) {
            ticketFormItemAttriDtoList.stream().forEach(it -> {
                String formItemId = it.getFormItemId();
                String formItemValueCol = itemIdColMap.get(formItemId);
                if (ObjectHelper.isNotEmpty(formItemId) && ObjectHelper.isNotEmpty(formItemValueCol))
                    it.setFormItemId(formItemValueCol);
                FormItemTypeEnum formItemType = itemIdTypeMap.get(formItemId);
                if (ObjectHelper.isNotEmpty(formItemId) && ObjectHelper.isNotEmpty(formItemType))
                    it.setFormItemType(formItemType);
            });
        }
        return new Response().success(ticketFormItemAttriDtoList);
    }

    public static Response<List<TicketFormItemAttriDto>> getTicketFormItemAttriDtoList(List<TicketFormItemAttriDto> ticketFormItemAttriDtoList, Map<String, String> itemIdColMap, Map<String, FormItemTypeEnum> itemIdTypeMap){
        //TicketFormItemIdValueDto为{formItemId,formItemValue,formItemType}
        if (ObjectHelper.isNotEmpty(ticketFormItemAttriDtoList)) {
            ticketFormItemAttriDtoList.stream().forEach(it -> {
                String formItemId = it.getFormItemId();
                String formItemValueCol = itemIdColMap.get(formItemId);
                if (ObjectHelper.isNotEmpty(formItemId) && ObjectHelper.isNotEmpty(formItemValueCol))
                    it.setFormItemId(formItemValueCol);
                FormItemTypeEnum formItemType = itemIdTypeMap.get(formItemId);
                if (ObjectHelper.isNotEmpty(formItemId) && ObjectHelper.isNotEmpty(formItemType))
                    it.setFormItemType(formItemType);
            });
        }
        return new Response().success(ticketFormItemAttriDtoList);
    }

    public static LambdaQueryWrapper<TicketFormItemValues> getTicketFormItemValuesQueryWrapper(AdvancedQueryDto advancedQueryDto, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList){

        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = getTicketFormItemValuesWhereWrapper(advancedQueryDto, ticketFormItemAttriDtoList);
        lambdaQueryWrapper.select(TicketFormItemValues::getTicketDataId,
                TicketFormItemValues::getTicketStatus,
                TicketFormItemValues::getTicketName,
                TicketFormItemValues::getTemplateId,
                TicketFormItemValues::getCurrentDealUsers,
                TicketFormItemValues::getTicketFinishTime,
                TicketFormItemValues::getCreateTime,
                TicketFormItemValues::getUpdateTime,
                TicketFormItemValues::getApplyUser,
                TicketFormItemValues::getWxChatGroupId,
                TicketFormItemValues::getTicketMsgArriveType,
                TicketFormItemValues::getTags
        );
        lambdaQueryWrapper.orderByDesc(TicketFormItemValues::getUpdateTime);
        return lambdaQueryWrapper;
    }

    public static LambdaQueryWrapper<TicketFormItemValues> pageQueryWrapper(PageQueryTicketDataReqDto pageQueryTicketDataReqDt, String user, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList){

        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(TicketFormItemValues::getDeleteTime);
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getTemplateId())) {
            lambdaQueryWrapper.eq(TicketFormItemValues::getTemplateId, pageQueryTicketDataReqDt.getTemplateId());
        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getTicketStatusStrList())) {
            lambdaQueryWrapper.in(TicketFormItemValues::getTicketStatus, pageQueryTicketDataReqDt.getTicketStatusStrList());
        }
        //TODO 暂定在模版中加字段解决
//        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getQueryType())
//                && TicketQueryTypeEnum.CREATED_BY_ME.equals(pageQueryTicketDataReqDt.getQueryType())) {
//            lambdaQueryWrapper.like(TicketFormItemValues::getApplyUser, user);
//        }
//        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getQueryType())
//                && TicketQueryTypeEnum.HANDLED_BY_ME.equals(pageQueryTicketDataReqDt.getQueryType())) {
//            lambdaQueryWrapper.and(LambdaQueryWrapper->{
//                LambdaQueryWrapper.like(TicketFormItemValues::getCurrentDoneUsers, user);
//                LambdaQueryWrapper.or();
//                LambdaQueryWrapper.like(TicketFormItemValues::getCurrentDealUsers, user);
//            });
//        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getTicketId())) {
            lambdaQueryWrapper.eq(TicketFormItemValues::getTicketDataId, pageQueryTicketDataReqDt.getTicketId());
        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getCreateTime())) {
            String[] createTimeStr = pageQueryTicketDataReqDt.getCreateTime();
            if (StringUtils.isNotEmpty(createTimeStr[0])) {
                lambdaQueryWrapper.ge(TicketFormItemValues::getCreateTime, createTimeStr[0]);
            }
            if (StringUtils.isNotEmpty(createTimeStr[1])) {
                lambdaQueryWrapper.le(TicketFormItemValues::getCreateTime, createTimeStr[1]);
            }
        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getTicketFinishTime())) {
            String[] ticketFinishTimeStr = pageQueryTicketDataReqDt.getTicketFinishTime();
            if (StringUtils.isNotEmpty(ticketFinishTimeStr[0])) {
                lambdaQueryWrapper.ge(TicketFormItemValues::getTicketFinishTime, ticketFinishTimeStr[0]);
            }
            if (StringUtils.isNotEmpty(ticketFinishTimeStr[1])) {
                lambdaQueryWrapper.le(TicketFormItemValues::getTicketFinishTime, ticketFinishTimeStr[1]);
            }
        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getCreatedBy())) {
            List<String> createdByList = pageQueryTicketDataReqDt.getCreatedBy();
            lambdaQueryWrapper.and(wrapper -> {
                createdByList.stream().forEach(createdBy -> {
                    wrapper.or().like(TicketFormItemValues::getCreateBy, createdBy);
                });
            });
        }
        if (ObjectHelper.isNotEmpty(pageQueryTicketDataReqDt.getCurrentDoneUser())) {
            List<String> currentDoneUserList = pageQueryTicketDataReqDt.getCurrentDoneUser();
            lambdaQueryWrapper.and(wrapper -> {
                currentDoneUserList.stream().forEach(createdBy -> {
                    wrapper.or().like(TicketFormItemValues::getCurrentDoneUsers, createdBy);
                });
            });
        }
        if (ObjectHelper.isNotEmpty(ticketFormItemAttriDtoList)) {
            //遍历ticketFormItemAttriDtoList,组装sql。
            for (int i = 0; i < ticketFormItemAttriDtoList.size(); i++) {
                TicketFormItemAttriDto ticketFormItemAttriDto = ticketFormItemAttriDtoList.get(i);
                FormItemTypeEnum formItemType = ticketFormItemAttriDto.getFormItemType();
                String formItemId = ticketFormItemAttriDto.getFormItemId();
                String formItemValue = ticketFormItemAttriDto.getFormItemValue();
                switch (formItemType) {
                    case INPUT:
                        if (ObjectHelper.isEmpty(formItemId)) break;
                        lambdaQueryWrapper.like(SFunctionMap.getSFunction(formItemId), formItemValue);
                        break;
                    case INPUTNUMBER:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        Double[] itemValueDoubleArr = JSON.parseObject(formItemValue, Double[].class);
                        if (ObjectHelper.isEmpty(itemValueDoubleArr)) break;
                        if (ObjectHelper.isEmpty(itemValueDoubleArr[0]) && ObjectHelper.isEmpty(itemValueDoubleArr[1]))
                            break;
                        if (ObjectHelper.isEmpty(itemValueDoubleArr[0]) && ObjectHelper.isNotEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[1]);
                        } else if (ObjectHelper.isNotEmpty(itemValueDoubleArr[0]) && ObjectHelper.isEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[0]);
                        } else if (ObjectHelper.isNotEmpty(itemValueDoubleArr[0]) && ObjectHelper.isNotEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[1]);
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[0]);
                        }
                        break;
                    case SELECT:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        List<String> itemValueList = JSON.parseObject(formItemValue, List.class);
                        if (ObjectHelper.isEmpty(itemValueList)) break;
                        lambdaQueryWrapper.in(SFunctionMap.getSFunction(formItemId), itemValueList);
                        break;
                    case SELECTMULTIPLE:
                        //item value里面是多个值
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        lambdaQueryWrapper.apply(String.format(" JSON_CONTAINS('%s',JSON_EXTRACT(%s, '$')) ", formItemValue, formItemId));
                        break;
                    case TIME:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        String[] itemValueStrArr = JSON.parseObject(formItemValue, String[].class);
                        if (ObjectHelper.isEmpty(itemValueStrArr)) break;
                        if (ObjectHelper.isEmpty(itemValueStrArr[0]) && ObjectHelper.isEmpty(itemValueStrArr[1])) break;
                        if (ObjectHelper.isEmpty(itemValueStrArr[0]) && ObjectHelper.isNotEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueStrArr[1]);
                        } else if (ObjectHelper.isNotEmpty(itemValueStrArr[0]) && ObjectHelper.isEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueStrArr[0]);
                        } else if (ObjectHelper.isNotEmpty(itemValueStrArr[0]) && ObjectHelper.isNotEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueStrArr[1]);
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueStrArr[0]);
                        }
                        break;
                    case TIMESPAN:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        JSONArray itemValueArr = (JSONArray) JSONArray.parse(formItemValue);
                        if (ObjectHelper.isEmpty(itemValueArr)) break;
                        if (ObjectHelper.isEmpty(itemValueArr.get(0)) && ObjectHelper.isEmpty(itemValueArr.get(1)))
                            break;
                        if (ObjectHelper.isEmpty(itemValueArr.get(0)) && ObjectHelper.isNotEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr1 = (JSONArray) itemValueArr.get(1);
                            String itemValueArr1Begin = (String) itemValueArr1.get(0);
                            String itemValueArr1End = (String) itemValueArr1.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) >= \'%s\'", formItemId, itemValueArr1Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) <= \'%s\'", formItemId, itemValueArr1End));
                        } else if (ObjectHelper.isNotEmpty(itemValueArr.get(0)) && ObjectHelper.isEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr0 = (JSONArray) itemValueArr.get(0);
                            String itemValueArr0Begin = (String) itemValueArr0.get(0);
                            String itemValueArr0End = (String) itemValueArr0.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) >= \'%s\'", formItemId, itemValueArr0Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) <= \'%s\'", formItemId, itemValueArr0End));
                        } else if (ObjectHelper.isNotEmpty(itemValueArr.get(0)) && ObjectHelper.isNotEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr0 = (JSONArray) itemValueArr.get(0);
                            String itemValueArr0Begin = (String) itemValueArr0.get(0);
                            String itemValueArr0End = (String) itemValueArr0.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) >= \'%s\'", formItemId, itemValueArr0Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) <= \'%s\'", formItemId, itemValueArr0End));
                            JSONArray itemValueArr1 = (JSONArray) itemValueArr.get(1);
                            String itemValueArr1Begin = (String) itemValueArr1.get(0);
                            String itemValueArr1End = (String) itemValueArr1.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) >= \'%s\'", formItemId, itemValueArr1Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) <= \'%s\'", formItemId, itemValueArr1End));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        lambdaQueryWrapper.orderByDesc(TicketFormItemValues::getUpdateTime);
        return lambdaQueryWrapper;
    }

    public static LambdaQueryWrapper<TicketFormItemValues> getTicketFormItemValuesWhereWrapper(AdvancedQueryDto advancedQueryDto, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList){

        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.isNull(TicketFormItemValues::getDeleteTime);
        //通用字段查询
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTicketDataId())) {
            lambdaQueryWrapper.eq(TicketFormItemValues::getTicketDataId, advancedQueryDto.getTicketDataId());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getApplyUser())) {
            lambdaQueryWrapper.like(TicketFormItemValues::getApplyUser, advancedQueryDto.getApplyUser());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentDealUser())) {
            lambdaQueryWrapper.like(TicketFormItemValues::getCurrentDealUsers, advancedQueryDto.getCurrentDealUser());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getCreateStartTime()) && ObjectHelper.isNotEmpty(advancedQueryDto.getCreateEndTime())) {
            lambdaQueryWrapper.between(TicketFormItemValues::getCreateTime, advancedQueryDto.getCreateStartTime(), advancedQueryDto.getCreateEndTime());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getUpdateStartTime()) && ObjectHelper.isNotEmpty(advancedQueryDto.getUpdateEndTime())) {
            lambdaQueryWrapper.between(TicketFormItemValues::getUpdateTime, advancedQueryDto.getUpdateStartTime(), advancedQueryDto.getUpdateEndTime());
        }
        if (CollectionUtils.isNotEmpty(advancedQueryDto.getTagsList())) {
            List<String> tagList = advancedQueryDto.getTagsList();
            lambdaQueryWrapper.and(LambdaQueryWrapper -> {
                for (String tag : tagList) {
                    LambdaQueryWrapper.like(TicketFormItemValues::getTags, tag);
                    LambdaQueryWrapper.or();
                }
            });
        }
        if (CollectionUtils.isNotEmpty(advancedQueryDto.getApplyTicketWaysList())) {
            List<String> applyTicketWaysList = advancedQueryDto.getApplyTicketWaysList();
            lambdaQueryWrapper.and(LambdaQueryWrapper -> {
                for (String applyTicketWays : applyTicketWaysList) {
                    LambdaQueryWrapper.like(TicketFormItemValues::getApplyTicketWays, applyTicketWays);
                    LambdaQueryWrapper.or();
                }
            });
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTemplateIdList())) {
            lambdaQueryWrapper.in(TicketFormItemValues::getTemplateId, advancedQueryDto.getTemplateIdList());
        }

        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTemplateId())) {
            lambdaQueryWrapper.eq(TicketFormItemValues::getTemplateId, advancedQueryDto.getTemplateId());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTicketStatusList())) {
            lambdaQueryWrapper.in(TicketFormItemValues::getTicketStatus, advancedQueryDto.getTicketStatusList());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getSearchValue())) {
            lambdaQueryWrapper.and(LambdaQueryWrapper ->
                    LambdaQueryWrapper.like(TicketFormItemValues::getTicketDataId, advancedQueryDto.getSearchValue())
                            .or()
                            .like(TicketFormItemValues::getTicketName, advancedQueryDto.getSearchValue())
                            .or()
                            .like(TicketFormItemValues::getTags, advancedQueryDto.getSearchValue()));
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.isCreatedByMe()) && advancedQueryDto.isCreatedByMe()
                && ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentUserInfo()))
        {
            lambdaQueryWrapper.like(TicketFormItemValues::getApplyUser, advancedQueryDto.getCurrentUserInfo());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.isNeedHandleByMe()) && advancedQueryDto.isNeedHandleByMe()
                && ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentUserInfo()))
        {
            lambdaQueryWrapper.and(LambdaQueryWrapper ->
                    LambdaQueryWrapper.like(TicketFormItemValues::getCurrentDealUsers, advancedQueryDto.getCurrentUserInfo())
                            .or()
                            .like(TicketFormItemValues::getCurrentDoneUsers, advancedQueryDto.getCurrentUserInfo())
                            .or()
                            .like(TicketFormItemValues::getCurrentCcUsers, advancedQueryDto.getCurrentUserInfo()));
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTicketStatusForUserStr())
                && ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentUserInfo())
                && UserDealTypeEnum.MY_DEAL_WAITING_HANDLE.getCode().equals(advancedQueryDto.getTicketStatusForUserStr()))
        {
            lambdaQueryWrapper.like(TicketFormItemValues::getCurrentDealUsers, advancedQueryDto.getCurrentUserInfo());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTicketStatusForUserStr())
                && ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentUserInfo())
                && UserDealTypeEnum.MY_DEAL_HANDLED.getCode().equals(advancedQueryDto.getTicketStatusForUserStr()))
        {
            lambdaQueryWrapper.like(TicketFormItemValues::getCurrentDoneUsers, advancedQueryDto.getCurrentUserInfo());
        }
        if (ObjectHelper.isNotEmpty(advancedQueryDto.getTicketStatusForUserStr())
                && ObjectHelper.isNotEmpty(advancedQueryDto.getCurrentUserInfo())
                && UserDealTypeEnum.MY_DEAL_HAS_CC.getCode().equals(advancedQueryDto.getTicketStatusForUserStr()))
        {
            lambdaQueryWrapper.like(TicketFormItemValues::getCurrentCcUsers, advancedQueryDto.getCurrentUserInfo());
        }
        if (ObjectHelper.isNotEmpty(ticketFormItemAttriDtoList)) {
            //遍历ticketFormItemAttriDtoList,组装sql。
            for (int i = 0; i < ticketFormItemAttriDtoList.size(); i++) {
                TicketFormItemAttriDto ticketFormItemAttriDto = ticketFormItemAttriDtoList.get(i);
                FormItemTypeEnum formItemType = ticketFormItemAttriDto.getFormItemType();
                String formItemId = ticketFormItemAttriDto.getFormItemId();
                String formItemValue = ticketFormItemAttriDto.getFormItemValue();
                switch (formItemType) {
                    case INPUT:
                        if (ObjectHelper.isEmpty(formItemId)) break;
                        lambdaQueryWrapper.like(SFunctionMap.getSFunction(formItemId), formItemValue);
                        break;
                    case INPUTNUMBER:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        Double[] itemValueDoubleArr = JSON.parseObject(formItemValue, Double[].class);
                        if (ObjectHelper.isEmpty(itemValueDoubleArr)) break;
                        if (ObjectHelper.isEmpty(itemValueDoubleArr[0]) && ObjectHelper.isEmpty(itemValueDoubleArr[1]))
                            break;
                        if (ObjectHelper.isEmpty(itemValueDoubleArr[0]) && ObjectHelper.isNotEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[1]);
                        } else if (ObjectHelper.isNotEmpty(itemValueDoubleArr[0]) && ObjectHelper.isEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[0]);
                        } else if (ObjectHelper.isNotEmpty(itemValueDoubleArr[0]) && ObjectHelper.isNotEmpty(itemValueDoubleArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[1]);
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueDoubleArr[0]);
                        }
                        break;
                    case SELECT:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        List<String> itemValueList = JSON.parseObject(formItemValue, List.class);
                        if (ObjectHelper.isEmpty(itemValueList)) break;
                        lambdaQueryWrapper.in(SFunctionMap.getSFunction(formItemId), itemValueList);
                        break;
                    case SELECTMULTIPLE:
                        //item value里面是多个值
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        lambdaQueryWrapper.apply(String.format(" JSON_CONTAINS('%s',JSON_EXTRACT(%s, '$')) ", formItemValue, formItemId));
                        break;
                    case TIME:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        String[] itemValueStrArr = JSON.parseObject(formItemValue, String[].class);
                        if (ObjectHelper.isEmpty(itemValueStrArr)) break;
                        if (ObjectHelper.isEmpty(itemValueStrArr[0]) && ObjectHelper.isEmpty(itemValueStrArr[1])) break;
                        if (ObjectHelper.isEmpty(itemValueStrArr[0]) && ObjectHelper.isNotEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueStrArr[1]);
                        } else if (ObjectHelper.isNotEmpty(itemValueStrArr[0]) && ObjectHelper.isEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueStrArr[0]);
                        } else if (ObjectHelper.isNotEmpty(itemValueStrArr[0]) && ObjectHelper.isNotEmpty(itemValueStrArr[1])) {
                            lambdaQueryWrapper.le(SFunctionMap.getSFunction(formItemId), itemValueStrArr[1]);
                            lambdaQueryWrapper.ge(SFunctionMap.getSFunction(formItemId), itemValueStrArr[0]);
                        }
                        break;
                    case TIMESPAN:
                        if (ObjectHelper.isEmpty(formItemValue)) break;
                        JSONArray itemValueArr = (JSONArray) JSONArray.parse(formItemValue);
                        if (ObjectHelper.isEmpty(itemValueArr)) break;
                        if (ObjectHelper.isEmpty(itemValueArr.get(0)) && ObjectHelper.isEmpty(itemValueArr.get(1)))
                            break;
                        if (ObjectHelper.isEmpty(itemValueArr.get(0)) && ObjectHelper.isNotEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr1 = (JSONArray) itemValueArr.get(1);
                            String itemValueArr1Begin = (String) itemValueArr1.get(0);
                            String itemValueArr1End = (String) itemValueArr1.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) >= \'%s\'", formItemId, itemValueArr1Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) <= \'%s\'", formItemId, itemValueArr1End));
                        } else if (ObjectHelper.isNotEmpty(itemValueArr.get(0)) && ObjectHelper.isEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr0 = (JSONArray) itemValueArr.get(0);
                            String itemValueArr0Begin = (String) itemValueArr0.get(0);
                            String itemValueArr0End = (String) itemValueArr0.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) >= \'%s\'", formItemId, itemValueArr0Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) <= \'%s\'", formItemId, itemValueArr0End));
                        } else if (ObjectHelper.isNotEmpty(itemValueArr.get(0)) && ObjectHelper.isNotEmpty(itemValueArr.get(1))) {
                            JSONArray itemValueArr0 = (JSONArray) itemValueArr.get(0);
                            String itemValueArr0Begin = (String) itemValueArr0.get(0);
                            String itemValueArr0End = (String) itemValueArr0.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) >= \'%s\'", formItemId, itemValueArr0Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[0]')) <= \'%s\'", formItemId, itemValueArr0End));
                            JSONArray itemValueArr1 = (JSONArray) itemValueArr.get(1);
                            String itemValueArr1Begin = (String) itemValueArr1.get(0);
                            String itemValueArr1End = (String) itemValueArr1.get(1);
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) >= \'%s\'", formItemId, itemValueArr1Begin));
                            lambdaQueryWrapper.apply(String.format("JSON_UNQUOTE(JSON_EXTRACT(%s,'$[1]')) <= \'%s\'", formItemId, itemValueArr1End));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return lambdaQueryWrapper;
    }

}

