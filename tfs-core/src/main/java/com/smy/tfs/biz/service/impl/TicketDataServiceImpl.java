package com.smy.tfs.biz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.util.SequenceUtil;
import com.smy.fsp.client.urlsign.UrlSignUtil;
import com.smy.tfs.api.constants.QWCardMD;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.NotificationDto.CreateChatGroup;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.AccountInfoDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.*;
import com.smy.tfs.api.enums.*;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.bo.DubboServiceConfig;
import com.smy.tfs.biz.bo.TicketDataAllBO;
import com.smy.tfs.biz.bo.TicketTemplateAllBO;
import com.smy.tfs.biz.bo.UpdateTicketFormDataMidBO;
import com.smy.tfs.biz.component.AccountReturnComponent;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.mapper.*;
import com.smy.tfs.biz.service.*;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.AesUtil;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.framework.config.DynamicDubboConsumer;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.apache.dubbo.apidocs.annotations.RequestParam;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.smy.tfs.biz.service.impl.NotificationBizService.BuildContentList;
import static com.smy.tfs.biz.service.impl.TicketDataServiceInner.*;
import static com.smy.tfs.common.utils.PageUtils.startPage;
import static com.smy.tfs.common.utils.PageUtils.startPageNotCount;

/**
 * <p>
 * 工单数据表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Slf4j
@Component("ticketDataServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "工单数据服务", apiInterface = ITicketDataService.class)
public class TicketDataServiceImpl extends ServiceImpl<TicketDataMapper, TicketData> implements ITicketDataService {

    @Resource
    private AccountReturnComponent accountReturnComponent;

    @Resource
    private ITicketFlowNodeTemplateService flowNodeTemplateService;

    @Resource
    private ITicketFlowNodeActionTemplateService flowNodeActionTemplateService;

    @Resource
    private ITicketAppService ticketAppService;

    @Resource
    private ITicketFlowNodeDataService flowNodeDataService;

    @Resource
    private ITicketFlowEventDataService flowEventDataService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TicketFlowNodeApproveDetailService ticketFlowNodeApproveDetailService;

    @Resource
    private ITicketAppService appService;

    @Resource
    private ITicketAccountService accountService;

    @Resource
    private TicketAppMapper ticketAppMapper;

    @Resource
    private TicketDataMapper ticketDataMapper;

    @Resource
    private TicketFlowDataMapper ticketFlowDataMapper;

    @Resource
    private TicketFlowNodeDataMapper ticketFlowNodeDataMapper;

    @Resource
    private TicketFlowNodeExecutorDataMapper ticketFlowNodeExecutorDataMapper;

    @Resource
    private TicketFlowNodeActionDataMapper ticketFlowNodeActionDataMapper;

    @Resource
    private TicketFlowEventDataMapper ticketFlowEventDataMapper;

    @Resource
    private TicketFormDataMapper ticketFormDataMapper;

    @Resource
    ITicketFormDataService ticketFormDataService;

    @Resource
    private ITicketFormItemDataLogService ticketFormItemDataLogService;

    @Resource
    ITicketFormItemDataService ticketFormItemDataService;

    @Resource
    private TicketFormItemDataMapper ticketFormItemDataMapper;

    @Resource
    private TicketFlowTemplateMapper ticketFlowTemplateMapper;

    @Resource
    TicketFlowEventTemplateMapper ticketFlowEventTemplateMapper;

    @Resource
    private TicketFormItemTemplateMapper ticketFormItemTemplateMapper;

    @Resource
    private TicketFlowNodeExecutorTemplateMapper ticketFlowNodeExecutorTemplateMapper;

    @Resource
    private TicketFormTemplateMapper ticketFormTemplateMapper;

    @Resource
    private TicketFlowNodeRuleTemplateMapper ticketFlowNodeRuleTemplateMapper;

    @Resource
    TicketDataApproveService ticketDataApproveService;

    @Resource
    ITicketAccountService ticketAccountService;

    @Resource
    ITicketFlowNodeDataService ticketFlowNodeDataService;

    @Resource
    ITicketFlowEventDataService ticketFlowEventDataService;

    @Resource
    ITicketFlowNodeExecutorDataService ticketFlowNodeExecutorDataService;

    @Resource
    ITicketFlowNodeActionDataService ticketFlowNodeActionDataService;

    @Resource
    ITicketDataService ticketDataService;

    @Resource
    ITicketFlowDataService ticketFlowDataService;

    @Resource
    ITicketTemplateService ticketTemplateService;

    @Resource
    NotificationService notificationService;

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Resource
    ITicketExecutorGroupService ticketExecutorGroupService;

    @Resource
    private ITicketFormItemValuesService ticketFormItemValuesService;

    @Resource
    private TicketFormItemValuesMapper ticketFormItemValuesMapper;

    @Resource
    private ITicketFormItemIdColMappingService iTicketFormItemIdColMappingService;

    @Resource
    private ITicketFormItemTemplateService iTicketFormItemTemplateService;

    @Resource
    private TfSJumpUrlProperties tfSJumpUrlProperties;

    @Resource
    INotificationBizService notificationBizService;

    @Resource
    ITicketCategoryService ticketCategoryService;

    @Resource
    DynamicDubboConsumer dynamicDubboConsumer;

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    @ApiDoc(value = "获取工单申请编号", description = "获取工单申请编号")
    public Response<String> getTicketApplyId(@RequestParam(value = "应用id", example = "1001", description = "应用id") String appid){

        String applyID = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_DATA);
        return initApplyId(appid, applyID);
    }


    @Resource
    private com.smy.ncs.service.export.cust.ExportCustomerInfoService exportCustomerInfoService;

    /**
     * @param userType
     * @param userId
     * @param userName
     * @return
     */
    public Response<HashMap<String, String>> getConvertData(ConvertDataParamDto convertDataParamDto, String userType, String userId, String userName){
//        try {
//            JSONObject output = new JSONObject();
//            String customerNo = "168000399898";
//            ExportCustomerInfoRequest exportCustomerInfoRequest = new ExportCustomerInfoRequest();
//            exportCustomerInfoRequest.setCustomerNo(customerNo);
//            CustomerOverdueInfoResponse customerOverdueInfoResponse = exportCustomerInfoService.queryOverdueInfo(exportCustomerInfoRequest);
//            output.put("催员ID", customerOverdueInfoResponse.getCUserId());
//            output.put("催员组长ID", customerOverdueInfoResponse.getManageIds().toString());
//        } catch (Exception ex) {
//            log.error("获取催员信息已采样:{}",ex);
//        }
        List<TicketFormItemData> formItemDataList = ticketFormItemDataService.lambdaQuery()
                .isNull(TicketFormItemData::getDeleteTime)
                .eq(TicketFormItemData::getId, convertDataParamDto.getConvertKey()).list();
        if (CollectionUtils.isEmpty(formItemDataList)) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应表单ID：%s, 表单数据为空", convertDataParamDto.getConvertKey()));
        }
        if (formItemDataList.size() > 1) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应表单ID：%s, 表单数量大于1", convertDataParamDto.getConvertKey()));
        }
        TicketFormItemData itemData = formItemDataList.get(0);
        if (!Objects.equals(convertDataParamDto.getConvertValue(), itemData.getItemValue())) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("页面数据：%s 和 底层数据：%s 不一致。", convertDataParamDto.getConvertValue(), itemData.getItemValue()));
        }
        if (!Objects.equals(convertDataParamDto.getTicketDataId(), itemData.getTicketDataId())) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("页面数据ID:%s, 底层数据ID:%s 不一致。", convertDataParamDto.getConvertKey(), itemData.getTicketDataId()));
        }
        if (StringUtils.isEmpty(itemData.getItemConfigExt())) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应数据ID：%s, 表单配置项为空", itemData.getId()));
        }
        String itemConfig = itemData.getItemConfigExt();
        itemData.EqConfig();
        com.alibaba.fastjson2.JSONObject jsonObject = com.alibaba.fastjson2.JSONObject.parseObject(itemConfig);
        if (jsonObject == null || !jsonObject.containsKey("convertible")) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应数据ID：%s, 表单配置项convertible不存在", itemData.getId()));
        }
        if (Objects.equals(jsonObject.getString("convertible"), "YES")) {
            String convertConfig = jsonObject.getString("convertConfig");
            if (StringUtils.isEmpty(convertConfig)) {
                return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, String.format("对应数据ID：%s, 表单配置项convertConfig为空", itemData.getId()));
            }
            var dubboServiceConfigRes = DubboServiceConfig.parseStrToDubboConfig(convertConfig);
            if (dubboServiceConfigRes.getEnum() != BizResponseEnums.SUCCESS) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务配置解析失败:" + convertConfig);
            }
            var dubboServiceConfig = dubboServiceConfigRes.getData();
            try {
                JSONObject inputParam = new JSONObject();
                inputParam.put("ticketDataId", itemData.getTicketDataId());
                inputParam.put("ItemId", itemData.getId());
                inputParam.put("ItemName", itemData.getItemLabel());
                inputParam.put("ItemValue", itemData.getItemValue());
                Object invokeResult = dynamicDubboConsumer.invokeDubboService(dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), new Object[]{inputParam.toJSONString()}, dubboServiceConfig.getVersion(), dubboServiceConfig.getGroup());
                JSONObject invokeJsonObject = JSONObject.parseObject(JSONUtil.toJsonStr(invokeResult));
                if (invokeJsonObject.containsKey("code") && "0".equals(invokeJsonObject.getString("code")) || "200".equals(invokeJsonObject.getString("code")) || 0 == invokeJsonObject.getIntValue("code") || 200 == invokeJsonObject.getIntValue("code")) {
                    HashMap<String, String> convertObj = new LinkedHashMap<>();
                    if (invokeJsonObject.containsKey("data")) {
                        JSONObject innerData = invokeJsonObject.getJSONObject("data");
                        if (innerData != null) {
                            for (String key : innerData.keySet()) {
                                convertObj.put(key, innerData.getString(key));
                            }
                            return new Response<>(convertObj, BizResponseEnums.SUCCESS, "success");
                        }
                    }
                } else {
                    String errMsg = invokeJsonObject.getString("msg");
                    log.error("dubbo服务调用失败2,接口名：{}, 方法名：{},  返回结果：{}", dubboServiceConfig.getInterfaceName(), dubboServiceConfig.getMethodName(), JSONUtil.toJsonStr(invokeResult));
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务调用失败2");
                }
            } catch (Exception ex) {
                log.error("dubbo服务调用异常,配置内容：{} 异常信息：{}", convertConfig, ex != null ? ex.getMessage() : "");
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "dubbo服务调用异常");
            }
        }
        return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "获取数据为空或异常");
    }

    private Response<String> initApplyId(String appid, String applyID){

        TicketData existData = this.baseMapper.selectById(applyID);
        if (existData != null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("applyID:%s 工单申请编号已存在, 请重试", applyID));
        }
        TicketApp ticketApp = ticketAppMapper.selectById(appid);
        if (ticketApp == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("appid:%s 应用不存在", appid));
        }
        TicketData newTicketData = new TicketData();
        newTicketData.InitTicketData(applyID, appid, "");
        int count = this.baseMapper.insert(newTicketData);
        if (count == 1) {
            return new Response<>(applyID, BizResponseEnums.SUCCESS, "");
        } else {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("appid:%s applyID:%s 获取工单申请编号异常, 请重试", appid, applyID));
        }
    }

    @Override
    @ApiDoc(value = "创建工单数据", description = "创建工单数据")
    public Response<String> createTicket(
            @RequestParam(value = "表单内容", example = " ", description = "表单内容")
            TicketDataStdDto ticketDataStdDto,
            @RequestParam(value = "用户类型", example = " ", description = "用户类型")
            String userType,
            @RequestParam(value = "用户ID", example = " ", description = "用户ID")
            String userId,
            @RequestParam(value = "用户名称", example = " ", description = "用户名称")
            String userName)
    {

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo applyUser = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        //兼容业务系统拿不到用户名情况
        if (StringUtils.isEmpty(userName) && StringUtils.isNotEmpty(ticketAccountMapping.getAccountName())) {
            applyUser.setAccountName(ticketAccountMapping.getAccountName());
        }
        if (ticketDataStdDto == null || StringUtils.isAnyEmpty(ticketDataStdDto.getApplyId(), ticketDataStdDto.getTicketTemplateId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        TicketData updateTicketData = this.baseMapper.selectById(ticketDataStdDto.getApplyId());// for update
        //使用业务ID作为统一工单ID
        if (updateTicketData == null) {
            Optional<TicketTemplate> templateOpt = ticketTemplateService.lambdaQuery()
                    .isNull(TicketTemplate::getDeleteTime)
                    .eq(TicketTemplate::getTicketTemplateCode, ticketDataStdDto.getTicketTemplateId())
                    .oneOpt();
            if (!templateOpt.isPresent()) {
                templateOpt = ticketTemplateService.lambdaQuery()
                        .isNull(TicketTemplate::getDeleteTime)
                        .eq(TicketTemplate::getId, ticketDataStdDto.getTicketTemplateId())
                        .oneOpt();
            }
            if (!templateOpt.isPresent()) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单模版ID：%s 工单模版不存在", ticketDataStdDto.getTicketTemplateId()));
            }
            Response<String> applyIdResp = initApplyId(templateOpt.get().getAppId(), ticketDataStdDto.getApplyId());
            if (!applyIdResp.isSuccess()) {
                return applyIdResp;
            }
            updateTicketData = this.baseMapper.selectById(ticketDataStdDto.getApplyId());
        }
        String ticketTemplateID = ticketDataStdDto.getTicketTemplateId();//工单模版ID
        //数据校验
        Response paramCheckRps = TicketDataStdDtoCreatedParamCheck(ticketDataStdDto, updateTicketData);
        if (!paramCheckRps.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return paramCheckRps;
        }
        //业务唯一单号校验
        if (StringUtils.isNotEmpty(ticketDataStdDto.getTicketBusinessKey())) {
            TicketTemplate ticketTemplate = null;
            var tempOpt = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getTicketTemplateCode, ticketTemplateID).oneOpt();
            if (tempOpt.isPresent()) {
                ticketTemplate = tempOpt.get();
            }
            if (ticketTemplate == null) {
                tempOpt = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, ticketTemplateID).oneOpt();
                if (tempOpt.isPresent()) {
                    ticketTemplate = tempOpt.get();
                }
            }
            if (ticketTemplate == null) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单模版ID:%s 模版数据不存在", ticketTemplateID));
            }
            Optional<TicketData> ticketDataOptional = ticketDataService.lambdaQuery()
                    .isNull(TicketData::getDeleteTime)
                    .eq(TicketData::getTicketBusinessKey, ticketDataStdDto.getTicketBusinessKey())
                    .eq(TicketData::getTemplateId, ticketTemplate.getId())
                    .eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING)
                    .oneOpt();
            if (ticketDataOptional.isPresent()) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应模版：%s 业务单号：%s, 已经存在审批中的工单", ticketTemplate.getId(), ticketDataStdDto.getTicketBusinessKey()));
            }
        }
        //获取模版数据
        TicketTemplateAllBO ticketTemplateAllBO = getTicketTemplateAll(ticketTemplateID);
        //数据校验
        Response templateCheckRps = CreateTicketParamCheck(ticketDataStdDto.getApplyId(), updateTicketData, ticketTemplateAllBO);
        if (!templateCheckRps.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return templateCheckRps;
        }
        if (ticketDataStdDto.getCreateTime() != null) {
            updateTicketData.setCreateTime(ticketDataStdDto.getCreateTime());
        }
        //构建工单数据
        var buildTicketDataAllBORsp = buildTicketDataAllBO(ticketTemplateAllBO, ticketDataStdDto, updateTicketData, applyUser, null);
        if (!buildTicketDataAllBORsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>(null, buildTicketDataAllBORsp.getEnum(), buildTicketDataAllBORsp.getMsg());
        }
        TicketDataAllBO ticketDataAllBO = buildTicketDataAllBORsp.getData();
        //事务保存
        var saveRsp = SaveCreateTicket(ticketDataAllBO);
        if (!saveRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return saveRsp;
        }
        //执行事件
        ExecuteCreateTicket(ticketDataAllBO);
        //进入审批节点
        var dealResp = ticketDataService.dealTicketDataById(ticketDataAllBO.getTicketData().getId(), ApproveDealTypeEnum.APPLY.getCode(), "提交工单", applyUser.getAccountType(), applyUser.getAccountId(), applyUser.getAccountName(), ticketDataAllBO.getTicketData().getCurrentNodeId());
        if (!dealResp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>("", dealResp.getEnum(), dealResp.getMsg());
        }
        return new Response<>(ticketDataAllBO.getTicketData().getId(), BizResponseEnums.SUCCESS, "成功");
    }

    @ApiDoc(value = "计算流程节点", description = "计算流程节点")
    public Response<String> countFlowNode(TicketDataStdDto ticketDataStdDto){

        if (ticketDataStdDto == null || StringUtils.isBlank(ticketDataStdDto.getApplyId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        TicketData updateTicketData = this.baseMapper.selectById(ticketDataStdDto.getApplyId());// for update
        String ticketTemplateID = ticketDataStdDto.getTicketTemplateId();//工单模版ID
        //数据校验
        Response paramCheckRps = TicketDataStdDtoParamCheck(ticketDataStdDto, updateTicketData);
        if (!paramCheckRps.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return paramCheckRps;
        }
        //获取模版数据
        TicketTemplateAllBO ticketTemplateAllBO = getTicketTemplateAll(ticketTemplateID);
        //数据校验
        Response templateCheckRps = CreateTicketParamCheck(ticketDataStdDto.getApplyId(), updateTicketData, ticketTemplateAllBO);
        if (!templateCheckRps.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return templateCheckRps;
        }
        //newTicketFlowNodeDataList 填充
        Response<List<TicketFlowNodeData>> nodeListRsp = buildTicketFlowNodeDataList(updateTicketData.getId(), "-1", ticketDataStdDto.getFormItems(), ticketTemplateAllBO.getTicketFormItemTemplateList(), ticketTemplateAllBO.getTicketFlowNodeTemplateList(), ticketTemplateAllBO.getTicketFlowNodeRuleTemplateList(), ticketTemplateAllBO.getTicketFlowNodeExecutorTemplateList());
        if (!nodeListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>(null, nodeListRsp.getEnum(), nodeListRsp.getMsg());
        }
        StringBuilder nodeIds = new StringBuilder();
        for (var item : nodeListRsp.getData()) {
            nodeIds.append(item.getTemplateId()).append("-");
        }
        return new Response<>(nodeIds.toString(), BizResponseEnums.SUCCESS, "成功");
    }

    @ApiDoc(value = "退回指定节点", description = "退回指定节点")
    public Response<String> gotoFlowNode(String ticketDataId, String currentNodeId, String gotoNodeId, String gotoNodeReason, AccountInfo loginUser){

        Date now = new Date();
        String loginUserStr = loginUser.ToJsonString();
        TicketData ticketData = ticketDataService.lambdaQuery().isNull(TicketData::getDeleteTime).eq(TicketData::getId, ticketDataId).one();
        List<TicketFlowNodeData> allTicketFlowNodeDataList = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getTicketDataId, ticketDataId).list();
        var checkResp = GotoFlowNodeParamCheck(ticketData, currentNodeId);
        if (!checkResp.isSuccess()) {
            return new Response<>(null, checkResp.getEnum(), checkResp.getMsg());
        }
        var nextNewResp = BuildNextNewFlowNodeDataList(ticketData, allTicketFlowNodeDataList, gotoNodeId);
        if (!nextNewResp.isSuccess()) {
            return new Response<>(null, nextNewResp.getEnum(), nextNewResp.getMsg());
        }
        List<TicketFlowNodeData> nextNewNodeList = nextNewResp.getData();
        if (CollectionUtils.isEmpty(nextNewNodeList)) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "后续待审批节点为空");
        }
        TicketFlowNodeData linkTicketFlowNodeData = new TicketFlowNodeData();
        linkTicketFlowNodeData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA));
        linkTicketFlowNodeData.setTemplateId("-1");
        linkTicketFlowNodeData.setTicketDataId(ticketData.getId());
        linkTicketFlowNodeData.setNodeStatus(NodeStatusEnum.APPROVE_PASS);
        linkTicketFlowNodeData.setCreateBy(loginUserStr);
        linkTicketFlowNodeData.setCreateTime(now);
        linkTicketFlowNodeData.setUpdateBy(loginUserStr);
        linkTicketFlowNodeData.setUpdateTime(now);

        AtomicReference<String> newCurrentNodeID = new AtomicReference<>();
        //节点拼接（1.保留原来审批记录 2.查询新的待审批节点 3.调整老的待审批节点的头节点）
        // A->B->C->D    a->b->link->B->C-D（C退回到link->B，新增B） a->b->link->A->B->C->D（C退回到A，新增link->A->B）
        var tranResult = transactionTemplate.execute(action -> {
            String newCurrentNodeName = null;
            String newLastNodeID = null;
            List<TicketFlowNodeExecutorData> newCurrentNodeExecutorList = null;

            TicketFlowNodeData oldCurrentNode = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, ticketData.getCurrentNodeId()).one();
            linkTicketFlowNodeData.setPreNodeId(oldCurrentNode.getPreNodeId());
            linkTicketFlowNodeData.setTicketFlowDataId(oldCurrentNode.getTicketFlowDataId());
            linkTicketFlowNodeData.setAuditedMethod(AuditedMethodEnum.OR);
            linkTicketFlowNodeData.setAuditedType(AuditedType.AUTO_PASS);
            linkTicketFlowNodeData.setNodeName(oldCurrentNode.getNodeName() + "(退回)");
            String preNodeID = linkTicketFlowNodeData.getId();
            //工单数据
            for (var nodeData : nextNewNodeList) {
                TicketFlowNodeData ticketFlowNodeData = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, nodeData.getId()).one();
                String newNodeID = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA);
                String newNodeName = nodeData.getNodeName();
                if (StringUtils.isNotEmpty(newNodeName) && !newNodeName.contains("(重审)")) {
                    newNodeName = newNodeName + "(重审)";
                }
                ticketFlowNodeData.setNodeName(newNodeName);
                ticketFlowNodeData.setId(newNodeID);
                ticketFlowNodeData.setPreNodeId(preNodeID);
                ticketFlowNodeData.setNodeStatus(newCurrentNodeID.get() == null ? NodeStatusEnum.APPROVING : NodeStatusEnum.APPROVE_INIT);

                ticketFlowNodeData.setNodeWxDealCardCode("");
                ticketFlowNodeData.setNodeWxDealCardMessageId("");
                ticketFlowNodeData.setUpdateTime(now);
                ticketFlowNodeData.setUpdateBy(loginUserStr);
                ticketFlowNodeData.setCreateTime(now);
                ticketFlowNodeData.setCreateBy(loginUserStr);
                ticketFlowNodeDataService.save(ticketFlowNodeData);

                List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = ticketFlowNodeExecutorDataService.lambdaQuery().isNull(TicketFlowNodeExecutorData::getDeleteTime).eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, nodeData.getId()).list();
                if (CollectionUtils.isNotEmpty(ticketFlowNodeExecutorDataList)) {
                    for (TicketFlowNodeExecutorData nodeExecutor : ticketFlowNodeExecutorDataList) {
                        String newNodeExecutorID = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA);
                        nodeExecutor.setId(newNodeExecutorID);
                        nodeExecutor.setTicketFlowNodeDataId(newNodeID);
                        nodeExecutor.setExecutorDoneList("");
                        nodeExecutor.setUpdateTime(now);
                        nodeExecutor.setUpdateBy(loginUserStr);
                        nodeExecutor.setCreateTime(now);
                        nodeExecutor.setCreateBy(loginUserStr);
                        ticketFlowNodeExecutorDataService.save(nodeExecutor);
                    }
                }

                List<TicketFlowEventData> ticketFlowEventDataList = ticketFlowEventDataService.lambdaQuery().isNull(TicketFlowEventData::getDeleteTime).eq(TicketFlowEventData::getTicketFlowNodeDataId, nodeData.getId()).list();
                if (CollectionUtils.isNotEmpty(ticketFlowEventDataList)) {
                    for (TicketFlowEventData event : ticketFlowEventDataList) {
                        String newEventID = SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_EVENT_DATA);
                        event.setId(newEventID);
                        event.setTicketFlowNodeDataId(newNodeID);
                        event.setEventStatus(EventStatusEnum.WAIT_EXECUTE);
                        event.setExecuteTime(null);
                        event.setUpdateTime(now);
                        event.setUpdateBy(loginUserStr);
                        event.setCreateTime(now);
                        event.setCreateBy(loginUserStr);
                        //把重试次数去掉，再放入新对象字段
                        if (StringUtils.isNotEmpty(event.getPushConfig()) && event.getPushConfig().contains("attempt")) {
                            String pushConfig = event.getPushConfig();
                            JSONObject pushConfigJson = JSONObject.parseObject(pushConfig);
                            pushConfigJson.remove("attempt");
                            event.setPushConfig(JSONObject.toJSONString(pushConfigJson));
                        }
                        ticketFlowEventDataService.save(event);
                    }
                }

                newLastNodeID = newNodeID;
                if (newCurrentNodeID.get() == null) {
                    newCurrentNodeID.set(newNodeID);
                    newCurrentNodeName = nodeData.getNodeName();
                    newCurrentNodeExecutorList = ticketFlowNodeExecutorDataList;
                }
                preNodeID = ticketFlowNodeData.getId();
            }
            ticketFlowNodeDataService.lambdaUpdate().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, oldCurrentNode.getId())
                    .set(TicketFlowNodeData::getPreNodeId, newLastNodeID)
                    .set(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVE_INIT)
                    .set(TicketFlowNodeData::getNodeWxDealCardCode, "")
                    .set(TicketFlowNodeData::getNodeWxDealCardMessageId, "")
                    .set(TicketFlowNodeData::getUpdateTime, now)
                    .set(TicketFlowNodeData::getUpdateBy, loginUserStr)
                    .update();

            ticketFlowNodeDataService.save(linkTicketFlowNodeData);
            TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
            approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            approveDetail.setTicketDataId(ticketData.getId());
            approveDetail.setTicketFlowNodeDataId(linkTicketFlowNodeData.getId());
            approveDetail.setDealUserType(loginUser.getAccountType());
            approveDetail.setDealUserId(loginUser.getAccountId());
            approveDetail.setDealUserName(loginUser.getAccountName());
            approveDetail.setDealType(ApproveDealTypeEnum.BACK);
            approveDetail.setCreateBy(loginUserStr);
            approveDetail.setCreateTime(now);
            approveDetail.setUpdateBy(loginUserStr);
            approveDetail.setUpdateTime(now);
            approveDetail.setDealOpinion(String.format("%s退回至%s", loginUser.getAccountName(), newCurrentNodeName) + (StringUtils.isNotEmpty(gotoNodeReason) ? "，原因：" + gotoNodeReason : ""));

            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("node_id", linkTicketFlowNodeData.getNodeName());
            paramsMap.put("detail_id", approveDetail.getId());
            paramsMap.put("detail_user_id", approveDetail.getDealUserId());
            paramsMap.put("detail_user_name", approveDetail.getDealUserName());
            paramsMap.put("detail_user_type", approveDetail.getDealUserType());
            paramsMap.put("detail_opinion", approveDetail.getDealOpinion());
            paramsMap.put("detail_type", approveDetail.getDealType().getCode());
            paramsMap.put("detail_type_des", approveDetail.getDealType().getDesc());
            Response<String> beforeResult = ticketDataApproveService.executeEvent(ticketData.getId(), currentNodeId, ExecuteStepEnum.BEFORE_BACK.getCode(), paramsMap, null, null);
            if (!beforeResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
                return false;
            }
            ticketFlowNodeApproveDetailService.save(approveDetail);

            List<AccountInfo> currentDealUsers = new ArrayList<>();
            List<AccountInfo> existCcs = AccountInfo.ToAccountInfoList(ticketData.getCurrentCcUsers());
            existCcs = existCcs == null ? new ArrayList<>() : existCcs;
            for (TicketFlowNodeExecutorData executor : newCurrentNodeExecutorList) {
                switch (executor.getExecutorType()) {
                    case APPLY_MEMBER_LIST:
                    case APPLY_GROUP:
                    case APPLY_LEADER:
                    case APPLY_SELF:
                    case APPLY_POINT:
                    case APPLY_DEPT_MANAGERS:
                    case APPLY_DEPT_POINT:
                    case APPLY_EXTERNAL_APPROVER:
                        List<AccountInfo> dealUsers = AccountInfo.ToAccountInfoList(executor.getExecutorList());
                        currentDealUsers.addAll(dealUsers);
                        break;
                    case CA_SELF:
                    case CA_MEMBER_LIST:
                    case CA_GROUP:
                    case CA_LEADER:
                    case CA_DEPT_MANAGERS:
                    case CA_EXTERNAL_APPROVER:
                        List<AccountInfo> ccUsers = AccountInfo.ToAccountInfoList(executor.getExecutorList());
                        existCcs.addAll(ccUsers);
                        break;
                    default:
                }
            }
            currentDealUsers = AccountInfo.Distinct(currentDealUsers);
            existCcs = AccountInfo.Distinct(existCcs);
            ticketDataService.lambdaUpdate().isNull(TicketData::getDeleteTime).eq(TicketData::getId, ticketData.getId())
                    .set(TicketData::getCurrentNodeId, newCurrentNodeID.get())
                    .set(TicketData::getCurrentNodeName, newCurrentNodeName + "(重审)")
                    .set(TicketData::getCurrentCcUsers, AccountInfo.ToAccountInfoListStr(existCcs))
                    .set(TicketData::getCurrentDealUsers, AccountInfo.ToAccountInfoListStr(currentDealUsers))
                    .set(TicketData::getUpdateTime, now)
                    .set(TicketData::getUpdateBy, loginUserStr)
                    .update();
            return true;
        });
        if (tranResult == false) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "数据保存失败");
        }
        //数据重新整合
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketData.getId()));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "数据重新整合失败");
        }
        TicketDataDto ticketDataDto = ticketDataDtoResponse.getData();
        //企微消息
        if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            //通知下一个节点审批人
            String title = "【退回】{{apply_user}}提交的{{ticket_name}}，待你处理"
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName());
            List<AccountInfo> sendUsers = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentDealUsers());
            var dealRes = notificationBizService.SendDealCard(
                    title,
                    ticketDataDto,
                    loginUser,
                    ApproveDealTypeEnum.SEND,
                    sendUsers,
                    false,
                    null
            );
            if (dealRes.getEnum() != BizResponseEnums.SUCCESS) {
                log.error("退回数据成功，发送企微卡片失败，错误信息：{}", dealRes.getMsg());
            }
        }

        //执行后续节点
        TicketFlowNodeData currentFlowNode = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, newCurrentNodeID.get()).one();
        ticketDataApproveService.autoApprove(ticketDataId, currentFlowNode.getAuditedType(), currentFlowNode.getId());
        return new Response<>(null, BizResponseEnums.SUCCESS, "退回成功");
    }

    public TicketTemplateAllBO getTicketTemplateAll(String ticketTemplateID){

        TicketTemplateAllBO ticketTemplateAllBO = new TicketTemplateAllBO();
        //获取 TicketTemplate
        TicketTemplate ticketTemplate = null;
        var tempOpt = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getTicketTemplateCode, ticketTemplateID).oneOpt();
        if (tempOpt.isPresent()) {
            ticketTemplate = tempOpt.get();
        } else {
            ticketTemplate = ticketTemplateService.lambdaQuery().isNull(TicketTemplate::getDeleteTime).eq(TicketTemplate::getId, ticketTemplateID).one();
        }
        if (ticketTemplate == null) {
            throw new RuntimeException(String.format("对应工单模版ID/CODE：%s 模版不存在", ticketTemplateID));
        }
        ticketTemplateAllBO.setTicketTemplate(ticketTemplate);
        //获取 TicketApp
        if (ticketTemplateAllBO.getTicketTemplate() != null) {
            TicketApp ticketApp = ticketAppService.lambdaQuery().isNull(TicketApp::getDeleteTime).eq(TicketApp::getId, ticketTemplateAllBO.getTicketTemplate().getAppId()).oneOpt().orElse(null);
            if (ticketApp == null) {
                throw new RuntimeException(String.format("对应APPID：%s 模版对应的业务不存在", ticketTemplateAllBO.getTicketTemplate().getAppId()));
            }
            ticketTemplateAllBO.setTicketApp(ticketApp);
        }
        //获取 TicketFlowTemplate
        QueryWrapper<TicketFlowTemplate> tFTWrapper = Wrappers.query();
        tFTWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFTWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowTemplate(ticketFlowTemplateMapper.selectOne(tFTWrapper));
        //获取 TicketFlowNodeTemplate
        QueryWrapper<TicketFlowNodeTemplate> tFNTWrapper = Wrappers.query();
        tFNTWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFNTWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowNodeTemplateList(flowNodeTemplateService.list(tFNTWrapper));
        //获取 TicketFlowNodeActionTemplate
        QueryWrapper<TicketFlowNodeActionTemplate> tFNATWrapper = Wrappers.query();
        tFNATWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFNATWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowNodeActionTemplateList(flowNodeActionTemplateService.list(tFNATWrapper));
        //获取 TicketFlowNodeRuleTemplate
        QueryWrapper<TicketFlowNodeRuleTemplate> tFNRTWrapper = Wrappers.query();
        tFNRTWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFNRTWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowNodeRuleTemplateList(ticketFlowNodeRuleTemplateMapper.selectList(tFNRTWrapper));
        //获取 ticketFlowNodeExecutorTemplate
        QueryWrapper<TicketFlowNodeExecutorTemplate> tFNETWrapper = Wrappers.query();
        tFNETWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFNETWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowNodeExecutorTemplateList(ticketFlowNodeExecutorTemplateMapper.selectList(tFNETWrapper));
        //获取 TicketFlowEventTemplate
        QueryWrapper<TicketFlowEventTemplate> tFETWrapper = Wrappers.query();
        tFETWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFETWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFlowEventTemplateList(ticketFlowEventTemplateMapper.selectList(tFETWrapper));
        //获取 TicketFormTemplate
        QueryWrapper<TicketFormTemplate> tFTTWrapper = Wrappers.query();
        tFTTWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFETWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFormTemplate(ticketFormTemplateMapper.selectOne(tFTTWrapper));
        //获取 TicketFormItemTemplate
        QueryWrapper<TicketFormItemTemplate> tFITTWrapper = Wrappers.query();
        tFITTWrapper.eq("ticket_template_id", ticketTemplate.getId());
        tFITTWrapper.isNull("delete_time");
        ticketTemplateAllBO.setTicketFormItemTemplateList(ticketFormItemTemplateMapper.selectList(tFITTWrapper));
        return ticketTemplateAllBO;
    }


    //构建工单数据
    public Response<TicketDataAllBO> buildTicketDataAllBO(TicketTemplateAllBO ticketTemplateAllBO, TicketDataStdDto ticketDataStdDto, TicketData ticketData, AccountInfo applyUser, String ticketFlowId){
        //模版数据
        var ticketApp = ticketTemplateAllBO.getTicketApp();
        var ticketTemplate = ticketTemplateAllBO.getTicketTemplate();
        var flowTemplate = ticketTemplateAllBO.getTicketFlowTemplate();
        var flowNodeTemplateList = ticketTemplateAllBO.getTicketFlowNodeTemplateList();
        var flowNodeActionTemplateList = ticketTemplateAllBO.getTicketFlowNodeActionTemplateList();
        var flowNodeRuleTemplateList = ticketTemplateAllBO.getTicketFlowNodeRuleTemplateList();
        var flowNodeExecutorTemplateList = ticketTemplateAllBO.getTicketFlowNodeExecutorTemplateList();
        var flowEventTemplateList = ticketTemplateAllBO.getTicketFlowEventTemplateList();
        var formTemplate = ticketTemplateAllBO.getTicketFormTemplate();
        var formItemTemplateList = ticketTemplateAllBO.getTicketFormItemTemplateList();
        //新数据
        TicketDataAllBO ticketDataAllBO = new TicketDataAllBO();

        //newTicketFormData 填充
        ticketDataAllBO.setTicketFormData(new TicketFormData(formTemplate, SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FORM_DATA), ticketData.getId()));

        Response<List<TicketFormItemData>> ticketFormItemDataListRsp;

        ticketFormItemDataListRsp = buildTicketFormItemDataList(ticketData.getId(), ticketDataAllBO.getTicketFormData(), formItemTemplateList, ticketDataStdDto.getFormItems());

        if (!ticketFormItemDataListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "构建表单组件数据失败:" + ticketFormItemDataListRsp.getMsg());
        }
        ticketDataAllBO.setTicketFormItemDataList(ticketFormItemDataListRsp.getData());


        //newTicketFlowData 填充
        ticketDataAllBO.setTicketFlowData(new TicketFlowData(flowTemplate, StringUtils.isNotEmpty(ticketFlowId) ? ticketFlowId : SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_DATA), ticketData.getId()));
        //newTicketFlowNodeDataList 填充
        Response<List<TicketFlowNodeData>> nodeListRsp = buildTicketFlowNodeDataList(ticketData.getId(), ticketDataAllBO.getTicketFlowData().getId(), ticketDataStdDto.getFormItems(), formItemTemplateList, flowNodeTemplateList, flowNodeRuleTemplateList, flowNodeExecutorTemplateList);
        if (!BizResponseEnums.SUCCESS.getCode().equals(nodeListRsp.getCode())) {
            return new Response<>(null, nodeListRsp.getEnum(), nodeListRsp.getMsg());
        }
        if (CollectionUtils.isEmpty(nodeListRsp.getData())) {
            return new Response<>(null, BizResponseEnums.SYSTEM_ERROR, "构建出来的流程节点为空");
        }
        ticketDataAllBO.setTicketFlowNodeDataList(nodeListRsp.getData());
        //newTicketFlowNodeActionDataList 填充
        Response<List<TicketFlowNodeActionData>> ticketFlowNodeActionDataListRsp = buildTicketFlowNodeActionDataList(
                ticketData.getId(),
                ticketDataAllBO.getTicketFlowData().getId(),
                flowNodeActionTemplateList,
                ticketDataAllBO.getTicketFlowNodeDataList()
        );
        if (!BizResponseEnums.SUCCESS.getCode().equals(ticketFlowNodeActionDataListRsp.getCode())) {
            return new Response<>(null, ticketFlowNodeActionDataListRsp.getEnum(), "构建审批节点动作数据失败:" + ticketFlowNodeActionDataListRsp.getMsg());
        }
        ticketDataAllBO.setTicketFlowNodeActionDataList(ticketFlowNodeActionDataListRsp.getData());
        //newTicketFlowNodeExecutorDataList 填充
        Response<List<TicketFlowNodeExecutorData>> ticketFlowNodeExecutorDataListRsp = buildTicketFlowNodeExecutorDataList(ticketData.getId(), ticketData.getInterfaceKey(), ticketDataAllBO.getTicketFormItemDataList(), applyUser, ticketDataAllBO.getTicketFlowNodeDataList(), flowNodeExecutorTemplateList, ticketDataStdDto.getFlowNodes());
        if (!ticketFlowNodeExecutorDataListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "构建审批节点执行人数据失败：" + ticketFlowNodeExecutorDataListRsp.getMsg());
        }
        ticketDataAllBO.setTicketFlowNodeExecutorDataList(ticketFlowNodeExecutorDataListRsp.getData());
        //newTicketFlowEventDataList 填充
        Response<List<TicketFlowEventData>> ticketFlowEventDataListRsp = buildTicketFlowNodeEventDataList(ticketData.getId(), ticketData.getCurrentNodeId(), ticketDataAllBO.getTicketFlowNodeDataList(), flowEventTemplateList);
        if (!ticketFlowEventDataListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "构建审批节点事件数据失败:" + ticketFlowEventDataListRsp.getMsg());
        }
        ticketDataAllBO.setTicketFlowEventDataList(ticketFlowEventDataListRsp.getData());

        //ticketData 更新
        var firstNodeData = ticketDataAllBO.getTicketFlowNodeDataList().get(0);
        List<TicketFlowNodeExecutorData> firstNodeDataExecutorList = ticketDataAllBO.getTicketFlowNodeExecutorDataList().stream().filter(x -> x.getTicketFlowNodeDataId().equals(firstNodeData.getId())).collect(Collectors.toList());

        ticketData.InitTicketData(
                JSONUtil.toJsonStr(applyUser),
                ticketApp,
                ticketTemplate,
                ticketDataStdDto,
                ticketDataAllBO.getTicketFlowData(),
                firstNodeData,
                ticketDataAllBO.getTicketFlowNodeDataList(),
                firstNodeDataExecutorList,
                ticketDataAllBO.getTicketFormItemDataList()
        );
        ticketDataAllBO.setTicketData(ticketData);
        return new Response<>(ticketDataAllBO, BizResponseEnums.SUCCESS, "成功");
    }


    private Response<List<TicketFlowNodeExecutorData>> buildTicketFlowNodeExecutorDataList(String ticketDataId, String interfaceKey, List<TicketFormItemData> ticketFormItemDataList, AccountInfo applyUser, List<TicketFlowNodeData> ticketFlowNodeDataList, List<TicketFlowNodeExecutorTemplate> flowNodeExecutorTemplateList, List<TicketFlowNodeStdDto> flowNodeStdDtoList){
        //Map<节点ID,List<执行人>>
        Map<String, List<TicketFlowNodeExecutorTemplate>> flowNodeExecutorTemplateMap = new HashMap<>();
        for (var executor : flowNodeExecutorTemplateList) {
            if (flowNodeExecutorTemplateMap.containsKey(executor.getTicketFlowNodeTemplateId())) {
                flowNodeExecutorTemplateMap.get(executor.getTicketFlowNodeTemplateId()).add(executor);
            } else {
                List<TicketFlowNodeExecutorTemplate> list = new ArrayList<>();
                list.add(executor);
                flowNodeExecutorTemplateMap.put(executor.getTicketFlowNodeTemplateId(), list);
            }
        }
        //Map<审批卡片,List<指定审批人>>
        Map<String, List<AccountInfo>> executorPointUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(flowNodeStdDtoList)) {
            for (var executor : flowNodeStdDtoList) {
                if (CollectionUtils.isNotEmpty(executor.getDealUsers())) {
                    List<AccountInfo> accountInfoList = new ArrayList<>();
                    for (var user : executor.getDealUsers()) {
                        TicketRemoteAccountDto accountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(user.getUserId(), user.getUserType());
                        if (accountDto == null) {
                            log.error(String.format("指定审批人：%s工单系统不存在", JSONObject.toJSONString(user)));
                        }
                        accountInfoList.add(new AccountInfo(accountDto.getSameOriginId(), user.getUserType(), user.getUserId(), user.getUserName()));
                    }
                    executorPointUserMap.put(executor.getTemplateId(), accountInfoList);
                }
            }
        }
        //Map<审批卡片,List<指定抄送人>>
        Map<String, List<AccountInfo>> executorPointccUserMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(flowNodeStdDtoList)) {
            for (var executor : flowNodeStdDtoList) {
                if (CollectionUtils.isNotEmpty(executor.getCcUsers())) {
                    List<AccountInfo> accountInfoList = new ArrayList<>();
                    for (var user : executor.getCcUsers()) {
                        TicketRemoteAccountDto accountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(user.getUserId(), user.getUserType());
                        if (accountDto == null) {
                            log.error(String.format("指定抄送人：%s工单系统不存在", JSONObject.toJSONString(user)));
                        }
                        accountInfoList.add(new AccountInfo(accountDto.getSameOriginId(), user.getUserType(), user.getUserId(), user.getUserName()));
                    }
                    executorPointccUserMap.put(executor.getTemplateId(), accountInfoList);
                }
            }
        }
        // 填充工单流程节点执行人
        List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataList = new ArrayList<>();
        for (var node : ticketFlowNodeDataList) {
            List<TicketFlowNodeExecutorTemplate> executorList = flowNodeExecutorTemplateMap.get(node.getTemplateId());
            if (CollectionUtils.isNotEmpty(executorList)) {
                for (var executor : executorList) {
                    TicketFlowNodeExecutorData ticketFlowNodeExecutorData = new TicketFlowNodeExecutorData(
                            executor,
                            SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA),
                            ticketDataId,
                            node.getId(),
                            node.getNodeName(),
                            applyUser,
                            ticketExecutorGroupService,
                            ticketAccountService,
                            executorPointUserMap.get(executor.getId()),
                            null
                    );
                    ticketFlowNodeExecutorDataList.add(ticketFlowNodeExecutorData);
                    //抄送人
                    if (executorPointccUserMap != null && CollectionUtils.isNotEmpty(executorPointccUserMap.get(executor.getId()))) {
                        TicketFlowNodeExecutorData ccExecutor = new TicketFlowNodeExecutorData(
                                executor,
                                SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_EXECUTOR_DATA),
                                ticketDataId,
                                node.getId(),
                                node.getNodeName(),
                                applyUser,
                                ticketExecutorGroupService,
                                ticketAccountService,
                                null,
                                executorPointccUserMap.get(executor.getId())
                        );
                        ccExecutor.setExecutorType(ExecutorTypeEnum.CE_MEMBER_LIST);
                        ticketFlowNodeExecutorDataList.add(ccExecutor);
                    }
                }
            }
        }
        return new Response<>(ticketFlowNodeExecutorDataList, BizResponseEnums.SUCCESS, "成功");
    }

    public void ExecuteCreateTicket(TicketDataAllBO ticketDataAllBO){
        //消息触达
        var now = new Date();
        var ticketData = ticketDataAllBO.getTicketData();
        var ticketFlowData = ticketDataAllBO.getTicketFlowData();
        var ticketFormItemDataList = ticketDataAllBO.getTicketFormItemDataList();
        if (ticketData == null || ticketData.getTicketMsgArriveType() == null) {
            return;
        }
        TicketApp ticketApp = appService.getById(ticketData.getAppId());
        switch (ticketData.getTicketMsgArriveType()) {
            case WECOM:
                //创建群聊
                if (ticketData.getTicketMsgBuildType() == TicketMsgBuildTypeEnum.APPLY_CREATE && ticketData.getApplyUser().contains("accountId") && StringUtils.isEmpty(ticketData.getWxChatGroupId())) {
                    doCreateQWGroup(ticketApp, ticketData, ticketFormItemDataList, null);
                }
                List<TicketFlowNodeExecutorData> hasSelfExecutorList = ticketFlowNodeExecutorDataService.lambdaQuery()
                        .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                        .eq(TicketFlowNodeExecutorData::getTicketDataId, ticketData.getId())
                        .eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, ticketData.getCurrentNodeId())
                        .in(TicketFlowNodeExecutorData::getExecutorType, Arrays.asList(ExecutorTypeEnum.CA_SELF, ExecutorTypeEnum.CE_SELF)).list();

                if (CollectionUtils.isNotEmpty(hasSelfExecutorList)) {
                    //发送消息
                    AccountInfo applyUser = AccountInfo.ToAccountInfo(ticketData.getApplyUser());
                    var applyUserWx = ticketAccountService.getTicketRemoteAccountByIdAndType(applyUser.getAccountId(), applyUser.getAccountType());
                    if (applyUserWx == null || StringUtils.isEmpty(applyUserWx.getQywxId())) {
                        return;
                    }

                    NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
                    String titleTemplate = "你发起的{{ticket_name}}已进入审批环节"
                            .replace("{{ticket_name}}", ticketData.getTicketName());
                    qwcardMsg.setTitle(titleTemplate);
                    String description = "申请时间：{{apply_time}}   业务：{{app_name}}"
                            .replace("{{apply_time}}", DateUtil.formatDateTime(ticketData.getCreateTime()))
                            .replace("{{app_name}}", ticketApp.getAppName());
                    qwcardMsg.setDescription(description);

                    Map<String, String> linkMap = new HashMap<>();
                    String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketData.getId();
                    linkMap.put("工单详情", jumpUrl);
                    qwcardMsg.setLinkKeyMap(linkMap);

                    List<String> userIdList = new ArrayList<>();
                    userIdList.add(applyUserWx.getQywxId());
                    qwcardMsg.setUserIdList(userIdList);
                    qwcardMsg.setJumpUrl(jumpUrl);
                    List<NotificationService.KvContent> kvContentList = BuildContentList(ticketFormItemDataList, jumpUrl);
                    qwcardMsg.setKvContentList(kvContentList);
                    NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
                    TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
                    approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
                    approveDetail.setTicketDataId(ticketData.getId());
                    approveDetail.setTicketFlowNodeDataId(ticketData.getCurrentNodeId());
                    approveDetail.setDealUserId(applyUserWx.getUserId());
                    approveDetail.setDealUserType(applyUserWx.getUserType());
                    approveDetail.setDealUserName(applyUserWx.getUserName());
                    approveDetail.setDealType(ApproveDealTypeEnum.SEND);
                    approveDetail.setDealOpinion("");
                    approveDetail.setCreateBy(ticketData.getApplyUser());
                    approveDetail.setUpdateBy(ticketData.getApplyUser());
                    approveDetail.setCreateTime(now);
                    approveDetail.setUpdateTime(now);
                    var ccRes = ticketFlowNodeApproveDetailService.save(approveDetail);
                    if (ccRes == false) {
                        log.error("卡片通知数据 记录保存异常");
                    }
                }
                break;
            case DINGTALK:
                throw new NotImplementedException("未实现钉钉消息触达类型");
            case NULL:
                break;
        }
    }

    public Response<String> SaveCreateTicket(TicketDataAllBO ticketDataAllBO){

        var tranResult = transactionTemplate.execute(action -> {
            int exeCount = 0;
            exeCount = ticketDataMapper.updateById(ticketDataAllBO.getTicketData());
            if (exeCount != 1) {
                return false;
            }
            exeCount = ticketFlowDataMapper.insert(ticketDataAllBO.getTicketFlowData());
            if (exeCount != 1) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFlowNodeDataList())) {
                for (var item : ticketDataAllBO.getTicketFlowNodeDataList()) {
                    exeCount = ticketFlowNodeDataMapper.insert(item);
                    if (exeCount != 1) {
                        return false;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFlowNodeExecutorDataList())) {
                for (var item : ticketDataAllBO.getTicketFlowNodeExecutorDataList()) {
                    exeCount = ticketFlowNodeExecutorDataMapper.insert(item);
                    if (exeCount != 1) {
                        return false;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFlowEventDataList())) {
                for (var item : ticketDataAllBO.getTicketFlowEventDataList()) {
                    exeCount = ticketFlowEventDataMapper.insert(item);
                    if (exeCount != 1) {
                        return false;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFlowNodeActionDataList())) {
                for (var item : ticketDataAllBO.getTicketFlowNodeActionDataList()) {
                    exeCount = ticketFlowNodeActionDataMapper.insert(item);
                    if (exeCount != 1) {
                        return false;
                    }
                }
            }
            exeCount = ticketFormDataMapper.insert(ticketDataAllBO.getTicketFormData());
            if (exeCount != 1) {
                return false;
            }
            if (CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFormItemDataList())) {
                for (var item : ticketDataAllBO.getTicketFormItemDataList()) {
                    exeCount = ticketFormItemDataMapper.insert(item);
                    if (exeCount != 1) {
                        return false;
                    }
                }
            }
            return true;
        });
        if (tranResult == false) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "数据保存失败");
        }
        return new Response<>(ticketDataAllBO.getTicketData().getId(), BizResponseEnums.SUCCESS, "成功");

    }

    @Override
    @ApiDoc(value = "创建动态工单数据", description = "创建动态工单数据")
    public Response<String> createTicketDynamic(TicketDataDynamicDto dynamicDto, String userType, String userId, String userName){
        //数据校验
        if (dynamicDto == null || dynamicDto.getId() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        TicketData updateTicketData = this.baseMapper.selectById(dynamicDto.getId());// for update
        //数据校验
        Response response = TicketDataDynamicDtoParamCheck(dynamicDto, updateTicketData);
        if (!response.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            return response;
        }
        // 实例化新增数据对象
        TicketFormData newTicketFormData = new TicketFormData();// for insert
        List<TicketFormItemData> newTicketFormItemDataList = new ArrayList<>();//for insert
        TicketFlowData newTicketFlowData = new TicketFlowData();//for insert
        List<TicketFlowNodeData> newTicketFlowNodeDataList = new ArrayList<>();//for insert
        List<TicketFlowNodeExecutorData> newTicketFlowNodeExecutorDataList = new ArrayList<>();//for insert
        // 数据填充
        TicketDataDynamicDtoDataFill(dynamicDto, updateTicketData, newTicketFormData, newTicketFormItemDataList, newTicketFlowData, newTicketFlowNodeDataList, newTicketFlowNodeExecutorDataList);
        //事务保存
        var result = transactionTemplate.execute(action -> {
            //ticketData todo 带data状态Init
            int count = ticketDataMapper.updateById(updateTicketData);
            if (count != 1) {
                return false;
            }
            ticketFlowDataMapper.insert(newTicketFlowData);
            for (var item : newTicketFlowNodeDataList) {
                ticketFlowNodeDataMapper.insert(item);
            }
            for (var item : newTicketFlowNodeExecutorDataList) {
                ticketFlowNodeExecutorDataMapper.insert(item);
            }
            ticketFormDataMapper.insert(newTicketFormData);
            for (var item : newTicketFormItemDataList) {
                ticketFormItemDataMapper.insert(item);
            }
            return true;
        });
        if (result) {
            return new Response<>(dynamicDto.getId(), BizResponseEnums.SUCCESS, "工单创建成功");
        }
        return new Response<>(dynamicDto.getId(), BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单创建失败");
    }

    @Override
    @ApiDoc(value = "通过businessKey查询工单状态", description = "通过businessKey查询工单状态")
    public Response<List<TicketDataStatusDto>> selectTicketStatusByBusinessKey(String businessKey, String ticketStatus, Integer limit){

        if (StrUtil.isBlank(businessKey)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "businessKey 不能为空");
        }

        if (limit != null && limit < 0) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "limit 不能小于零");
        }

        LambdaQueryChainWrapper<TicketData> query = this.ticketDataService.lambdaQuery()
                .select(TicketData::getId, TicketData::getTicketStatus, TicketData::getTicketBusinessKey)
                .isNull(TicketData::getDeleteTime)
                .eq(TicketData::getTicketBusinessKey, businessKey)
                .orderByDesc(TicketData::getUpdateTime)
                .notIn(TicketData::getTicketStatus,
                        TicketDataStatusEnum.INIT.getCode(),
                        TicketDataStatusEnum.DRAFT.getCode());

        if (StrUtil.isNotBlank(ticketStatus)) {
            query = query.eq(TicketData::getTicketStatus, ticketStatus);
        }
        if (limit != null && limit > 0) {
            query = query.last("LIMIT " + limit);
        }

        List<TicketData> list = query.list();

        if (CollUtil.isEmpty(list)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("businessKey：{%s} 未查询到工单数据", businessKey));
        }
        List<TicketDataStatusDto> res = BeanUtil.copyToList(list, TicketDataStatusDto.class);
        return Response.success(res);
    }

    @Override
    @ApiDoc(value = "通过businessKey查询工单", description = "通过businessKey查询工单")
    public Response<List<TicketDataDto>> selectTicketByBusinessKey(String businessKey, String templateIdOrCode, Integer limit){

        if (StrUtil.isBlank(businessKey) || StrUtil.isBlank(templateIdOrCode)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "businessKey or templateCode 不能为空");
        }
        if (limit != null && limit < 0) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "limit 不能小于零");
        }

        Optional<String> tmpId = ticketTemplateService.lambdaQuery()
                .select(TicketTemplate::getId)
                .isNull(TicketTemplate::getDeleteTime)
                .and(wrapper -> wrapper
                        .eq(TicketTemplate::getTicketTemplateCode, templateIdOrCode)
                        .or()
                        .eq(TicketTemplate::getId, templateIdOrCode)
                )
                .oneOpt()
                .map(TicketTemplate::getId);
        if (!tmpId.isPresent()) {
            log.error("selectTicketByBusinessKey templateIdOrCode {} 对应工单模板不存在", templateIdOrCode);
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "templateIdOrCode: " + templateIdOrCode + " 工单模板不存在");
        }

        LambdaQueryChainWrapper<TicketData> query = this.ticketDataService.lambdaQuery()
                //.select(TicketData::getId, TicketData::getTicketStatus, TicketData::getTicketBusinessKey)
                .isNull(TicketData::getDeleteTime)
                .eq(TicketData::getTicketBusinessKey, businessKey)
                .eq(TicketData::getTemplateId, tmpId.get());

        if (limit != null && limit > 0) {
            query = query.last("LIMIT " + limit);
        }
        List<TicketData> list = query.list();
        if (CollUtil.isEmpty(list)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("businessKey：{%s} 未查询到工单数据", businessKey));
        }
        List<TicketDataDto> res = BeanUtil.copyToList(list, TicketDataDto.class);
        return Response.success(res);
    }

    @Override
    @ApiDoc(value = "通过工单号查询工单所有数据", description = "通过工单号查询工单所有数据")
    public Response<TicketDataDto> selectFullTicketDataById(ReqParam reqParam){

        String ticketDataId = null;
        //数据查询
        //TicketData
        TicketData ticketDataDbo = null;
        if (StringUtils.isEmpty(reqParam.getId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "ID号不能为空");
        }
        if (StringUtils.isEmpty(reqParam.getIdType())) {
            ticketDataId = reqParam.getId();
            if (StringUtils.isBlank(ticketDataId)) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单号不能为空");
            }
            Optional<TicketData> ticketDataOpt = this.ticketDataService.lambdaQuery()
                    .isNull(TicketData::getDeleteTime)
                    .eq(TicketData::getId, ticketDataId)
                    .oneOpt();
            if (!ticketDataOpt.isPresent()) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在", ticketDataId));
            }
            ticketDataDbo = ticketDataOpt.get();
        }
        if ("BusiKey".equals(reqParam.getIdType())) {
            Optional<TicketData> ticketDataOpt = this.ticketDataService.lambdaQuery()
                    .isNull(TicketData::getDeleteTime)
                    .eq(TicketData::getTicketBusinessKey, reqParam.getId())
                    .eq(TicketData::getTicketStatus, TicketDataStatusEnum.APPLYING)
                    .oneOpt();
            if (!ticketDataOpt.isPresent()) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单业务标识：{%s} 工单数据不存在", reqParam.getId()));
            }
            ticketDataDbo = ticketDataOpt.get();
        }
        if (ticketDataDbo == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：%s TYPE:%s 工单数据不存在", reqParam.getId(), reqParam.getIdType()));
        }

        //TicketApp
        TicketApp ticketAppDbo = ticketAppService.lambdaQuery().eq(TicketApp::getId, ticketDataDbo.getAppId()).isNull(TicketApp::getDeleteTime).one();
        if (ticketAppDbo == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} appid：{%s} 不存在工单应用", ticketDataId, ticketDataDbo.getAppId()));
        }
        //TicketFlowData
        List<TicketFlowData> ticketFlowDataDboList = ticketFlowDataService.lambdaQuery().eq(TicketFlowData::getTicketDataId, ticketDataId).isNull(TicketFlowData::getDeleteTime).list();
        if (ticketFlowDataDboList == null || ticketFlowDataDboList.size() != 1) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程数据", ticketDataId));
        }
        //TicketFlowNodeData
        List<TicketFlowNodeData> ticketFlowNodeDataDboList = ticketFlowNodeDataService.lambdaQuery().eq(TicketFlowNodeData::getTicketDataId, ticketDataId).isNull(TicketFlowNodeData::getDeleteTime).list();
        if (ticketFlowNodeDataDboList == null || ticketFlowNodeDataDboList.size() == 0) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在流程节点数据", ticketDataId));
        }
        HashMap<String, String> nextIdToIdMap = new LinkedHashMap<>();
        HashMap<String, TicketFlowNodeData> idFlowNodeMap = new LinkedHashMap<>();
        for (TicketFlowNodeData item : ticketFlowNodeDataDboList) {
            nextIdToIdMap.putIfAbsent(item.getPreNodeId(), item.getId());
            idFlowNodeMap.putIfAbsent(item.getId(), item);
        }
        for (TicketFlowNodeData item : ticketFlowNodeDataDboList) {
            if (!NodeStatusEnum.APPROVING.equals(item.getNodeStatus()) && !Objects.equals(ticketDataDbo.getCurrentNodeId(), item.getId())) {
                if ("ignore".equals(item.getNodeName()) || ((!"-1".equals(item.getPreNodeId())) && "开始".equals(item.getNodeName()))) {
                    if (idFlowNodeMap.containsKey(item.getId()) && nextIdToIdMap.containsKey(item.getId()) && idFlowNodeMap.containsKey(nextIdToIdMap.get(item.getId()))) {
                        idFlowNodeMap.remove(item.getId());
                        idFlowNodeMap.get(nextIdToIdMap.get(item.getId())).setPreNodeId(item.getPreNodeId());
                    }
                }
            }
        }
        ticketFlowNodeDataDboList = new ArrayList<>(idFlowNodeMap.values());

        //TicketFlowNodeExecutorData
        List<TicketFlowNodeExecutorData> ticketFlowNodeExecutorDataDboList = ticketFlowNodeExecutorDataService.lambdaQuery().eq(TicketFlowNodeExecutorData::getTicketDataId, ticketDataId).isNull(TicketFlowNodeExecutorData::getDeleteTime).list();
        //TicketFlowNodeActionData
        List<TicketFlowNodeActionData> ticketFlowNodeActionDataList = ticketFlowNodeActionDataService.lambdaQuery().eq(TicketFlowNodeActionData::getTicketDataId, ticketDataId).isNull(TicketFlowNodeActionData::getDeleteTime).list();
        //TicketFormData
        List<TicketFormData> ticketFormDataDboList = ticketFormDataService.lambdaQuery().eq(TicketFormData::getTicketDataId, ticketDataId).isNull(TicketFormData::getDeleteTime).list();
        if (ticketFormDataDboList == null || ticketFormDataDboList.size() != 1) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在表单数据", ticketDataId));
        }
        //TicketFlowNodeApproveDetail
        List<TicketFlowNodeApproveDetail> ticketFlowNodeApproveDetailDboList = ticketFlowNodeApproveDetailService.lambdaQuery().eq(TicketFlowNodeApproveDetail::getTicketDataId, ticketDataId).isNull(TicketFlowNodeApproveDetail::getDeleteTime).list();
        //TicketFormItemData
        List<TicketFormItemData> ticketFormItemDataDboList = ticketFormItemDataService.lambdaQuery().eq(TicketFormItemData::getTicketDataId, ticketDataId).isNull(TicketFormItemData::getDeleteTime).list();
        if (ticketFormItemDataDboList == null || ticketFormItemDataDboList.size() == 0) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 不存在表单项数据", ticketDataId));
        }
        //数据转换
        Response<TicketDataDto> ticketDataDtoResponse = ToTicketDataDto(ticketAppDbo, ticketDataDbo, ticketFormDataDboList.get(0), ticketFormItemDataDboList, ticketFlowDataDboList.get(0), ticketFlowNodeDataDboList, ticketFlowNodeActionDataList, ticketFlowNodeExecutorDataDboList, ticketFlowNodeApproveDetailDboList, reqParam);

        return ticketDataDtoResponse;
    }

    @Override
    @ApiDoc(value = "通过工单号查询工单本身数据", description = "通过工单号查询工单本身数据")
    public TicketData selectTicketDataById(@RequestParam(value = "工单号", description = "工单号") String ticketDataId){

        return this.getById(ticketDataId);
    }

    @Override
    public List<TicketData> selectTicketDataById(List<String> ticketDataIdList){

        LambdaQueryWrapper<TicketData> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(TicketData::getId, ticketDataIdList);

        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @ApiDoc(value = "分页查询工单数据", description = "分页查询工单数据")
    public Page<TicketDataDto> selectTicketDataPage(TicketDataDto ticketDataDto, int pageIndex, int pageSize){

        return null;
    }

    @Override
    public Response<List<TicketDataListResponseDto>> selectTicketDataList(TicketDataListRequestDto ticketDataListRequestDto){

        queryPreHandleForDataList(ticketDataListRequestDto);
        if (ticketDataListRequestDto.isNeedCount()) {
            //需要查询总条数
            startPage();
        } else {
            //不需要查询总条数
            startPageNotCount();
        }
        //查询数据
        List<TicketDataListResponseDto> ticketDataListResponseList = this.baseMapper.selectTicketDataList(ticketDataListRequestDto);
        queryPostHandleForDataList(ticketDataListResponseList);
        return new Response<>().success(ticketDataListResponseList);
    }

    @Override
    public Response<List<TicketDataListResponseDto>> selectTicketDataList(TicketDataListRequestDto ticketDataListRequestDto, Integer pageNum, Integer pageSize){

        queryPreHandleForDataList(ticketDataListRequestDto);
        if (ticketDataListRequestDto.isNeedCount()) {
            //需要查询总条数
            startPage(pageNum, pageSize);
        } else {
            //不需要查询总条数
            startPageNotCount(pageNum, pageSize);
        }
        //查询数据
        List<TicketDataListResponseDto> ticketDataListResponseList = this.baseMapper.selectTicketDataList(ticketDataListRequestDto);
        queryPostHandleForDataList(ticketDataListResponseList);
        return new Response<>().success(ticketDataListResponseList);
    }

    /**
     * 查询工单前对参数的预处理
     *
     * @param advancedQueryDto
     */
    public void advancedQueryPreHandle(AdvancedQueryDto advancedQueryDto){
        //添加当前操作人，方便关联查询
        String currentUsername = SecurityUtils.getOriginUserInfoForSearch();
        advancedQueryDto.setCurrentUserInfo(currentUsername);

        //根据模版分類查詢模版id
        List<String> categoryTemplateIdList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(advancedQueryDto.getCategoryIdList())) {
            List<TicketCategory> categoryList = ticketCategoryService.lambdaQuery()
                    .isNull(TicketCategory::getDeleteTime)
                    .in(TicketCategory::getId, advancedQueryDto.getCategoryIdList())
                    .list();
            if (CollectionUtils.isNotEmpty(categoryList)) {
                categoryTemplateIdList = categoryList.stream()
                        .filter(it -> StringUtils.isNotEmpty(it.getTemplateId()))
                        .map(it -> it.getTemplateId())
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isEmpty(categoryTemplateIdList)) {
                throw new ServiceException("所选中的分类下无关联模版");
            }

        }
        String templateId = advancedQueryDto.getTemplateId();
        if (StringUtils.isNotEmpty(templateId) && CollectionUtils.isNotEmpty(categoryTemplateIdList) && !categoryTemplateIdList.contains(templateId)) {
            throw new ServiceException(String.format("所选模版(id:%s)不在分类里面", templateId));
        }
        if (StringUtils.isEmpty(templateId) && CollectionUtils.isNotEmpty(categoryTemplateIdList)) {
            advancedQueryDto.setTemplateIdList(categoryTemplateIdList);
        }
        //工单状态
        if (StringUtils.isNotBlank(advancedQueryDto.getTicketStatusStr())) {
            List<String> ticketStatusList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(advancedQueryDto.getTicketStatusList())) {
                ticketStatusList = advancedQueryDto.getTicketStatusList();
            }
            List<String> ticketStatusStrList = Arrays.asList(advancedQueryDto.getTicketStatusStr().split(","));
            if (CollectionUtils.isNotEmpty(ticketStatusStrList)) {
                ticketStatusList.addAll(ticketStatusStrList);
            }
            ticketStatusList = ticketStatusList.stream().distinct().collect(Collectors.toList());
            advancedQueryDto.setTicketStatusList(ticketStatusList);
            advancedQueryDto.setTicketStatusStr("");
        }
        Date updateStartTime = advancedQueryDto.getUpdateStartTime();
        Date updateEndTime = advancedQueryDto.getUpdateEndTime();
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime) && Objects.isNull(advancedQueryDto.getTicketDataId())) {
            throw new ServiceException("更新时间或者工单id不能都为空");
        }
        if ((Objects.isNull(updateStartTime) && Objects.nonNull(updateEndTime)) || (Objects.nonNull(updateStartTime) && Objects.isNull(updateEndTime))) {
            throw new ServiceException("更新开始时间和结束时间必须同时传值");
        }
        if (Objects.nonNull(updateStartTime) && Objects.nonNull(updateEndTime) && (updateStartTime.after(updateEndTime) || DateUtil.between(updateStartTime, updateEndTime, DateUnit.DAY) > 30)) {
            throw new ServiceException("更新时间的终止时间比开始时间最多晚30天");
        }
    }

    @Override
    public Response<List<TicketFormItemValues>> advancedSelectTicketDataList(AdvancedQueryDto advancedQueryDto){

        try {
            //设置查询前置条件
            advancedQueryPreHandle(advancedQueryDto);
            /**
             *
             * 1、生成最高版本的模板项<表单项id值,表单项id值对应的列名>映射itemIdColMap
             * 2、生成 <表单项id,表单项类型>itemIdTypeMap
             *
             */
            String ticketTemplateId = advancedQueryDto.getTemplateId();
            if (StringUtils.isEmpty(ticketTemplateId)) {
                //开始分页
                startPageNotCount();
                return new Response<>().success(ticketFormItemValuesService.queryTicketDataListResponseDtoList(advancedQueryDto, null));
            }
            List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = iTicketFormItemIdColMappingService.lambdaQuery().eq(TicketFormItemIdColMapping::getTicketTemplateId, ticketTemplateId).list();
            Response<Map<String, String>> itemIdColMapResponse = TicketDataServiceInner.getItemIdColMap(ticketFormItemIdColMappingList);
            if (!itemIdColMapResponse.isSuccess())
                return Response.error(BizResponseEnums.getEnumByCode(itemIdColMapResponse.getCode()), itemIdColMapResponse.getMsg());
            Map<String, String> itemIdColMap = itemIdColMapResponse.getData();
            List<TicketFormItemTemplate> ticketFormItemTemplateList = iTicketFormItemTemplateService.lambdaQuery().eq(TicketFormItemTemplate::getTicketTemplateId, ticketTemplateId).list();
            Map<String, FormItemTypeEnum> itemIdTypeMap = new HashMap();
            if (ObjectHelper.isNotEmpty(ticketFormItemTemplateList)) {
                ticketFormItemTemplateList.stream().forEach(it -> itemIdTypeMap.put(it.getId(), it.getItemType()));
            }
            /**
             * 组装List<TicketFormItemAttriDto> ticketFormItemAttriDtoList；
             * TicketFormItemAttriDto为{formItemId,formItemValue,formItemType}
             */
            Response<List<TicketFormItemAttriDto>> ticketFormItemAttriDtoListResponse = TicketDataServiceInner.getTicketFormItemAttriDtoList(advancedQueryDto, itemIdColMap, itemIdTypeMap);
            if (!ticketFormItemAttriDtoListResponse.isSuccess())
                return Response.error(BizResponseEnums.getEnumByCode(ticketFormItemAttriDtoListResponse.getCode()), ticketFormItemAttriDtoListResponse.getMsg());
            List<TicketFormItemAttriDto> ticketFormItemAttriDtoList = ticketFormItemAttriDtoListResponse.getData();

            //开始分页
            startPageNotCount();
            return new Response<>().success(ticketFormItemValuesService.queryTicketDataListResponseDtoList(advancedQueryDto, ticketFormItemAttriDtoList));
        } catch (Exception e) {
            log.error("查询异常：{}", e.getMessage());
            return Response.error(BizResponseEnums.QUERY_ERROR, e.getMessage());
        }
    }

    @Override
    public Response<Integer> advancedSelectTicketDataCount(AdvancedQueryDto advancedQueryDto){
        //设置查询前置条件
        advancedQueryPreHandle(advancedQueryDto);
        /**
         *
         * 1、生成最高版本的模板项<表单项id值,表单项id值对应的列名>映射itemIdColMap
         * 2、生成 <表单项id,表单项类型>itemIdTypeMap
         *
         */
        String ticketTemplateId = advancedQueryDto.getTemplateId();
        if (StringUtils.isEmpty(ticketTemplateId)) {
            return new Response<Integer>().success(queryTicketDataCount(advancedQueryDto, null));
        }
        List<TicketFormItemIdColMapping> ticketFormItemIdColMappingList = iTicketFormItemIdColMappingService.lambdaQuery().eq(TicketFormItemIdColMapping::getTicketTemplateId, ticketTemplateId).list();
        Response<Map<String, String>> itemIdColMapResponse = TicketDataServiceInner.getItemIdColMap(ticketFormItemIdColMappingList);
        if (!itemIdColMapResponse.isSuccess())
            return Response.error(BizResponseEnums.getEnumByCode(itemIdColMapResponse.getCode()), itemIdColMapResponse.getMsg());
        Map<String, String> itemIdColMap = itemIdColMapResponse.getData();
        List<TicketFormItemTemplate> ticketFormItemTemplateList = iTicketFormItemTemplateService.lambdaQuery().eq(TicketFormItemTemplate::getTicketTemplateId, ticketTemplateId).list();
        Map<String, FormItemTypeEnum> itemIdTypeMap = new HashMap();
        if (ObjectHelper.isNotEmpty(ticketFormItemTemplateList)) {
            ticketFormItemTemplateList.stream().forEach(it -> itemIdTypeMap.put(it.getId(), it.getItemType()));
        }
        /**
         * 组装List<TicketFormItemAttriDto> ticketFormItemAttriDtoList；
         * TicketFormItemAttriDto为{formItemId,formItemValue,formItemType}
         */
        Response<List<TicketFormItemAttriDto>> ticketFormItemAttriDtoListResponse = TicketDataServiceInner.getTicketFormItemAttriDtoList(advancedQueryDto, itemIdColMap, itemIdTypeMap);
        if (!ticketFormItemAttriDtoListResponse.isSuccess())
            return Response.error(BizResponseEnums.getEnumByCode(ticketFormItemAttriDtoListResponse.getCode()), ticketFormItemAttriDtoListResponse.getMsg());
        List<TicketFormItemAttriDto> ticketFormItemAttriDtoList = ticketFormItemAttriDtoListResponse.getData();

        return new Response<Integer>().success(queryTicketDataCount(advancedQueryDto, ticketFormItemAttriDtoList));
    }


    private Integer queryTicketDataCount(AdvancedQueryDto advancedQueryDto, List<TicketFormItemAttriDto> ticketFormItemAttriDtoList){

        LambdaQueryWrapper<TicketFormItemValues> lambdaQueryWrapper = TicketDataServiceInner.getTicketFormItemValuesWhereWrapper(advancedQueryDto, ticketFormItemAttriDtoList);
        Integer ticketDataCount = ticketFormItemValuesMapper.selectCount(lambdaQueryWrapper);
        return ticketDataCount;
    }

    public Response<List<TicketDataListResponseDto>> advancedQueryPostHandle(List<TicketFormItemValues> ticketFormItemValuesList){

        if (CollUtil.isEmpty(ticketFormItemValuesList)) {
            return Response.success(new ArrayList<>());
        }
        List<TicketDataListResponseDto> ticketDataListResponseDtoList = ticketFormItemValuesList.stream().map(ticketFormItemValues -> {
            TicketDataListResponseDto ticketDataListResponseDto = new TicketDataListResponseDto().toTicketDataListResponseDto(ticketFormItemValues);
            ticketDataListResponseDto.setApplyUser(accountReturnComponent.toAccountInfoStrForFront(ticketDataListResponseDto.getApplyUser()));
            if (ticketDataListResponseDto.getTicketStatus() == TicketDataStatusEnum.APPLYING && ticketDataListResponseDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
                ticketDataListResponseDto.setShowReminderButton(Boolean.TRUE);
                ticketDataListResponseDto.setShowFollowButton(StrUtil.isBlank(ticketDataListResponseDto.getWxChatGroupId()));
            }
            return ticketDataListResponseDto;
        }).collect(Collectors.toList());
        return Response.success(ticketDataListResponseDtoList);
    }

    /**
     * 给ticket_data表的字段tags赋值
     */
    @Override
    public void ticketDataTagsSet(){

        List<TicketTemplate> ticketTemplateList = ticketTemplateService.lambdaQuery()
                .eq(TicketTemplate::getTicketAgingFlag, YESNOEnum.YES)
                .list();
        if (ObjectHelper.isEmpty(ticketTemplateList)) {
            log.info("没有'工单支持时效标识'为'是'的模板 ");
            return;
        }
        /**
         * 获取所有工单支持时效标识为yes的模板下超时的工单
         */
        List<TicketData> allOverTimeTicketDataList = new ArrayList<>();
        List<TicketFormItemValues> allOverTimeTicketFormItemValuesList = new ArrayList<>();
        ticketTemplateList.stream().forEach(ticketTemplate -> {
            String ticketTemplateId = ticketTemplate.getId();
            if (ObjectHelper.isEmpty(ticketTemplate.getTicketAgingTime()) && ObjectHelper.isEmpty(ticketTemplate.getTicketDealTime())) {
                log.info(String.format("此模板(%s)未设置工单创建超时时长和工单处理超时时长 ", ticketTemplateId));
                return;
            }
            //查询每个模板下超时的工单
            List<TicketData> overTimeTicketDataList = getOverTimeTicketDataList(ticketTemplateId, ticketTemplate.getTicketAgingTime(), ticketTemplate.getTicketDealTime());
            if (ObjectHelper.isNotEmpty(overTimeTicketDataList)) {
                allOverTimeTicketDataList.addAll(overTimeTicketDataList);
            }
        });
        if (ObjectHelper.isNotEmpty(allOverTimeTicketDataList)) {
            List<String> allOverTimeIdList = allOverTimeTicketDataList.stream().map(it -> it.getId()).collect(Collectors.toList());
            List<TicketFormItemValues> ticketFormItemValuesList = ticketFormItemValuesService.lambdaQuery()
                    .in(TicketFormItemValues::getTicketDataId, allOverTimeIdList)
                    .select(TicketFormItemValues::getId,
                            TicketFormItemValues::getTicketDataId,
                            TicketFormItemValues::getTags,
                            TicketFormItemValues::getUpdateTime,
                            TicketFormItemValues::getUpdateBy
                    )
                    .list();
            if (ObjectHelper.isNotEmpty(ticketFormItemValuesList)) {
                Map<String, TicketData> allOverTimeTicketDataMapList = new HashMap<>();
                allOverTimeTicketDataList.stream().forEach(it -> {
                    allOverTimeTicketDataMapList.put(it.getId(), it);
                });
                ticketFormItemValuesList.stream().forEach(it -> {
                    String ticketDataId = it.getTicketDataId();
                    String tgivTags = it.getTags();
                    TicketData ticketData = allOverTimeTicketDataMapList.get(ticketDataId);
                    String tdTags = ticketData.getTags();
                    if (ObjectHelper.isNotEmpty(tdTags) && !tdTags.equals(tgivTags)) {
                        it.setTags(tdTags);
                        allOverTimeTicketFormItemValuesList.add(it);
                    }
                });
            }
        }
        /**
         * 批量更新工单tags字段
         */
        transactionTemplate.executeWithoutResult(action -> {
            if (ObjectHelper.isNotEmpty(allOverTimeTicketDataList)
                    && !saveOrUpdateBatch(allOverTimeTicketDataList))
            {
                String formatError = String.format("同步保存allOverTimeTicketDataList(size:%s)异常", allOverTimeTicketDataList.size());
                log.error(formatError);
                throw new RuntimeException(formatError);
            }
            if (ObjectHelper.isNotEmpty(allOverTimeTicketFormItemValuesList)
                    && !ticketFormItemValuesService.saveOrUpdateBatch(allOverTimeTicketFormItemValuesList))
            {
                String formatError = String.format("同步保存allOverTimeTicketFormItemValuesList(size:%s)异常", allOverTimeTicketFormItemValuesList.size());
                log.error(formatError);
                throw new RuntimeException(formatError);
            }
        });
    }

    /**
     * 查询每个模板下超时的工单
     *
     * @param ticketTemplateId
     * @return
     */
    private List<TicketData> getOverTimeTicketDataList(String ticketTemplateId, Integer ticketAgingTime, Integer ticketDealTime){
        /**
         * 组装查询条件：查询2倍时效内的工单数据
         */
        List<TicketData> overTimeTicketDataList = new ArrayList<>();
        Timestamp agingStartTimestampTemp = null;
        Timestamp dealStartTimestampTemp = null;
        LocalDateTime currentLocalDateTime = LocalDateTime.now();
        if (ObjectHelper.isNotEmpty(ticketAgingTime) && ticketAgingTime != 0) {
            Integer agingHours = ticketAgingTime * 2;
            //当前时间减去agingHours小时
            LocalDateTime agingHoursAgo = currentLocalDateTime.minusHours(agingHours);
            agingStartTimestampTemp = Timestamp.valueOf(agingHoursAgo);
        }
        if (ObjectHelper.isNotEmpty(ticketDealTime) && ticketDealTime != 0) {
            Integer dealHours = ticketDealTime * 2;
            //当前时间减去dealHours小时
            LocalDateTime dealHoursAgo = currentLocalDateTime.minusHours(dealHours);
            dealStartTimestampTemp = Timestamp.valueOf(dealHoursAgo);
        }
        final Timestamp agingStartTimestamp = agingStartTimestampTemp;
        final Timestamp dealStartTimestamp = dealStartTimestampTemp;
        LambdaQueryWrapper<TicketData> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TicketData::getTemplateId, ticketTemplateId);
        if (ObjectHelper.isNotEmpty(agingStartTimestamp) && ObjectHelper.isEmpty(dealStartTimestamp)) {
            lambdaQueryWrapper.ge(TicketData::getCreateTime, agingStartTimestamp);
        }
        if (ObjectHelper.isNotEmpty(dealStartTimestamp) && ObjectHelper.isEmpty(agingStartTimestamp)) {
            lambdaQueryWrapper.ge(TicketData::getUpdateTime, dealStartTimestamp);
        }
        if (ObjectHelper.isNotEmpty(agingStartTimestamp) && ObjectHelper.isNotEmpty(dealStartTimestamp)) {
            lambdaQueryWrapper.and(LambdaQueryWrapper ->
                    LambdaQueryWrapper.ge(TicketData::getCreateTime, agingStartTimestamp)
                            .or()
                            .ge(TicketData::getUpdateTime, dealStartTimestamp));
        }
        lambdaQueryWrapper.select(TicketData::getId, TicketData::getApplyUser, TicketData::getCreateTime, TicketData::getUpdateTime,
                TicketData::getTicketFinishTime, TicketData::getTags, TicketData::getUpdateBy);
        List<TicketData> ticketDataList = ticketDataService.list(lambdaQueryWrapper);

        if (ObjectHelper.isEmpty(ticketDataList)) {
            log.info(String.format("模板(%s):工单创建时效(%s小时)两倍区间内，工单处理时效(%s小时)两倍区间内,没有查询到工单", ticketTemplateId, ticketAgingTime, ticketDealTime));
            return null;
        }

        /**
         * 批量设置ticketData的tag字段数据
         */
        ticketDataList.stream().forEach(ticketData -> {
            List<String> tagList = new ArrayList<>();
            //创建时效超时的工单
            LocalDateTime createTime = LocalDateTime.ofInstant(ticketData.getCreateTime().toInstant(), ZoneId.systemDefault());
            String tags = ticketData.getTags();
            if (ObjectHelper.isNotEmpty(ticketAgingTime)
                    && createTime.compareTo(currentLocalDateTime.minusHours(ticketAgingTime)) < 0
                    && (ObjectHelper.isEmpty(tags) || !tags.contains(TfsBaseConstant.AGING_OVER_TIME)))
            {
                if (ObjectHelper.isEmpty(ticketData.getTicketFinishTime())) {
                    tagList.add(TfsBaseConstant.AGING_OVER_TIME);
                } else {
                    LocalDateTime finishTime = LocalDateTime.ofInstant(ticketData.getTicketFinishTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime AgingDateTime = createTime.plusHours(ticketAgingTime);
                    if (finishTime.compareTo(AgingDateTime) > 0) {
                        tagList.add(TfsBaseConstant.AGING_OVER_TIME);
                    }
                }
            }
            //处理时效超时的工单
            LocalDateTime updateTime = LocalDateTime.ofInstant(ticketData.getUpdateTime().toInstant(), ZoneId.systemDefault());
            if (ObjectHelper.isNotEmpty(ticketDealTime)
                    && updateTime.compareTo(currentLocalDateTime.minusHours(ticketDealTime)) < 0
                    && (ObjectHelper.isEmpty(tags) || !tags.contains(TfsBaseConstant.DEAL_OVER_TIM)))
            {
                if (ObjectHelper.isEmpty(ticketData.getTicketFinishTime())) {
                    tagList.add(TfsBaseConstant.DEAL_OVER_TIM);
                    //发送消息通知给给currentDealUsers
                    ThreadUtil.execAsync(() -> {
                        String id = ticketData.getId();
                        AccountInfo accountInfo = AccountInfo.ToAccountInfo(ticketData.getApplyUser());
                        log.info("***********************accountInfo:{}", JSONObject.toJSONString(accountInfo));
                        ticketDataService.urgeTicketByIdList(ApproveDealTypeEnum.OVERTIME, Arrays.asList(id), accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName(), null);
                    });
                }
            }
            if (ObjectHelper.isNotEmpty(tagList)) {
                String sourceTags = ticketData.getTags();
                if (ObjectHelper.isNotEmpty(sourceTags)) {
                    List<String> sourceTagsList = JSONObject.parseObject(sourceTags, List.class);
                    sourceTagsList.stream().forEach(it -> {
                        tagList.add("\"" + it + "\"");
                    });
                }
                ticketData.setTags(tagList.toString());
                overTimeTicketDataList.add(ticketData);
            }
        });
        return overTimeTicketDataList;
    }

    public static void main(String[] args) {
        List<String> tagList = new ArrayList<>();
        tagList.add("\"创建超时\"");
        tagList.add("\"处理超时\"");
        System.out.println(tagList.toString());

        List<String> sourceTagsList = JSONObject.parseObject(tagList.toString(), List.class);
        System.out.println(sourceTagsList.toString());
    }


    /**
     * 查询工单前对参数的预处理
     *
     * @param ticketDataListRequestDto
     */
    private void queryPreHandleForDataList(TicketDataListRequestDto ticketDataListRequestDto){
        //添加当前操作人，方便关联查询
        String currentUsername = SecurityUtils.getOriginUserInfoForSearch();
        ticketDataListRequestDto.setCurrentUserInfo(currentUsername);

        //特殊处理
        parseMultipleStrToList(ticketDataListRequestDto);

        List<String> reqAppIdList = ticketDataListRequestDto.getAppIdList();
        List<String> reqTemplateIdList = ticketDataListRequestDto.getTemplateIdList();
        if (ticketDataListRequestDto.isNeedControl() && !SecurityUtils.isAdmin()) {
            List<String> adminAppIdList = ticketAppService.queryAdminAppListForPointUser(currentUsername);
            if (ObjectHelper.isEmpty(adminAppIdList)) {
                throw new ServiceException("此用户没有任何业务的管理员权限");
            }
            ;
            List<String> queryAppIdList = adminAppIdList;
            //如果原有的查询条件就是有appIdList的筛选，则取交集赋值
            if (ObjectHelper.isNotEmpty(reqAppIdList)) {
                queryAppIdList = (List<String>) CollUtil.intersection(reqAppIdList, adminAppIdList);
            }
            if (ObjectHelper.isEmpty(queryAppIdList)) {
                throw new ServiceException("此用户没有所选的业务权限");
            }
            LambdaQueryWrapper<TicketTemplate> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.in(TicketTemplate::getAppId, queryAppIdList);
            queryAppIdList.stream().forEach(it -> {
                lambdaQueryWrapper.or();
                lambdaQueryWrapper.like(TicketTemplate::getBeyondApps, it);
            });
            List<TicketTemplate> ticketTemplates = ticketTemplateService.list(lambdaQueryWrapper);
            if (ObjectHelper.isEmpty(ticketTemplates))
                throw new ServiceException("此用户没有订阅的工单模板");
            List<String> ticketTemplateIdList = ticketTemplates.stream().map(it -> it.getId()).collect(Collectors.toList());
            //如果原有的查询条件就是有reqTemplateIdList的筛选，则取交集赋值
            List<String> queryTemplateIdList = ticketTemplateIdList;
            if (ObjectHelper.isNotEmpty(reqTemplateIdList)) {
                queryTemplateIdList = (List<String>) CollUtil.intersection(reqTemplateIdList, ticketTemplateIdList);
            }
            //此时只需要根据ticketTemplateIdList去查询
            ticketDataListRequestDto.setAppIdList(null);
            ticketDataListRequestDto.setTemplateIdList(queryTemplateIdList);
        }

        //从sdk过来的，需要过滤模版订阅
        if (!SecurityUtils.isTfs() && StrUtil.isNotBlank(ticketDataListRequestDto.getAppId())) {
            LambdaQueryWrapper<TicketTemplate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(TicketTemplate::getAppId, ticketDataListRequestDto.getAppId());
            lambdaQueryWrapper.or().like(TicketTemplate::getBeyondApps, ticketDataListRequestDto.getAppId());
            List<TicketTemplate> ticketTemplates = ticketTemplateService.list(lambdaQueryWrapper);

            if (ObjectHelper.isNotEmpty(ticketTemplates)) {
                //如果原有的查询条件就是有reqTemplateIdList的筛选，则取交集赋值
                List<String> queryTemplateIdList = ticketTemplates.stream().map(TicketTemplate::getId).collect(Collectors.toList());
                if (ObjectHelper.isNotEmpty(ticketDataListRequestDto.getTemplateIdList())) {
                    queryTemplateIdList = (List<String>) CollUtil.intersection(ticketDataListRequestDto.getTemplateIdList(), queryTemplateIdList);
                }
                ticketDataListRequestDto.setAppId(null);
                ticketDataListRequestDto.setAppIdList(null);
                ticketDataListRequestDto.setTemplateIdList(queryTemplateIdList);
            }
        }

        //根据模版分類查詢模版id
        List<String> categoryTemplateIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(ticketDataListRequestDto.getCategoryIdListStr())) {
            String[] categoryIdArray = ticketDataListRequestDto.getCategoryIdListStr().split("\\,");
            List<Integer> categoryIdList = Arrays.stream(categoryIdArray).map(it -> Integer.parseInt(it)).collect(Collectors.toList());
            List<TicketCategory> categoryList = ticketCategoryService.lambdaQuery()
                    .isNull(TicketCategory::getDeleteTime)
                    .in(TicketCategory::getId, categoryIdList)
                    .list();
            if (CollectionUtils.isNotEmpty(categoryList)) {
                categoryTemplateIdList = categoryList.stream()
                        .map(it -> it.getTemplateId())
                        .collect(Collectors.toList());
            }

        }
        List originalTemplateIdList = ticketDataListRequestDto.getTemplateIdList();
        if (CollectionUtils.isNotEmpty(categoryTemplateIdList) && CollectionUtils.isEmpty(originalTemplateIdList)) {
            ticketDataListRequestDto.setTemplateIdList(categoryTemplateIdList);
        } else if (CollectionUtils.isNotEmpty(categoryTemplateIdList) && CollectionUtils.isNotEmpty(originalTemplateIdList)) {
            List<String> intersectionList = (List<String>) CollUtil.intersection(originalTemplateIdList, categoryTemplateIdList);
            if (CollectionUtils.isEmpty(intersectionList)) {
                throw new ServiceException("工单分类下面的模版、选中的业务下面的模版、筛选的模版不重合，查询为空");
            }
            ticketDataListRequestDto.setTemplateIdList((List<String>) CollUtil.intersection(originalTemplateIdList, categoryTemplateIdList));
        }

        String appId = ticketDataListRequestDto.getAppId();
        TicketApp ticketApp = null;
        if (StringUtils.isNotEmpty(appId)) {
            List<TicketApp> ticketAppList = ticketAppService.lambdaQuery()
                    .eq(TicketApp::getId, appId)
                    .isNull(TicketApp::getDeleteTime)
                    .list();
            if (CollectionUtils.isEmpty(ticketAppList)) {
                throw new ServiceException(String.format("该业务(id:%s)不存在", appId));
            }
            ticketApp = ticketAppList.get(0);
        }
        Date updateStartTime = ticketDataListRequestDto.getUpdateStartTime();
        Date updateEndTime = ticketDataListRequestDto.getUpdateEndTime();
        if ((Objects.isNull(updateStartTime) && Objects.nonNull(updateEndTime)) || (Objects.nonNull(updateStartTime) && Objects.isNull(updateEndTime))) {
            throw new ServiceException("更新开始时间和结束时间必须同时传值");
        }
        if (Objects.nonNull(updateStartTime) && Objects.nonNull(updateEndTime) && (updateStartTime.after(updateEndTime) || DateUtil.between(updateStartTime, updateEndTime, DateUnit.DAY) > 30)) {
            throw new ServiceException("更新时间的终止时间比开始时间最多晚30天");
        }
        //PC端：更新时间、模糊查询字段、扩展字段1三选一

//        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
//                && StringUtils.isBlank(ticketDataListRequestDto.getSearchValue())
//                && StringUtils.isBlank(ticketDataListRequestDto.getExtend1())
//                && ticketDataListRequestDto.isNeedCount() //代表PC端：needCount为true
//                ) {
//            String extendName1 = "扩展字段1";
//            if (Objects.nonNull(ticketApp) && StringUtils.isNotEmpty(ticketApp.getExtendFields())) {
//                String extendFieldsStr = ticketApp.getExtendFields();
//                List<BusiTicketDataFieldsMappingDto> extendFieldsList = JSONArray.parseArray(extendFieldsStr, BusiTicketDataFieldsMappingDto.class);
//                if (CollectionUtils.isNotEmpty(extendFieldsList)) {
//                    List<BusiTicketDataFieldsMappingDto> extendList = extendFieldsList.stream().filter(it -> "extend1".equals(it.getFieldCode())).collect(Collectors.toList());
//                    if (CollectionUtils.isNotEmpty(extendList)) {
//                        BusiTicketDataFieldsMappingDto busiTicketDataFieldsMappingDto = extendList.get(0);
//                        extendName1 = busiTicketDataFieldsMappingDto.getFieldName();
//                    }
//                }
//            }
//            throw new ServiceException(String.format("更新时间、模糊查询字段、%s不能都为空", extendName1));
//        }

        //jssdk端：更新时间、模糊查询字段二选一
        if (Objects.isNull(updateStartTime) && Objects.isNull(updateEndTime)
                && StringUtils.isBlank(ticketDataListRequestDto.getSearchValue())
                && !ticketDataListRequestDto.isNeedCount() //代表jssdk端:needCount为false
        )
        {
            updateStartTime = DateUtil.offsetDay(new Date(), -30);
            updateEndTime = new Date();
        }
        ticketDataListRequestDto.setUpdateStartTime(updateStartTime);
        ticketDataListRequestDto.setUpdateEndTime(updateEndTime);
    }

    /**
     * 查询工单后对接口的后置处理
     *
     * @param ticketDataListResponseList
     */
    private void queryPostHandleForDataList(List<TicketDataListResponseDto> ticketDataListResponseList){

        if (CollUtil.isEmpty(ticketDataListResponseList)) {
            return;
        }
        List<String> appIdList = ticketDataListResponseList.stream().map(TicketDataListResponseDto::getAppId).collect(Collectors.toList());
        Map<String, String> appIdNameMap = ticketAppService.selectNameMapByIdList(appIdList);

        List<String> templateIdList = ticketDataListResponseList.stream().map(TicketDataListResponseDto::getTemplateId).collect(Collectors.toList());
        Map<String, String> templateIdNameMap = ticketTemplateService.selectNameMapByIdList(templateIdList);


        for (TicketDataListResponseDto ticketDataListResponseDto : ticketDataListResponseList) {
            ticketDataListResponseDto.setApplyUser(accountReturnComponent.toAccountInfoStrForFront(ticketDataListResponseDto.getApplyUser()));
            ticketDataListResponseDto.setCurrentDealUsers(accountReturnComponent.toAccountInfoStrForFront(ticketDataListResponseDto.getCurrentDealUsers()));
            ticketDataListResponseDto.setAppName(appIdNameMap.getOrDefault(ticketDataListResponseDto.getAppId(), ""));
            ticketDataListResponseDto.setTicketTemplateName(templateIdNameMap.getOrDefault(ticketDataListResponseDto.getTemplateId(), ""));

            if (ticketDataListResponseDto.getTicketStatus() == TicketDataStatusEnum.APPLYING && ticketDataListResponseDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
                ticketDataListResponseDto.setShowReminderButton(Boolean.TRUE);
                ticketDataListResponseDto.setShowFollowButton(StrUtil.isBlank(ticketDataListResponseDto.getWxChatGroupId()));
            }
        }
    }

    //用于列表查询，将前端下拉框批量选择转换为列表
    private void parseMultipleStrToList(TicketDataListRequestDto ticketDataListRequestDto){
        //所属应用
        if (StrUtil.isNotBlank(ticketDataListRequestDto.getAppId())) {
            ticketDataListRequestDto.setAppIdList(Arrays.asList(ticketDataListRequestDto.getAppId().split(",")));
            if (ticketDataListRequestDto.getAppId().contains(",")) {
                ticketDataListRequestDto.setAppId("");
            }
        }

        //工单模版类型
        if (StrUtil.isNotBlank(ticketDataListRequestDto.getTemplateId())) {
            ticketDataListRequestDto.setTemplateIdList(Arrays.asList(ticketDataListRequestDto.getTemplateId().split(",")));
            if (ticketDataListRequestDto.getTemplateId().contains(",")) {
                ticketDataListRequestDto.setTemplateId("");
            }
        }

        //工单状态
        if (StrUtil.isNotBlank(ticketDataListRequestDto.getTicketStatusStr()) && ticketDataListRequestDto.getTicketStatusStr().contains(",")) {
            ticketDataListRequestDto.setTicketStatusStrList(Arrays.asList(ticketDataListRequestDto.getTicketStatusStr().split(",")));
            ticketDataListRequestDto.setTicketStatusStr("");
        }
    }

    @Override
    public Integer selectTicketDataCount(TicketDataListRequestDto ticketDataListRequestDto){

        queryPreHandleForDataList(ticketDataListRequestDto);
        return this.baseMapper.selectTicketDataCount(ticketDataListRequestDto);
    }


    @Override
    public TicketDataDto argumentTicketDataById(TicketDataDto ticketDataDto, String dealUserType, String dealUserId, String argumentContent){

        return null;
    }

    /**
     * 审批通过，审批驳回
     * appId userId token
     *
     * @param ticketID    工单数据ID
     * @param dealType    处理类型（通过，驳回）
     * @param dealOpinion 处理备注
     * @param dealUserId  处理人ID，
     * @return 工单数据信息
     * @throws Exception
     */
    @Override
    @ApiDoc(value = "审批（通过/驳回）", description = "审批（通过/驳回）")
    public Response<TicketDataDto> dealTicketDataById(String ticketID, String dealType, String dealOpinion, String dealUserType, String dealUserId, String dealUserName, String dealNodeId){

        return ticketDataApproveService.approve(ticketID, dealType, dealOpinion, dealUserType, dealUserId, dealUserName, dealNodeId);
    }

    //加签
    @Override
    @ApiDoc(value = "加签", description = "加签")
    public Response addNodeData(AddTicketFlowNodeDto addTicketFlowNodeDto, AccountInfo accountInfo){

        return ticketDataApproveService.addTicketFlowNodeData(addTicketFlowNodeDto, accountInfo);
    }

    //撤回
    @Override
    @ApiDoc(value = "撤销工单", description = "撤销工单")
    public Response<String> withdrawTicketByIdList(List<String> ticketIdList, String userType, String userId, String userName){

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }

        List<String> errorInfoList = new ArrayList<>();
        List<TicketData> ticketDataList = this.selectTicketDataById(ticketIdList);
        for (TicketData ticketData : ticketDataList) {
            // 循环撤销
            try {
                Response<String> response = doWithdrawTicket(ticketData, ticketAccountMapping.getSameOriginId(), userType, userId, userName);
                if (!response.isSuccess()) {
                    errorInfoList.add(response.getMsg());
                }
            } catch (Exception e) {
                log.error("工单{}催办异常，原因：", ticketData.getId(), e);
                errorInfoList.add(String.format("工单%s催办异常，", ticketData.getId()));
            }
        }
        if (CollUtil.isNotEmpty(errorInfoList)) {
            return new Response<>(null, BizResponseEnums.UNKNOW_EXCEPTION_CODE, CollUtil.join(errorInfoList, "\r\n"));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "撤销成功");
    }

    private Response<String> doWithdrawTicket(TicketData ticketData, String sameOriginId, String userType, String userID, String userName){

        if (!StrUtil.equals(TicketDataStatusEnum.APPLYING.getCode(), ticketData.getTicketStatus().getCode())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s不在审批中", ticketData.getId()));
        }
        AccountInfo dealUser = new AccountInfo(sameOriginId, userType, userID, userName);
        Date now = new Date();
        String dealUserStr = dealUser.ToJsonString();
        //撤回卡片+修改工单状态+操作记录+撤销回调
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketData.getId());
        approveDetail.setTicketFlowNodeDataId(ticketData.getCurrentNodeId());
        approveDetail.setDealUserType(userType);
        approveDetail.setDealUserId(userID);//待定
        approveDetail.setDealUserName(userName);//待定
        approveDetail.setDealType(ApproveDealTypeEnum.WITHDRAW);
        approveDetail.setCreateBy(dealUserStr);
        approveDetail.setCreateTime(now);
        approveDetail.setUpdateBy(dealUserStr);
        approveDetail.setUpdateTime(now);
        approveDetail.setDealOpinion("撤回工单");
        if (ticketData.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM && StringUtils.isNotEmpty(ticketData.getCurrentNodeId())) {
            //数据查询, 工单数据
            Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketData.getId()));
            if (!ticketDataDtoResponse.isSuccess()) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("ID:%s，工单全量数据查询失败", ticketData.getId()));
            }
            TicketDataDto ticketDataDto = ticketDataDtoResponse.getData();
            TicketFlowNodeDataDto currentNode = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);
            ticketDataApproveService.disable(ticketDataDto, currentNode, null, "申请人已撤回");
        }
        transactionTemplate.execute(action -> {
            ticketFlowNodeDataService.lambdaUpdate()
                    .eq(TicketFlowNodeData::getId, ticketData.getCurrentNodeId())
                    .eq(TicketFlowNodeData::getTicketDataId, ticketData.getId())
                    .eq(TicketFlowNodeData::getTemplateId, ticketData.getTemplateId())
                    .eq(TicketFlowNodeData::getTicketFlowDataId, ticketData.getCurrentNodeId())
                    .isNull(TicketFlowNodeData::getDeleteTime)
                    .set(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.WITHDRAW)
                    .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                    .set(TicketFlowNodeData::getUpdateTime, now)
                    .update();
            ticketDataService.lambdaUpdate()
                    .eq(TicketData::getId, ticketData.getId())
                    .isNull(TicketData::getDeleteTime)
                    .set(TicketData::getTicketStatus, TicketDataStatusEnum.WITHDRAW)
                    .set(TicketData::getCurrentNodeName, "开始")
                    .set(TicketData::getCurrentNodeId, "-1")
                    .set(TicketData::getCurrentDealUsers, "[]")
                    .set(TicketData::getUpdateBy, dealUserStr)
                    .set(TicketData::getUpdateTime, now)
                    .update();
            ticketFlowNodeApproveDetailService.save(approveDetail);
            return true;
        });

        var firstFlowNodeOpt = ticketFlowNodeDataService.lambdaQuery()
                .isNull(TicketFlowNodeData::getDeleteTime)
                .eq(TicketFlowNodeData::getPreNodeId, "-1")
                .eq(TicketFlowNodeData::getTicketDataId, ticketData.getId()).oneOpt();
        if (firstFlowNodeOpt.isPresent()) {
            //撤回事件执行时机： 开始结点&拒绝后执行
            Response<String> executeResult = ticketDataApproveService.executeEvent(ticketData.getId(), firstFlowNodeOpt.get().getId(), ExecuteStepEnum.DONE_REJECT.getCode(), null, null, null);
        }

        return Response.success();
    }

    @Override
    @ApiDoc(value = "催办", description = "催办")
    public Response<String> urgeTicketByIdList(ApproveDealTypeEnum dealTypeEnum, List<String> ticketIdList, String userType, String userId, String userName, String dealOpinion){

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        String tag = "";
        switch (dealTypeEnum) {
            case URGE:
                tag = "【催办】";
                break;
            case OVERTIME:
                tag = "【处理超时】";
                break;
        }

        List<String> errorInfoList = new ArrayList<>();
        for (String ticketId : ticketIdList) {
            // 循环催办
            try {
                //var urgeRes = doUrgeTicket(ticketId, userType, userId, userName);
                //数据查询, 工单数据
                Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketId));
                if (!ticketDataDtoResponse.isSuccess()) {
                    return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败");
                }
                var ticketDataDto = ticketDataDtoResponse.getData();
                String title = tag + "{{apply_user}}提交的{{ticket_name}}，待你处理"
                        .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                        .replace("{{ticket_name}}", ticketDataDto.getTicketName());
                List<AccountInfo> sendUsers = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentDealUsers());
                //发送卡片，并持久化数据
                var urgeRes = notificationBizService.SendDealCard(
                        title,
                        ticketDataDto,
                        new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName),
                        ApproveDealTypeEnum.URGE,
                        sendUsers,
                        true,
                        dealOpinion
                );
                if (!urgeRes.isSuccess()) {
                    errorInfoList.add(urgeRes.getMsg());
                }
            } catch (Exception e) {
                log.error("工单{}催办异常，原因：", ticketId, e);
                errorInfoList.add(String.format("工单%s催办异常，", ticketId));
            }
        }
        if (CollUtil.isNotEmpty(errorInfoList)) {
            return new Response<>(null, BizResponseEnums.UNKNOW_EXCEPTION_CODE, CollUtil.join(errorInfoList, "\r\n"));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "催办成功");
    }

    //根据工单数据具体执行催办操作
    private Response<String> doUrgeTicket(String ticketDataId, String sameOriginId, String userType, String userId, String userName){

        AccountInfo thisUser = new AccountInfo(sameOriginId, userType, userId, userName);
        String thisUserStr = JSONUtil.toJsonStr(thisUser);
        //数据查询, 工单数据
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败");
        }
        var ticketDataDto = ticketDataDtoResponse.getData();
        if (ticketDataDto.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s不在审批中", ticketDataDto.getId()));
        }
        if (ticketDataDto.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            String currentDealUser = ticketDataDto.getCurrentDealUsers();
            List<AccountInfo> currentDealUsers = AccountInfo.ToAccountInfoList(currentDealUser);

            List<AccountInfoDto> qwUserList = new ArrayList<>();
            for (AccountInfo dealUser : currentDealUsers) {
                TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(dealUser.getAccountId(), dealUser.getAccountType());
                if (ticketRemoteAccountDto != null && StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId())) {
                    qwUserList.add(new AccountInfoDto(dealUser.getSameOriginId(), dealUser.getAccountType(), dealUser.getAccountId(), dealUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
                }
            }
            TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);

            //new
            NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
            String titleTemplate = "【催办】{{apply_user}}提交的{{ticket_name}}，待你处理";
            qwcardMsg.setTitle(titleTemplate
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName())
            );

            String desciptionTemplate = "申请时间：{{apply_time}}    业务：{{app_name}}";
            qwcardMsg.setDescription(desciptionTemplate
                    .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                    .replace("{{app_name}}", ticketDataDto.getAppName()));

            String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketDataDto.getId();
            qwcardMsg.setJumpUrl(jumpUrl);

            //TODO 复杂处理
            List<NotificationService.KvContent> kvContentList = new ArrayList<>();
            List<TicketFormItemDataDto> ticketFormItemDataDtoList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList();
            if (CollectionUtils.isNotEmpty(ticketFormItemDataDtoList)) {
                int maxSum = 0;
                int itemCount = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().size();
                for (TicketFormItemDataDto ticketFormItemDataDto : ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList()) {
                    //TODO:Switch类型
                    maxSum = maxSum + 1;
                    if (itemCount > 6 && maxSum == 6) {
                        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                        break;
                    } else {
                        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                    }
                }
            }
            qwcardMsg.setKvContentList(kvContentList);

            log.info("doApproveNotify 需要通知的人，{}", JSONUtil.toJsonStr(qwUserList));
            for (AccountInfoDto accountInfo : qwUserList) {
                qwcardMsg.setUserIdList(Arrays.asList(accountInfo.getQywxId()));
                Map<String, String> buttonMap = new LinkedHashMap<>();
                String rejectKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.REJECT.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(rejectKey, "驳回");
                String passKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.PASS.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(passKey, "通过");
                qwcardMsg.setButtonKeyMap(buttonMap);
                NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
                TicketFlowNodeData ticketFlowNodeData = flowNodeDataService.lambdaQuery()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId())
                        .one();
                Map<String, List> wxDealCardCode = new HashMap<>();
                if (StringUtils.isNotBlank(ticketFlowNodeData.getNodeWxDealCardCode())) {
                    wxDealCardCode.putAll(JSONObject.parseObject(ticketFlowNodeData.getNodeWxDealCardCode(), Map.class));
                }

                String key = String.format("%s-%s-%s", accountInfo.getAccountId(), accountInfo.getAccountType(), TicketMsgArriveTypeEnum.WECOM.getCode());
                if (!wxDealCardCode.containsKey(key)) {
                    wxDealCardCode.put(key, new ArrayList());
                }
                wxDealCardCode.get(key).add(cardNotifyRet.getResponseCode());
                var msgSaveRes = flowNodeDataService.lambdaUpdate()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId()).
                        isNull(TicketFlowNodeData::getDeleteTime)
                        .set(TicketFlowNodeData::getNodeWxDealCardCode, JSONUtil.toJsonStr(wxDealCardCode))
                        .update();
                if (!msgSaveRes) {
                    log.error(String.format("节点卡片信息存储失败，工单ID：%s 流程节点ID：%s", currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId()));
                }
            }
        } else {
            log.warn(String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        Date now = new Date();
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketDataDto.getId());
        approveDetail.setTicketFlowNodeDataId(ticketDataDto.getCurrentNodeId());
        approveDetail.setDealUserType(thisUser.getAccountType());
        approveDetail.setDealUserId(thisUser.getAccountId());//待定
        approveDetail.setDealUserName(thisUser.getAccountName());//待定
        approveDetail.setDealType(ApproveDealTypeEnum.URGE);
        approveDetail.setDealOpinion(thisUser.getAccountName() + "催办工单");
        approveDetail.setCreateTime(now);
        approveDetail.setCreateBy(thisUserStr);
        approveDetail.setUpdateTime(now);
        approveDetail.setUpdateBy(thisUserStr);
        Boolean saveRes = ticketFlowNodeApproveDetailService.save(approveDetail);
        if (!saveRes) {
            return new Response<>(null, BizResponseEnums.SAVE_ERROR, "催办失败");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "催办成功");
    }


    //根据工单数据具体执行催办操作
    private Response<String> doUrgeTicketDemo(String ticketDataId, String sameOriginId, String userType, String userId, String userName){

        AccountInfo thisUser = new AccountInfo(sameOriginId, userType, userId, userName);
        String thisUserStr = JSONUtil.toJsonStr(thisUser);
        //数据查询, 工单数据
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketDataId));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据查询失败");
        }
        var ticketDataDto = ticketDataDtoResponse.getData();
        if (ticketDataDto.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s不在审批中", ticketDataDto.getId()));
        }
        if (ticketDataDto.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        if (ticketDataDto.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            String currentDealUser = ticketDataDto.getCurrentDealUsers();
            List<AccountInfo> currentDealUsers = AccountInfo.ToAccountInfoList(currentDealUser);

            List<AccountInfoDto> qwUserList = new ArrayList<>();
            for (AccountInfo dealUser : currentDealUsers) {
                TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndType(dealUser.getAccountId(), dealUser.getAccountType());
                if (ticketRemoteAccountDto != null && StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId())) {
                    qwUserList.add(new AccountInfoDto(dealUser.getSameOriginId(), dealUser.getAccountType(), dealUser.getAccountId(), dealUser.getAccountName(), ticketRemoteAccountDto.getQywxId()));
                }
            }
            TicketFlowNodeDataDto currentFlowNodeData = ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList().stream().filter(item -> Objects.equals(item.getId(), ticketDataDto.getCurrentNodeId())).findFirst().orElse(null);

            //new
            NotificationService.QwCardMsg qwcardMsg = new NotificationService.QwCardMsg();
            String titleTemplate = "【催办】{{apply_user}}提交的{{ticket_name}}，待你处理";
            qwcardMsg.setTitle(titleTemplate
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName())
            );

            String desciptionTemplate = "申请时间：{{apply_time}}    业务：{{app_name}}";
            qwcardMsg.setDescription(desciptionTemplate
                    .replace("{{apply_time}}", DateUtil.formatDateTime(ticketDataDto.getCreateTime()))
                    .replace("{{app_name}}", ticketDataDto.getAppName()));

            String jumpUrl = tfSJumpUrlProperties.getTicketDetailUrl() + ticketDataDto.getId();
            qwcardMsg.setJumpUrl(jumpUrl);

            //TODO 复杂处理
            List<NotificationService.KvContent> kvContentList = new ArrayList<>();
            List<TicketFormItemDataDto> ticketFormItemDataDtoList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList();
            if (CollectionUtils.isNotEmpty(ticketFormItemDataDtoList)) {
                int maxSum = 0;
                int itemCount = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().size();
                for (TicketFormItemDataDto ticketFormItemDataDto : ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList()) {
                    //TODO:Switch类型
                    maxSum = maxSum + 1;
                    if (itemCount > 6 && maxSum == 100) {
                        kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "查看更多..", jumpUrl, "", ""));
                        break;
                    } else {
                        switch (ticketFormItemDataDto.getItemType()) {
                            case INPUTNUMBER://
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                                break;
                            case SELECTMULTIPLE:
                                List<String> valueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), String.class);
                                if (valueArr == null) {
                                    valueArr = new ArrayList<>();
                                }
                                StringBuilder selectMultipleValue = new StringBuilder();
                                for (String value : valueArr) {
                                    selectMultipleValue.append(value).append(" ");
                                }
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                selectMultipleValue.toString(),
                                                "",
                                                "",
                                                "")
                                );
                                break;
                            case TIMESPAN:
                                List<String> timeSpanValueArr = JSON.parseArray(ticketFormItemDataDto.getItemValue(), String.class);
                                kvContentList.add(
                                        new NotificationService.KvContent(
                                                NotificationService.KvContentType.TEXT,
                                                ticketFormItemDataDto.getItemLabel(),
                                                timeSpanValueArr.get(0) + "到" + timeSpanValueArr.get(1),
                                                "",
                                                "",
                                                "")
                                );
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                                break;
                            case PICTURE://
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "详情查看图片..", jumpUrl, "", ""));
                                break;
                            case FILE://
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "详情查看附件..", jumpUrl, "", ""));
                                break;
                            case GROUP://
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.LINK, ticketFormItemDataDto.getItemLabel(), "详情查看明细..", jumpUrl, "", ""));
                                break;
                            default:
                                kvContentList.add(new NotificationService.KvContent(NotificationService.KvContentType.TEXT, ticketFormItemDataDto.getItemLabel(), ticketFormItemDataDto.getItemValue(), "", "", ""));
                                break;
                        }
                    }
                }
            }
            qwcardMsg.setKvContentList(kvContentList);

            log.info("doApproveNotify 需要通知的人，{}", JSONUtil.toJsonStr(qwUserList));
            for (AccountInfoDto accountInfo : qwUserList) {
                qwcardMsg.setUserIdList(Arrays.asList(accountInfo.getQywxId()));
                Map<String, String> buttonMap = new LinkedHashMap<>();
                String passKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.PASS.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(passKey, "通过");
                String rejectKey = String.format("%s-%s-%s-%s-%s",
                        "approveCardButtonCallBack",
                        ticketDataDto.getId(),
                        ApproveDealTypeEnum.REJECT.getCode(),
                        String.format("%s_%s_%s", accountInfo.getAccountType(), accountInfo.getAccountId(), accountInfo.getAccountName()),
                        currentFlowNodeData.getId()
                );
                buttonMap.put(rejectKey, "驳回");
                qwcardMsg.setButtonKeyMap(buttonMap);
                NotificationService.CardNotifyRet cardNotifyRet = notificationService.notifyQwCard(qwcardMsg);
                TicketFlowNodeData ticketFlowNodeData = flowNodeDataService.lambdaQuery()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId())
                        .one();
                Map<String, List> wxDealCardCode = new HashMap<>();
                if (StringUtils.isNotBlank(ticketFlowNodeData.getNodeWxDealCardCode())) {
                    wxDealCardCode.putAll(JSONObject.parseObject(ticketFlowNodeData.getNodeWxDealCardCode(), Map.class));
                }

                String key = String.format("%s-%s-%s", accountInfo.getAccountId(), accountInfo.getAccountType(), TicketMsgArriveTypeEnum.WECOM.getCode());
                if (!wxDealCardCode.containsKey(key)) {
                    wxDealCardCode.put(key, new ArrayList());
                }
                wxDealCardCode.get(key).add(cardNotifyRet.getResponseCode());
                var msgSaveRes = flowNodeDataService.lambdaUpdate()
                        .eq(TicketFlowNodeData::getId, currentFlowNodeData.getId()).
                        isNull(TicketFlowNodeData::getDeleteTime)
                        .set(TicketFlowNodeData::getNodeWxDealCardCode, JSONUtil.toJsonStr(wxDealCardCode))
                        .update();
                if (!msgSaveRes) {
                    log.error(String.format("节点卡片信息存储失败，工单ID：%s 流程节点ID：%s", currentFlowNodeData.getTicketDataId(), currentFlowNodeData.getId()));
                }
            }
        } else {
            log.warn(String.format("工单ID:%s 未开启企微通知", ticketDataDto.getId()));
        }
        Date now = new Date();
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketDataDto.getId());
        approveDetail.setTicketFlowNodeDataId(ticketDataDto.getCurrentNodeId());
        approveDetail.setDealUserType(thisUser.getAccountType());
        approveDetail.setDealUserId(thisUser.getAccountId());//待定
        approveDetail.setDealUserName(thisUser.getAccountName());//待定
        approveDetail.setDealType(ApproveDealTypeEnum.URGE);
        approveDetail.setDealOpinion(thisUser.getAccountName() + "催办工单");
        approveDetail.setCreateTime(now);
        approveDetail.setCreateBy(thisUserStr);
        approveDetail.setUpdateTime(now);
        approveDetail.setUpdateBy(thisUserStr);
        Boolean saveRes = ticketFlowNodeApproveDetailService.save(approveDetail);
        if (!saveRes) {
            return new Response<>(null, BizResponseEnums.SAVE_ERROR, "催办失败");
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "催办成功");
    }


    //建群
    @Override
    @ApiDoc(value = "企微建群", description = "企微建群")
    public Response<String> createQWGroupByIdList(List<String> ticketIdList, List<AccountInfo> accountInfoList){

        List<String> errorInfoList = new ArrayList<>();
        List<TicketData> ticketDataList = this.selectTicketDataById(ticketIdList);
        for (TicketData ticketData : ticketDataList) {
            // 循环建群跟单
            try {
                if (ticketData.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
                    errorInfoList.add(String.format("工单ID:%s不在审批中", ticketData.getId()));
                    continue;
                }
                if (ticketData.getTicketMsgArriveType() != TicketMsgArriveTypeEnum.WECOM) {
                    errorInfoList.add(String.format("工单ID:%s 未开启企微通知", ticketData.getId()));
                    continue;
                }
                if (ticketData.getTicketMsgBuildType() != TicketMsgBuildTypeEnum.AUDITOR_CREATE && ticketData.getTicketMsgBuildType() != TicketMsgBuildTypeEnum.APPLY_CREATE) {
                    errorInfoList.add(String.format("工单ID:%s 未开启建群配置", ticketData.getId()));
                    continue;
                }
                if (StringUtils.isNotBlank(ticketData.getWxChatGroupId())) {
                    errorInfoList.add(String.format("工单ID: %s, 已建有相关群聊", ticketData.getId()));
                    continue;
                }

                List<TicketFormItemData> ticketFormItemDataList = ticketFormItemDataMapper.selectList(Wrappers.<TicketFormItemData>lambdaQuery().eq(TicketFormItemData::getTicketDataId, ticketData.getId()).isNull(TicketFormItemData::getDeleteTime));

                TicketApp ticketApp = ticketAppService.getById(ticketData.getAppId());
                Response<String> response = doCreateQWGroup(ticketApp, ticketData, ticketFormItemDataList, accountInfoList);
                if (!response.isSuccess()) {
                    errorInfoList.add(response.getMsg());
                }
            } catch (Exception e) {
                log.error("工单{}建群跟单失败，原因：", ticketData.getId(), e);
                errorInfoList.add(String.format("工单ID: %s, 建群跟单失败", ticketData.getId()));
            }
        }
        if (CollUtil.isNotEmpty(errorInfoList)) {
            return new Response<>(null, BizResponseEnums.UNKNOW_EXCEPTION_CODE, CollUtil.join(errorInfoList, "\r\n"));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "建群跟单成功");
    }

    //根据工单数据具体执行建群跟单操作
    //涉及成员： 申请人 + 操作人 + 应用管理员 + 所以涉及审批人
    private Response<String> doCreateQWGroup(TicketApp ticketApp, TicketData ticketData, List<TicketFormItemData> ticketFormItemDataList, List<AccountInfo> accountInfoList){

        Set<String> userQWMap = new HashSet<>();
        accountInfoList = accountInfoList == null ? new ArrayList<>() : accountInfoList;

        //获取申请人
        AccountInfo applyAccountInfo = AccountInfo.ToAccountInfo(ticketData.getApplyUser());
        accountInfoList.add(applyAccountInfo);

        List<AccountInfo> executorAccountList = AccountInfo.ToAccountInfoList(ticketData.getCurrentDealUsers());
        if (CollUtil.isNotEmpty(executorAccountList)) {
            accountInfoList.addAll(executorAccountList);
        }

        //去重取企业微信号
        accountInfoList = AccountInfo.Distinct(accountInfoList);
        for (AccountInfo accountInfo : accountInfoList) {
            TicketRemoteAccountDto accountDto = accountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
            if (accountDto != null && StringUtils.isNotBlank(accountDto.getQywxId())) {
                userQWMap.add(accountDto.getQywxId());
            }
        }

        List<String> userList = new ArrayList<>();
        userList.addAll(userQWMap);
        if (userList.size() <= 2) {
            //应用管理员
            List<AccountInfo> appAdminAccountList = AccountInfo.ToAccountInfoList(ticketApp.getAppAdminUsers());
            if (CollectionUtils.isNotEmpty(appAdminAccountList)) {
                for (AccountInfo accountInfo : appAdminAccountList) {
                    TicketRemoteAccountDto accountDto = accountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                    if (accountDto != null && StringUtils.isNotBlank(accountDto.getQywxId()) && !userList.contains(accountDto.getQywxId())) {
                        userList.add(accountDto.getQywxId());
                    }
                }
            }
            if (userList.size() <= 2) {
                return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID: %s, 企业微信相关人员不足3人", ticketData.getId()));
            }
        }


        String dealContentStr = getDealContentStr(ticketData, ticketFormItemDataList);
        CreateChatGroup createChatGroup = new CreateChatGroup(
                userList,
                String.format("统一工单平台：%s", ticketData.getTicketName()),
                userList.get(0),
                "",
                QWCardMD.qwGroupCardStr.
                        replace("{{app_name}}", ticketApp.getAppName()).
                        replace("{{ticket_id}}", ticketData.getId()).
                        replace("{{ticket_name}}", ticketData.getTicketName()).
                        replace("{{apply_user}}", applyAccountInfo.getAccountName()).
                        replace("{{deal_content}}", dealContentStr).
                        replace("{{apply_time}}", DateFormat.getInstance().format(ticketData.getCreateTime())).
                        replace("{{ticket_detail_url}}", tfSJumpUrlProperties.getTicketDetailUrl() + ticketData.getId()));
        String chatId = notificationService.createChatGroup(createChatGroup);
        boolean updateBool = ticketDataService.lambdaUpdate().eq(TicketData::getId, ticketData.getId()).isNull(TicketData::getWxChatGroupId).isNull(TicketData::getDeleteTime).set(TicketData::getWxChatGroupId, chatId).update();
        if (!updateBool) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, String.format("工单ID: %s, 建群成功但数据保存失败", ticketData.getId()));
        }
        return Response.success();
    }

    private String getDealContentStr(TicketData ticketData, List<TicketFormItemData> ticketFormItemDataList){

        StringBuilder dealContent = new StringBuilder();
        if (CollectionUtils.isNotEmpty(ticketFormItemDataList)) {
            for (TicketFormItemData ticketFormItemData : ticketFormItemDataList) {
                switch (ticketFormItemData.getItemType()) {
                    case INPUT:
                    case TEXTAREA:
                        dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("\n");
                        break;
                    case INPUTNUMBER:
                        dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("单位").append("\n");
                        break;
                    case SELECT:
                    case SELECTMULTIPLE:
                        dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("\n");
                        break;
                    case TIME:
                        dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("判断格式").append("\n");
                        break;
                    case PERSON:
                        try {
                            List<Object> deptValueArr = JSON.parseArray(ticketFormItemData.getItemValue(), Object.class);
                            StringBuilder sb = null;
                            if (CollectionUtils.isNotEmpty(deptValueArr)) {
                                for (var obj : deptValueArr) {
                                    JSONObject jObject = (JSONObject) com.alibaba.fastjson.JSONObject.toJSON(obj);
                                    if (sb == null) {
                                        sb = new StringBuilder(jObject.get("accountName").toString());
                                    } else {
                                        sb.append("，").append(jObject.get("accountName").toString());
                                    }
                                }
                            }
                            dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(sb == null ? "" : sb.toString()).append("\n");
                        } catch (Exception e) {
                            dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("\n");
                        }
                        break;
                    case DEPT:
                        try {
                            List<Object> deptValueArr = JSON.parseArray(ticketFormItemData.getItemValue(), Object.class);
                            StringBuilder sb = null;
                            if (CollectionUtils.isNotEmpty(deptValueArr)) {
                                for (var obj : deptValueArr) {
                                    JSONObject jObject = (JSONObject) JSONObject.toJSON(obj);
                                    if (sb == null) {
                                        sb = new StringBuilder(jObject.get("deptName").toString());
                                    } else {
                                        sb.append("，").append(jObject.get("deptName").toString());
                                    }
                                }
                            }
                            dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(sb.toString()).append("\n");
                        } catch (Exception e) {
                            dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("\n");
                        }
                        break;
                    case TIP:
                        break;
                    default:
                        dealContent.append(">").append(ticketFormItemData.getItemLabel()).append(":").append(ticketFormItemData.getItemValue()).append("\n");
                        break;
                }
            }
        }
        return dealContent.toString();
    }


    @Override
    @ApiDoc(value = "加入企微群", description = "加入企微群")
    public Response<String> joinQWGroup(String ticketId, String userType, String userId){

        TicketRemoteAccountDto accountDto = accountService.getTicketRemoteAccountByIdAndType(userId, userType);
        if (StringUtils.isEmpty(accountDto.getQywxId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应账户type:%s id:%s ,企微信息不存在", userType, userId));
        }
        var ticketDataOpt = ticketDataService.lambdaQuery().eq(TicketData::getId, ticketId).isNull(TicketData::getDeleteTime).oneOpt();
        if (!ticketDataOpt.isPresent()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单ID:%s ,有效工单数据不存在", ticketId));
        }
        TicketData ticketData = ticketDataOpt.get();
        if (StringUtils.isEmpty(ticketData.getWxChatGroupId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应工单ID:%s ,企微跟进群不存在", ticketId));
        }
//        Set<String> userList=notificationService.queryChatUserList(ticketData.getWxChatGroupId());
//        if(userList!=null&&userList.contains(accountDto.getQywxId())) {
//            return new Response<>(null, BizResponseEnums.SUCCESS, "邀请人已经在群聊中");
//        }
        String code = notificationService.updateChatGroup(ticketData.getWxChatGroupId(), Arrays.asList(accountDto.getQywxId()), null);
        if (!"0".equals(code)) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("拉人进群聊失败", ticketId));
        }
        return new Response<>(null, BizResponseEnums.SUCCESS, "拉人进群聊成功");
    }

    public Response<String> comment(AddTicketFlowNodeCommentDto commentDto, AccountInfo accountInfo){

        if (commentDto == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数为空");
        }
        if (StringUtils.isAllEmpty(commentDto.getNodeId(), commentDto.getTicketDataId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数字段 nodeId&ticketDataId 为空");
        }

        if (commentDto.getDealContent() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数字段 dealContent 为空");
        }
        if (StringUtils.isNotEmpty(commentDto.getDealTypeDescription())) {
            commentDto.setDealDescription(commentDto.getDealTypeDescription());
        }

        // 组装评论对象
        JSONObject dealOpinionObject = new JSONObject();
        dealOpinionObject.put("commentStrInfo", commentDto.getDealContent());
        if (StrUtil.isNotBlank(commentDto.getCommentFileInfo())) {
            dealOpinionObject.put("commentFileInfo", commentDto.getCommentFileInfo());
        }
        if (CollectionUtils.isNotEmpty(commentDto.getCommentTagInfo())) {
            dealOpinionObject.put("commentTagInfo", commentDto.getCommentTagInfo());
        }

        String dealUserStr = JSONUtil.toJsonStr(accountInfo);

        TicketFlowNodeData ticketFlowNodeData = null;
        if (StringUtils.isNotEmpty(commentDto.getNodeId())) {
            Optional<TicketFlowNodeData> dataOptional = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, commentDto.getNodeId()).oneOpt();
            if (dataOptional.isPresent()) {
                ticketFlowNodeData = dataOptional.get();
            }
        }
        if (ticketFlowNodeData == null && StringUtils.isNotEmpty(commentDto.getTicketDataId())) {
            Optional<TicketData> dataOptional = ticketDataService.lambdaQuery().isNull(TicketData::getDeleteTime).eq(TicketData::getId, commentDto.getTicketDataId()).oneOpt();
            if (dataOptional.isPresent()) {
                if (StringUtils.isNotEmpty(dataOptional.get().getCurrentNodeId())) {
                    Optional<TicketFlowNodeData> nodeOptional = ticketFlowNodeDataService.lambdaQuery().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, dataOptional.get().getCurrentNodeId()).oneOpt();
                    if (nodeOptional.isPresent()) {
                        ticketFlowNodeData = nodeOptional.get();
                    }
                }
            }
        }
        if (ticketFlowNodeData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("节点[%s][%s]不存在", commentDto.getNodeId(), commentDto.getTicketDataId()));
        }

        Date now = new Date();
        TicketFlowNodeApproveDetail flowNodeApproveDetail = new TicketFlowNodeApproveDetail();
        flowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        flowNodeApproveDetail.setTicketDataId(ticketFlowNodeData.getTicketDataId());
        flowNodeApproveDetail.setTicketFlowNodeDataId(ticketFlowNodeData.getId());
        flowNodeApproveDetail.setDealUserId(accountInfo.getAccountId());
        flowNodeApproveDetail.setDealUserType(accountInfo.getAccountType());
        flowNodeApproveDetail.setDealUserName(accountInfo.getAccountName());
        flowNodeApproveDetail.setDealType(ApproveDealTypeEnum.COMMENT);
        String dealDes = ApproveDealTypeEnum.COMMENT.getDesc();
        if (StringUtils.isNotEmpty(commentDto.getDealDescription())) {
            dealDes = commentDto.getDealDescription();
        }
        flowNodeApproveDetail.setDealTypeDescription(dealDes);
        flowNodeApproveDetail.setDealOpinion(dealOpinionObject.toString());
        flowNodeApproveDetail.setUpdateBy(dealUserStr);
        flowNodeApproveDetail.setUpdateTime(now);
        flowNodeApproveDetail.setCreateBy(dealUserStr);
        flowNodeApproveDetail.setCreateTime(now);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("node_name", ticketFlowNodeData.getNodeName());
        paramsMap.put("detail_id", flowNodeApproveDetail.getId());
        paramsMap.put("detail_user_id", flowNodeApproveDetail.getDealUserId());
        paramsMap.put("detail_user_name", flowNodeApproveDetail.getDealUserName());
        paramsMap.put("detail_user_type", flowNodeApproveDetail.getDealUserType());

        String opinion = flowNodeApproveDetail.getDealOpinion();
        if (StringUtils.isNotEmpty(opinion) && opinion.startsWith("{\"")) {
            JSONObject jsonObject = JSONObject.parseObject(opinion);
            if (jsonObject.containsKey("commentFileInfo")) {
                com.alibaba.fastjson2.JSONArray jsonArray = com.alibaba.fastjson2.JSONArray.parseArray(jsonObject.getString("commentFileInfo"));
                if (jsonArray != null && jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        com.alibaba.fastjson2.JSONObject jsItem = jsonArray.getJSONObject(i);
                        if (jsItem.containsKey("url") && jsItem.getString("url").startsWith("")) {
                            String url = jsItem.getString("url");
                            try {
                                url = UrlSignUtil.urlSignBuilder().url(url).isPublic(true).getSignUrl();
                                jsItem.put("url", url);
                            } catch (Exception ex) {
                                log.error("url 内网转外网失败", ex);
                            }
                        }
                    }
                }
                jsonObject.put("commentFileInfo", com.alibaba.fastjson2.JSONArray.toJSONString(jsonArray));
            }
            opinion = JSONObject.toJSONString(jsonObject);
        }
        paramsMap.put("detail_opinion", opinion);
        paramsMap.put("detail_type", flowNodeApproveDetail.getDealType().getCode());
        paramsMap.put("detail_type_des", flowNodeApproveDetail.getDealTypeDescription());
        Response<String> beforeResult = ticketDataApproveService.executeEvent(ticketFlowNodeData.getTicketDataId(), ticketFlowNodeData.getId(), ExecuteStepEnum.BEFORE_COMMENT.getCode(), paramsMap, null, null);
        if (!BizResponseEnums.SUCCESS.getCode().equals(beforeResult.getCode())) {
            log.error("评论前事件执行失败,{}", beforeResult.getMsg());
            return new Response<>(null, BizResponseEnums.SAVE_ERROR, StringUtils.isNotEmpty(beforeResult.getMsg()) ? beforeResult.getMsg() : dealDes + "失败");
        }
        boolean result = ticketFlowNodeApproveDetailService.save(flowNodeApproveDetail);
        if (result) {
            return new Response<>(null, BizResponseEnums.SUCCESS, dealDes + "成功");
        } else {
            return new Response<>(null, BizResponseEnums.SAVE_ERROR, dealDes + "失败");
        }
    }


    //指令接收
    @Override
    @ApiDoc(value = "接企微群消息（入口）", description = "接企微群消息（入口）")
    public TicketDataDto receiveQWGroupMsg(TicketDataDto ticketDataDto, String currentFlowNodeDataId, String
            dealUserType, String dealUserId, String msgContent)
    {

        return null;
    }

    @Override
    @ApiDoc(value = "更新表单数据", description = "更新表单数据")
    public Response updateTicketFormData(
            TicketFormUpdateDto ticketFormUpdateDto,
            String userType,
            String userId,
            String userName)
    {

        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo applyUser = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userId, userName);
        String dealUserStr = JSONUtil.toJsonStr(applyUser);
        Date now = new Date();

        //1.校验参数
        if (ticketFormUpdateDto == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单更新，参数为空");
        }
        if (StringUtils.isBlank(ticketFormUpdateDto.getTicketDataId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单更新，参数工单ID为空");
        }
        if (CollectionUtils.isEmpty(ticketFormUpdateDto.getFormItems())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单更新，参数表单项列表为空");
        }

        Response<TicketDataDto> ticketDataDtoResponse = this.selectFullTicketDataById(new ReqParam(ticketFormUpdateDto.getTicketDataId()));
        if (ticketDataDtoResponse == null || !ticketDataDtoResponse.isSuccess()) {
            return ticketDataDtoResponse;
        }
        if (ticketDataDtoResponse.getData() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单表单更新，没找到匹配的工单");
        }
        if (!TicketDataStatusEnum.APPLYING.equals(ticketDataDtoResponse.getData().getTicketStatus())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，工单状态[%s]不在审批中", ticketDataDtoResponse.getData().getId(), ticketDataDtoResponse.getData().getTicketStatus()));
        }
        if (ticketDataDtoResponse.getData().getTicketFormDataDto() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，没找到匹配的表单", ticketDataDtoResponse.getData().getId()));
        }
        if (ticketDataDtoResponse.getData().getTicketFormChangeFlag() == YESNOEnum.NO) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，不支持修改表单", ticketDataDtoResponse.getData().getId()));
        }
        if (ticketDataDtoResponse.getData().getTicketFlowDataDto() == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，没找到匹配的流程", ticketDataDtoResponse.getData().getId()));
        }
        if (CollectionUtils.isEmpty(ticketDataDtoResponse.getData().getTicketFlowDataDto().getTicketFlowNodeDataDtoList())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，没找到匹配的流程节点", ticketDataDtoResponse.getData().getId()));
        }
        TicketDataDto ticketDataDto = ticketDataDtoResponse.getData();
        TicketFlowDataDto ticketFlowDataDto = ticketDataDto.getTicketFlowDataDto();
        TicketFormDataDto ticketFormDataDto = ticketDataDto.getTicketFormDataDto();

        HashMap<String, String> templateIDToDataIDMap = new HashMap<>();
        HashMap<String, TicketFormItemDataDto> templateIDToDataMap = new HashMap<>();
        HashMap<String, String> codeToTemplateIDMap = new HashMap<>();
        HashMap<String, String> nameToTemplateIdMap = new HashMap<>();
        HashMap<String, String> nameToDataIDMap = new HashMap<>();
        for (TicketFormItemDataDto item : ticketFormDataDto.getTicketFormItemDataDtoList()) {
            //只记录动态添加的数据
            if (StringUtils.isNotEmpty(item.getItemLabel()) && "-1".equals(item.getTemplateId())) {
                nameToDataIDMap.putIfAbsent(item.getItemLabel(), item.getId());
            }
            if (StringUtils.isNotEmpty(item.getTemplateId()) && !"-1".equals(item.getTemplateId())) {
                templateIDToDataIDMap.putIfAbsent(item.getTemplateId(), item.getId());
                templateIDToDataMap.putIfAbsent(item.getTemplateId(), item);
                nameToTemplateIdMap.putIfAbsent(item.getItemLabel(), item.getTemplateId());
                if (StringUtils.isNotEmpty(item.getItemConfig())) {
                    JSONObject jsonConfig = JSONObject.parseObject(item.getItemConfig());
                    if (jsonConfig.containsKey("itemCode")) {
                        codeToTemplateIDMap.putIfAbsent(jsonConfig.getString("itemCode"), item.getTemplateId());
                    }
                }
            }
        }
        //还原TemplateID，当然也兼容找不到的情况
        for (TicketFormItemStdDto item : ticketFormUpdateDto.getFormItems()) {
            if (!item.getTemplateId().matches("[0-9]+")) {
                if (codeToTemplateIDMap.containsKey(item.getTemplateId())) {
                    item.setTemplateId(codeToTemplateIDMap.get(item.getTemplateId()));
                } else if (nameToTemplateIdMap.containsKey(item.getTemplateId())) {
                    item.setTemplateId(nameToTemplateIdMap.get(item.getTemplateId()));
                } else {
                    log.error("工单表单更新，表单项[{}]不存在", item.getTemplateId());
                }
            }
        }

        if ("DYNAMIC".equals(ticketFormUpdateDto.getUpdateMode()) || "DYNAMIC".equals(ticketFormUpdateDto.getMode()) || Arrays.asList("KF-SCS", "smy-kefu-20240528").contains(ticketDataDto.getAppId())) {
            //动态模式不校验
        } else {
            if (StringUtils.isBlank(ticketDataDto.getCurrentDealUsers())) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 当前修改节点ID:{%s},修改人{%s}不能修改", ticketDataDto.getId(), ticketDataDto.getCurrentNodeName(), applyUser.getAccountName()));
            }
            List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(ticketDataDto.getCurrentDealUsers());
            if (!ticketDataApproveService.inList(applyUser, accountInfoList)) {
                return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID：{%s} 当前修改节点ID:{%s},修改人{%s}不能修改", ticketDataDto.getId(), ticketDataDto.getCurrentNodeName(), applyUser.getAccountName()));
            }
        }

        Map<String, TicketFormItemDataDto> dbTtemDataDtoMap = ticketFormDataDto.getTicketFormItemDataDtoList().stream().collect(Collectors.toMap(TicketFormItemDataDto::getId, item -> item, (i1, i2) -> i1));
        boolean renderNode = false;
        //如果重绘，需要所有表单项的数据
        List<TicketFormItemStdDto> allItemList = new ArrayList<>();
        //非重绘数据
        List<TicketFormItemStdDto> updateItemList = new ArrayList<>();
        List<TicketFormItemData> insertItemList = new ArrayList<>();
        List<TicketFormItemData> deletetItemList = new ArrayList<>();
        List<String> deleteIdList = new ArrayList<>();
        //调整的item信息
        Set<String> updateTemplateIDSet = new HashSet<>();
        Set<String> updateTemplateNameSet = new HashSet<>();

        List<TicketFormItemData> formItemDataDtosLog = new ArrayList<>();
        Map<String, String> updateExtendedMap = new HashMap<>();
        //表单项模板列表
        List<TicketFormItemTemplate> allDbItemTemplates = iTicketFormItemTemplateService.lambdaQuery().isNull(TicketFormItemTemplate::getDeleteTime).eq(TicketFormItemTemplate::getTicketFormTemplateId, ticketFormDataDto.getTemplateId()).list();
        Map<String, TicketFormItemTemplate> templateIDMap = allDbItemTemplates.stream().collect(Collectors.toMap(i -> i.getId(), i -> i, (i1, i2) -> i1));

        StringBuilder addSb = new StringBuilder();
        StringBuilder editSb = new StringBuilder();
        StringBuilder delSb = new StringBuilder();
        //以传的参数为准
        for (TicketFormItemStdDto updateItem : ticketFormUpdateDto.getFormItems()) {
            String itemDataID = null;
            if (StringUtils.isNotEmpty(updateItem.getTemplateId()) && templateIDToDataIDMap.containsKey(updateItem.getTemplateId())) {
                itemDataID = templateIDToDataIDMap.get(updateItem.getTemplateId());
            }
            //动态添加字段，也支持修改
            if (StringUtils.isEmpty(itemDataID)) {
                itemDataID = nameToDataIDMap.get(updateItem.getTemplateId());
            }
            //修改
            if (StringUtils.isNotEmpty(itemDataID)) {
                TicketFormItemDataDto dbItemData = dbTtemDataDtoMap.remove(itemDataID);
                if (dbItemData == null) {
                    continue;
                }
                Response response = this.updateDecry(dbItemData);
                if (!response.isSuccess()) {
                    return response;
                }
                //修改,新旧值不相等，则认为是修改操作
                if ("YES".equals(updateItem.getRenderAble()) || !Objects.equals(dbItemData.getItemValue(), updateItem.getValue())) {
                    String newVal = StringUtils.isNotEmpty(updateItem.getDisplayValue()) ? updateItem.getDisplayValue() : updateItem.getValue();
                    String oldItemConfig = dbItemData.getItemConfig();
                    String oldDisplayValue = "";
                    if (StringUtils.isNotEmpty(oldItemConfig)) {
                        JSONObject oldItemConfigJSONObj = JSONObject.parseObject(oldItemConfig);
                        if (null != oldItemConfigJSONObj.get("displayValue")) {
                            oldDisplayValue = String.valueOf(oldItemConfigJSONObj.get("displayValue"));
                        }
                    }
                    String oldVal = StringUtils.isNotEmpty(oldDisplayValue) ? oldDisplayValue : dbItemData.getItemValue();
                    if (StringUtils.isEmpty(dbItemData.getItemValue())) {

                        addSb.append(String.format(" %s:%s", dbItemData.getItemLabel(), cutString(newVal, 30)));
                    } else if (StringUtils.isEmpty(updateItem.getValue())) {

                        delSb.append(String.format(" %s:%s", dbItemData.getItemLabel(), cutString(oldVal, 30)));
                    } else {

                        editSb.append(String.format(" %s:%s->%s", dbItemData.getItemLabel(), cutString(oldVal, 30), cutString(newVal, 30)));
                    }
                    JSONObject config = JSONObject.parseObject(dbItemData.getItemConfig());
                    if ("YES".equals(updateItem.getRenderAble()) || (config != null && Objects.equals(config.get("renderNode"), "YES"))) {
                        renderNode = true;
                    }
                    if (null != config
                            && config.containsKey("extendKey")
                            && null != config.get("extendKey")
                            && StringUtils.isNotEmpty(config.getString("extendKey")))
                    {
                        updateExtendedMap.putIfAbsent(config.getString("extendKey"), updateItem.getValue());
                    }
                    //标记修改
                    if (StringUtils.isNotEmpty(dbItemData.getItemLabel())) {
                        updateTemplateNameSet.add(dbItemData.getItemLabel());
                    }
                    if (StringUtils.isNotEmpty(dbItemData.getTemplateId()) && !"-1".equals(dbItemData.getTemplateId())) {
                        updateTemplateIDSet.add(dbItemData.getTemplateId());
                    }
                    updateItem.setTemplateId("-1".equals(dbItemData.getTemplateId()) ? dbItemData.getItemLabel() : dbItemData.getTemplateId());
                    updateItem.setType(dbItemData.getItemType().getCode());
                    updateItemList.add(updateItem);
                    //如果是明细，需要把明细项删除，并生成新的明细项（重绘的情况下不用处理，因为整个工单会生成全新的数据）
                    if (!renderNode && dbItemData.getItemType() == FormItemTypeEnum.GROUP) {
                        deleteIdList.add(itemDataID);
                        //删除旧数据
                        List<TicketFormItemData> deleteListTemp = ticketFormItemDataService.selectTicketFormItemByParentId(itemDataID);
                        if (CollectionUtils.isNotEmpty(deleteListTemp)) {
                            for (TicketFormItemData ticketFormItemData : deleteListTemp) {
                                dbTtemDataDtoMap.remove(ticketFormItemData.getId());
                                deleteIdList.add(ticketFormItemData.getId());
                            }
                        }
                        //生成新的明细项
                        Response<List<TicketFormItemData>> ticketFormItemDataListRsp =
                                TicketDataServiceInner.buildTicketFormItemDataList(
                                        ticketFormDataDto.getTicketDataId(),
                                        BeanUtil.toBean(ticketFormDataDto, TicketFormData.class),
                                        allDbItemTemplates,
                                        Arrays.asList(updateItem)
                                );
                        if (!ticketFormItemDataListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
                            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "修改工单，构建表单组件数据失败");
                        }
                        insertItemList.addAll(ticketFormItemDataListRsp.getData());
                    }
                }
                allItemList.add(updateItem);
            }
            //删除
            else if (StringUtils.isNotEmpty(itemDataID) && StringUtils.isBlank(updateItem.getValue())) {
                TicketFormItemDataDto dbItemData = dbTtemDataDtoMap.remove(itemDataID);
                delSb.append(String.format(" %s:%s", dbItemData.getItemLabel(), cutString(dbItemData.getItemValue(), 30)));
                JSONObject config = JSONObject.parseObject(dbItemData.getItemConfig());
                if (config != null) {
                    if (Objects.equals(config.get("renderNode"), "YES")) {
                        renderNode = true;
                    }
                    if (config.containsKey("extendKey")) {
                        updateExtendedMap.putIfAbsent(config.getString("extendKey"), updateItem.getValue());
                    }
                }
                //标记修改
                if (StringUtils.isNotEmpty(dbItemData.getItemLabel())) {
                    updateTemplateNameSet.add(dbItemData.getItemLabel());
                }
                if (StringUtils.isNotEmpty(dbItemData.getTemplateId()) && !"-1".equals(dbItemData.getTemplateId())) {
                    updateTemplateIDSet.add(dbItemData.getTemplateId());
                }
                deleteIdList.add(itemDataID);
                //如果是明细，需要把明细项删除，（重绘的情况下不用处理，因为整个工单会生成全新的数据）
                if (!renderNode && dbItemData.getItemType() == FormItemTypeEnum.GROUP) {
                    List<TicketFormItemData> deleteListTemp = ticketFormItemDataService.selectTicketFormItemByParentId(itemDataID);
                    // 记录删除日志
                    formItemDataDtosLog.addAll(deleteListTemp);

                    for (TicketFormItemData ticketFormItemData : deleteListTemp) {
                        dbTtemDataDtoMap.remove(ticketFormItemData.getId());
                        deleteIdList.add(ticketFormItemData.getId());
                    }
                }
            }
            //新增
            else if (StringUtils.isNotBlank(updateItem.getTemplateId()) && !templateIDToDataIDMap.containsKey(updateItem.getTemplateId()) && StringUtils.isNotBlank(updateItem.getValue())) {
                TicketFormItemTemplate template = templateIDMap.get(updateItem.getTemplateId());
                log.info("工单{}表单项新增,templateId={}, value={}", ticketFormDataDto.getTicketDataId(), updateItem.getTemplateId(), updateItem.getValue());
                if (template != null && template.getItemConfig() != null) {
                    JSONObject config = JSONObject.parseObject(template.getItemConfig());
                    if (config != null && Objects.equals(config.get("renderNode"), "YES")) {
                        renderNode = true;
                    }
                }
                //重绘工单流程，无需单独生成表单项
                if (!renderNode) {
                    //生成新的表单项
                    Response<List<TicketFormItemData>> ticketFormItemDataListRsp = TicketDataServiceInner.buildTicketFormItemDataList(ticketFormDataDto.getTicketDataId(), BeanUtil.toBean(ticketFormDataDto, TicketFormData.class), allDbItemTemplates, Arrays.asList(updateItem));
                    if (!ticketFormItemDataListRsp.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
                        return new Response<>(null, BizResponseEnums.UPDATE_ERROR, "构建表单组件数据失败" + ticketFormItemDataListRsp.getMsg());
                    }
                    List<TicketFormItemData> newTicketItemDataList = ticketFormItemDataListRsp.getData();
                    if (CollectionUtils.isNotEmpty(newTicketItemDataList)) {
                        for (TicketFormItemData itemData : newTicketItemDataList) {
                            String itemConfig = itemData.getItemConfigExt();
                            itemData.EqConfig();
                            JSONObject config = JSONObject.parseObject(itemConfig);
                            if (config != null && config.containsKey("extendKey")) {
                                updateExtendedMap.putIfAbsent(config.getString("extendKey"), updateItem.getValue());
                            }
                            addSb.append(String.format(" %s:%s", itemData.getItemLabel(), cutString(updateItem.getValue(), 30)));
                            insertItemList.add(itemData);
                        }
                    }
                }
                //标记修改
                if (StringUtils.isNotEmpty(updateItem.getTemplateId()) && !"-1".equals(updateItem.getTemplateId())) {
                    updateTemplateIDSet.add(updateItem.getTemplateId());
                }
                allItemList.add(updateItem);
            } else {
                log.warn("工单{}修改数据{}异常", ticketDataDto.getId(), JSONObject.toJSONString(updateItem));
            }
        }

        String modifyStr = "";
        JSONObject dealJson = new JSONObject();
        if (StringUtils.isNotEmpty(ticketFormUpdateDto.getDealOpinion())) {
            if (ticketFormUpdateDto.getDealOpinion().startsWith("{") && ticketFormUpdateDto.getDealOpinion().endsWith("}")) {
                dealJson = JSONObject.parseObject(ticketFormUpdateDto.getDealOpinion());
                modifyStr = dealJson.getString("commentStrInfo");
            } else {
                modifyStr = ticketFormUpdateDto.getDealOpinion();
            }
        }
        dealJson.put("commentStrInfo", modifyStr);
        String autoStr = "";
        if (addSb.length() > 0) {
            autoStr = String.format("%s新增项(%s); ", autoStr, addSb);
        }
        if (editSb.length() > 0) {
            autoStr = String.format("%s修改项(%s); ", autoStr, editSb);
        }
        if (delSb.length() > 0) {
            autoStr = String.format("%s删除项(%s); ", autoStr, delSb);
        }
        dealJson.put("commentAutoInfo", cutString(autoStr, 4000));
        String dealOpinionStr = JSONObject.toJSONString(dealJson);
        //执行前事件
        HashMap<String, String> beforeParamsMap = new HashMap<>();
        beforeParamsMap.put("detail_user_id", applyUser.getAccountId());
        beforeParamsMap.put("detail_user_name", applyUser.getAccountName());
        beforeParamsMap.put("detail_user_type", applyUser.getAccountType());
        beforeParamsMap.put("detail_opinion", dealOpinionStr);
        beforeParamsMap.put("detail_type", ApproveDealTypeEnum.MODIFY.getCode());
        String detailTypeDes = ApproveDealTypeEnum.MODIFY.getDesc();
        if (StringUtils.isNotEmpty(ticketFormUpdateDto.getDealDescription())) {
            detailTypeDes = ticketFormUpdateDto.getDealDescription();
        }
        beforeParamsMap.put("detail_type_des", detailTypeDes);
        Response<String> beforeResult = ticketDataApproveService.executeEvent(ticketDataDto.getId(), ticketDataDto.getCurrentNodeId(), ExecuteStepEnum.BEFORE_UPDATE.getCode(), beforeParamsMap, null, null);
        if (!beforeResult.isSuccess()) {
            return beforeResult;
        }
        //true=重绘节点 false=仅做表单项值的修改
        if (renderNode) {

            //重绘工单流程，allStdList 补全
            for (TicketFormItemDataDto itemDataDto : dbTtemDataDtoMap.values()) {
                if (StringUtils.isNotBlank(itemDataDto.getItemParentId()) && itemDataDto.getItemParentId().contains(":")) {
                    continue;
                }
                TicketFormItemStdDto itemStdDto = new TicketFormItemStdDto();
                this.updateDecry(itemDataDto);
                itemStdDto.setTemplateId("-1".equals(itemDataDto.getTemplateId()) ? itemDataDto.getItemLabel() : itemDataDto.getTemplateId());
                itemStdDto.setType(itemDataDto.getItemType().getCode());
                itemStdDto.setValue(itemDataDto.getItemValue());
                if (StringUtils.isNotEmpty(itemDataDto.getItemConfig())) {
                    JSONObject itemConfigJSONObj = JSONObject.parseObject(itemDataDto.getItemConfig());
                    if (null != itemConfigJSONObj.get("displayValue")) {
                        itemStdDto.setDisplayValue(String.valueOf(itemConfigJSONObj.get("displayValue")));
                    }

                }
                allItemList.add(itemStdDto);
            }
            ticketFormUpdateDto.setFormItems(allItemList);
            //重绘工单流程节点
            Response response = this.doRenderNode(ticketDataDto, ticketFormUpdateDto, applyUser, dealOpinionStr);
            if (!response.isSuccess()) {
                return response;
            } else {
                // 不能全部删除 按照记录的来
                renderNodeAddItemLogs(ticketFormUpdateDto, ticketDataDto, updateTemplateIDSet, updateTemplateNameSet, userId, userName, now);
                return response;
            }

        } else {
            if (CollUtil.isNotEmpty(deleteIdList)) {
                ticketFormItemDataService.removeByIds(deleteIdList);

                if (CollUtil.isNotEmpty(formItemDataDtosLog)) {
                    batchAddItemDataLogs(formItemDataDtosLog, "DELETE", userId, userName, now);
                }

            }
            if (CollectionUtils.isNotEmpty(updateItemList)) {

                saveNonExistItemLogs(ticketDataDto, updateTemplateIDSet, userId, userName, now);

                for (TicketFormItemStdDto itemStdDto : updateItemList) {
                    TicketFormItemData ticketFormItemData = new TicketFormItemData();
                    ticketFormItemData.setItemValue(itemStdDto.getValue());

                    ticketFormItemData.setItemConfig("{}");
                    ticketFormItemData.setItemConfigExt("{}");
                    if (templateIDMap.get(itemStdDto.getTemplateId()) != null) {
                        Response response = this.updateEncrypt(itemStdDto, templateIDMap.get(itemStdDto.getTemplateId()), ticketFormItemData);
                        if (!response.isSuccess()) {
                            return response;
                        }
                    }
                    try {
                        if (templateIDToDataIDMap.get(itemStdDto.getTemplateId()) != null) {
                            ticketFormItemData.EqConfig();
                            ticketFormItemDataService.lambdaUpdate()
                                    .set(TicketFormItemData::getItemValue, ticketFormItemData.getItemValue())
                                    .set(TicketFormItemData::getItemConfig, "{}")
                                    .set(TicketFormItemData::getItemConfigExt, ticketFormItemData.getItemConfigExt())
                                    .set(TicketFormItemData::getUpdateTime, now)
                                    .set(TicketFormItemData::getUpdateBy, userId)
                                    .eq(TicketFormItemData::getId, templateIDToDataIDMap.get(itemStdDto.getTemplateId())).update();

                            saveNormalItemLogsByTplIDToDataMap(templateIDToDataIDMap, templateIDToDataMap, itemStdDto, ticketDataDto, userId, userName, now);


                        } else if (nameToDataIDMap.get(itemStdDto.getTemplateId()) != null) {
                            ticketFormItemData.EqConfig();
                            ticketFormItemDataService.lambdaUpdate()
                                    .set(TicketFormItemData::getItemValue, ticketFormItemData.getItemValue())
                                    .set(TicketFormItemData::getItemConfig, "{}")
                                    .set(TicketFormItemData::getItemConfigExt, ticketFormItemData.getItemConfigExt())
                                    .set(TicketFormItemData::getUpdateTime, now)
                                    .set(TicketFormItemData::getUpdateBy, userId)
                                    .eq(TicketFormItemData::getId, nameToDataIDMap.get(itemStdDto.getTemplateId())).update();

                            saveNomalItemLogsByNameToDataIDMap(templateIDToDataIDMap, nameToDataIDMap, itemStdDto, ticketDataDto, userId, userName, now);

                        }
                    } catch (Exception e) {
                        if (e.getMessage().contains("Incorrect string value")) {
                            log.error("update字符编码错误 - ticketDataID: {} itemID: {} itemValue: {}", ticketDataDto.getId(),
                                    nameToDataIDMap.get(itemStdDto.getTemplateId()), ticketFormItemData.getItemValue());
                        }
                        throw e;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(insertItemList)) {

                try {
                    ticketFormItemDataService.saveBatch(insertItemList);
                    List<TicketFormItemDataLog> itemLogs = toTicketFormItemDataLogList(insertItemList, "ADD", userId, userName, now);
                    ticketFormItemDataLogService.saveBatch(itemLogs);
                } catch (Exception e) {
                    if (e.getMessage().contains("Incorrect string value")) {
                        log.error("update字符编码错误 - ticketDataID: {}", ticketDataDto.getId());
                        log.error("字符编码错误 - insertItemList: {}", insertItemList);
                        log.error("update字符编码错误 - ticketDataID: {} itemList: {} ", ticketDataDto.getId(), insertItemList);
                    }
                    throw e;
                }
            }

            if (updateExtendedMap.size() > 0) {
                var lambda = ticketDataService.lambdaUpdate().isNull(TicketData::getDeleteTime).eq(TicketData::getId, ticketDataDto.getId());
                var hasSet = false;
                if (updateExtendedMap.containsKey("extend1")) {
                    lambda.set(TicketData::getExtend1, updateExtendedMap.get("extend1"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend2")) {
                    lambda.set(TicketData::getExtend2, updateExtendedMap.get("extend2"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend3")) {
                    lambda.set(TicketData::getExtend3, updateExtendedMap.get("extend3"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend4")) {
                    lambda.set(TicketData::getExtend4, updateExtendedMap.get("extend4"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend5")) {
                    lambda.set(TicketData::getExtend5, updateExtendedMap.get("extend5"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend6")) {
                    lambda.set(TicketData::getExtend6, updateExtendedMap.get("extend6"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend7")) {
                    lambda.set(TicketData::getExtend7, updateExtendedMap.get("extend7"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend8")) {
                    lambda.set(TicketData::getExtend8, updateExtendedMap.get("extend8"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend9")) {
                    lambda.set(TicketData::getExtend9, updateExtendedMap.get("extend9"));
                    hasSet = true;
                }
                if (updateExtendedMap.containsKey("extend10")) {
                    lambda.set(TicketData::getExtend10, updateExtendedMap.get("extend10"));
                    hasSet = true;
                }
                if (hasSet) {
                    lambda.update();
                }
            }

            TicketFlowNodeApproveDetail flowNodeApproveDetail = new TicketFlowNodeApproveDetail();
            flowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
            flowNodeApproveDetail.setTicketDataId(ticketDataDto.getId());
            flowNodeApproveDetail.setTicketFlowNodeDataId(ticketDataDto.getCurrentNodeId());
            flowNodeApproveDetail.setDealUserId(applyUser.getAccountId());
            flowNodeApproveDetail.setDealUserType(applyUser.getAccountType());
            if (StringUtils.isNotEmpty(ticketFormUpdateDto.getDealDescription())) {
                flowNodeApproveDetail.setDealTypeDescription(ticketFormUpdateDto.getDealDescription());
            } else {
                flowNodeApproveDetail.setDealTypeDescription(ApproveDealTypeEnum.MODIFY.getDesc());
            }
            flowNodeApproveDetail.setDealUserName(applyUser.getAccountName());
            flowNodeApproveDetail.setDealType(ApproveDealTypeEnum.MODIFY);
            flowNodeApproveDetail.setDealOpinion(dealOpinionStr);
            flowNodeApproveDetail.setUpdateBy(dealUserStr);
            flowNodeApproveDetail.setUpdateTime(now);
            flowNodeApproveDetail.setCreateBy(dealUserStr);
            flowNodeApproveDetail.setCreateTime(now);
            boolean result = ticketFlowNodeApproveDetailService.save(flowNodeApproveDetail);
            if (result) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("detail_id", flowNodeApproveDetail.getId());
                paramsMap.put("detail_user_id", flowNodeApproveDetail.getDealUserId());
                paramsMap.put("detail_user_name", flowNodeApproveDetail.getDealUserName());
                paramsMap.put("detail_user_type", flowNodeApproveDetail.getDealUserType());
                paramsMap.put("detail_opinion", flowNodeApproveDetail.getDealOpinion());
                paramsMap.put("detail_type", flowNodeApproveDetail.getDealType().getCode());
                paramsMap.put("detail_type_des", detailTypeDes);
                Response<String> afterResult = ticketDataApproveService.executeEvent(ticketDataDto.getId(), ticketDataDto.getCurrentNodeId(), ExecuteStepEnum.DONE_UPDATE.getCode(), paramsMap, null, null);
                if (!afterResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
                    return afterResult;
                }
            }
            //只更新表单项的值
            return new Response<>(null, BizResponseEnums.SUCCESS, "更新工单成功");
        }
    }

    private void saveNonExistItemLogs(TicketDataDto ticketDataDto, Set<String> updateTemplateIDSet, String userId, String userName, Date now){

        CompletableFuture.runAsync(() -> {

            try {
                List<TicketFormItemDataDto> oldTicketFormItemDataDtoList = Optional.ofNullable(ticketDataDto)
                        .map(TicketDataDto::getTicketFormDataDto)
                        .map(TicketFormDataDto::getTicketFormItemDataDtoList)
                        .orElse(Collections.emptyList());

                if (CollUtil.isEmpty(oldTicketFormItemDataDtoList)) {
                    return;
                }

                // 更具 tplid 筛选出 被更新的老数据
                List<TicketFormItemDataLog> matchedOldItemList = oldTicketFormItemDataDtoList.stream()
                        .filter(Objects::nonNull)
                        .filter(dto -> dto.getTemplateId() != null && updateTemplateIDSet.contains(dto.getTemplateId()))
                        .map(this::toTicketFormItemDataLog)
                        .filter(Objects::nonNull)  // 防止转换过程中产生null值
                        .collect(Collectors.toList());
                //Set<String> isExistsLog = ticketFormItemDataLogService.batchCheckOldItemLogExists(matchedOldItemList);

                if (CollUtil.isEmpty(matchedOldItemList)) {
                    return;
                }

                Set<String> isExistsLog = CollUtil.isEmpty(matchedOldItemList) ?
                        Collections.emptySet() :
                        ticketFormItemDataLogService.lambdaQuery()
                                .select(TicketFormItemDataLog::getTemplateId)
                                .and(wrapper -> {
                                    matchedOldItemList.forEach(item ->
                                            wrapper.or(w -> w
                                                    .eq(TicketFormItemDataLog::getTicketDataId, item.getTicketDataId())
                                                    .eq(TicketFormItemDataLog::getItemType, item.getItemType())
                                                    .eq(TicketFormItemDataLog::getItemLabel, item.getItemLabel())
                                                    .eq(TicketFormItemDataLog::getTemplateId, item.getTemplateId())
                                            )
                                    );
                                })
                                .list()
                                .stream()
                                .map(TicketFormItemDataLog::getTemplateId)
                                .collect(Collectors.toSet());

                List<TicketFormItemDataLog> notExistsList = matchedOldItemList.stream()
                        .filter(item -> !isExistsLog.contains(item.getTemplateId()))
                        .peek(item -> {
                            item.setOperType("ADD");
                            item.setOperTime(now);
                            item.setOperId(userId);
                            item.setOperBy(userName);
                        })
                        .collect(Collectors.toList());

                if (CollUtil.isNotEmpty(notExistsList)) {
                    ticketFormItemDataLogService.saveBatch(notExistsList);
                }
            } catch (Exception e) {
                log.error("saveNonExistItemLogs ticketDataId{}, itemId{}, error{}", ticketDataDto.getId(), updateTemplateIDSet.toString(), e.getMessage());
            }

        }, executor);

    }

    private void saveNormalItemLogsByTplIDToDataMap(HashMap<String, String> templateIDToDataIDMap,
                                                    HashMap<String, TicketFormItemDataDto> templateIDToDataMap,
                                                    TicketFormItemStdDto itemStdDto, TicketDataDto ticketDataDto,
                                                    String userId, String userName, Date now)
    {

        try {
            Optional<TicketFormItemData> updateItem = ticketFormItemDataService.lambdaQuery()
                    .eq(TicketFormItemData::getId, templateIDToDataIDMap.get(itemStdDto.getTemplateId()))
                    .isNull(TicketFormItemData::getDeleteTime).oneOpt();
            updateItem.ifPresent(formItemData -> saveTicketFormItemDataLog(formItemData, null, ticketDataDto, formItemData.getId(), "UPDATE", userId, userName, now));
        } catch (Exception e) {
            log.error("saveTicketFormItemDataLog templateIDToDataIDMap error {} and templateId {}", templateIDToDataMap.get(itemStdDto.getTemplateId()), itemStdDto.getTemplateId());
        }
    }

    private void saveNomalItemLogsByNameToDataIDMap(HashMap<String, String> templateIDToDataIDMap,
                                                    HashMap<String, String> nameToDataIDMap,
                                                    TicketFormItemStdDto itemStdDto, TicketDataDto ticketDataDto,
                                                    String userId, String userName, Date now)
    {

        try {
            Optional<TicketFormItemData> updateItem = ticketFormItemDataService.lambdaQuery().eq(TicketFormItemData::getId, templateIDToDataIDMap.get(itemStdDto.getTemplateId())).isNull(TicketFormItemData::getDeleteTime).oneOpt();
            updateItem.ifPresent(formItemData -> saveTicketFormItemDataLog(formItemData, null, ticketDataDto, formItemData.getId(), "UPDATE", userId, userName, now));
        } catch (Exception e) {
            log.error("saveTicketFormItemDataLog nameToDataIDMap error {} and templateId {}", nameToDataIDMap.get(itemStdDto.getTemplateId()), itemStdDto.getTemplateId());
        }
    }

    private void renderNodeAddItemLogs(TicketFormUpdateDto ticketFormUpdateDto, TicketDataDto ticketDataDto,
                                       Set<String> updateTemplateIDSet, Set<String> updateTemplateNameSet,
                                       String userId, String userName, Date now)
    {

        List<TicketFormItemDataDto> oldTicketFormItemDataDtoList = Optional.ofNullable(ticketDataDto)
                .map(TicketDataDto::getTicketFormDataDto)
                .map(TicketFormDataDto::getTicketFormItemDataDtoList)
                .orElse(Collections.emptyList());

        if (CollUtil.isEmpty(oldTicketFormItemDataDtoList)) {
            return;
        }

        CompletableFuture.runAsync(() -> {

            // 同时根据 ID 和 Name 筛选并记录日志
            List<TicketFormItemDataDto> matchedOldList = oldTicketFormItemDataDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(dto -> dto.getId() != null && updateTemplateIDSet.contains(dto.getId()) ||
                            dto.getItemLabel() != null && updateTemplateNameSet.contains(dto.getItemLabel()))
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(matchedOldList)) {

                try {
                    List<TicketFormItemDataLog> matchedDeleteItemList = matchedOldList.stream()
                            .filter(Objects::nonNull)
                            .filter(dto -> dto.getTemplateId() != null && updateTemplateIDSet.contains(dto.getTemplateId()))
                            .map(this::toTicketFormItemDataLog)
                            .filter(Objects::nonNull)  // 防止转换过程中产生null值
                            .collect(Collectors.toList());

                    Set<String> isExistsLog = CollUtil.isEmpty(matchedDeleteItemList) ?
                            Collections.emptySet() :
                            ticketFormItemDataLogService.lambdaQuery()
                                    .select(TicketFormItemDataLog::getTemplateId)
                                    .and(wrapper -> {
                                        matchedDeleteItemList.forEach(item ->
                                                wrapper.or(w -> w
                                                        .eq(TicketFormItemDataLog::getTicketDataId, item.getTicketDataId())
                                                        .eq(TicketFormItemDataLog::getItemType, item.getItemType())
                                                        .eq(TicketFormItemDataLog::getItemLabel, item.getItemLabel())
                                                        .eq(TicketFormItemDataLog::getTemplateId, item.getTemplateId())
                                                )
                                        );
                                    })
                                    .list()
                                    .stream()
                                    .map(TicketFormItemDataLog::getTemplateId)
                                    .collect(Collectors.toSet());

                    DateTime delTime = DateUtil.offsetSecond(now, -1);
                    List<TicketFormItemDataLog> notExistsList = matchedDeleteItemList.stream()
                            .filter(item -> !isExistsLog.contains(item.getTemplateId()))
                            .peek(item -> {
                                item.setOperType("DELETE");
                                item.setOperTime(delTime);
                                item.setOperId(userId);
                                item.setOperBy(userName);
                            })
                            .collect(Collectors.toList());

                    if (CollUtil.isNotEmpty(notExistsList)) {
                        ticketFormItemDataLogService.saveBatch(notExistsList);
                    }
                } catch (Exception e) {
                    log.error("renderNodeAddItemLogs save delete log error {} and templateId {}", ticketDataDto.getId(), updateTemplateIDSet.toString(), e.getMessage());
                }
            }

            if (ObjectUtil.isNotEmpty(ticketFormUpdateDto) && ticketFormUpdateDto.getTicketDataId() != null) {

                List<TicketFormItemData> diffUpdateList = ticketFormItemDataService.lambdaQuery()
                        .eq(TicketFormItemData::getTicketDataId, ticketFormUpdateDto.getTicketDataId())
                        .and(wrapper -> wrapper
                                .in(CollUtil.isNotEmpty(updateTemplateNameSet), TicketFormItemData::getItemLabel, updateTemplateNameSet)  // itemLabel在集合中
                                .or()
                                .in(CollUtil.isNotEmpty(updateTemplateIDSet), TicketFormItemData::getTemplateId, updateTemplateIDSet)    // 或者templateId在集合中
                        ).list();
                if (CollUtil.isNotEmpty(diffUpdateList)) {
                    DateTime dateTime = DateUtil.offsetSecond(now, 2);
                    batchAddItemDataLogs(diffUpdateList, "UPDATE", userId, userName, dateTime);
                }
            }


            //添加老数据中 含有 updateTag 的部分
            oldTicketFormItemDataDtoList.stream()
                    .filter(Objects::nonNull)
                    .filter(dto -> hasUpdateTag(dto))
                    .forEach(dto -> {
                        if (StrUtil.isNotBlank(dto.getTemplateId())) {
                            updateTemplateIDSet.add(dto.getTemplateId());
                        }
                        if (StrUtil.isNotBlank(dto.getItemLabel())) {
                            updateTemplateNameSet.add(dto.getItemLabel());
                        }
                    });

            List<TicketFormItemData> matchedNewItems = ticketFormItemDataService.lambdaQuery()
                    .eq(TicketFormItemData::getTicketDataId, ticketDataDto.getId())
                    .and(wrapper -> wrapper
                            .in(TicketFormItemData::getTemplateId, updateTemplateIDSet)
                            .or()
                            .in(TicketFormItemData::getItemLabel, updateTemplateNameSet)
                    )
                    .list().stream()
                    .peek(item -> {

                        if (StrUtil.isNotBlank(item.getItemConfig())) {
                            try {
                                JSONObject json = JSON.parseObject(item.getItemConfig());
                                json.put("updateTag", "TRUE");
                                item.setItemConfig(json.toJSONString());
                            } catch (Exception e) {
                                log.error("Error add updateTag itemConfig with ID: {} and exception {}", item.getId(), e.getMessage());
                            }
                        }

                        if (StrUtil.isNotBlank(item.getItemConfigExt())) {
                            try {
                                JSONObject json = JSON.parseObject(item.getItemConfigExt());
                                json.put("updateTag", "TRUE");
                                item.setItemConfigExt(json.toJSONString());
                            } catch (Exception e) {
                                log.error("Error add updateTag to itemConfigExt with ID: {} and exception {}", item.getId(), e.getMessage());
                            }
                        }
                    })
                    .collect(Collectors.toList());
            if (!matchedNewItems.isEmpty()) {
                ticketFormItemDataService.saveOrUpdateBatch(matchedNewItems);
            }
        }, executor);
    }

    private boolean hasUpdateTag(TicketFormItemDataDto dto){
        // 检查 itemConfig
        if (StrUtil.isNotBlank(dto.getItemConfig())) {
            try {
                JSONObject configJson = JSON.parseObject(dto.getItemConfig());
                if (configJson.containsKey("updateTag")) {
                    return true;
                }
            } catch (Exception e) {
                // 忽略 JSON 解析异常
            }
        }
        return false;
    }

    public void saveTicketFormItemDataLog(TicketFormItemData ticketFormItemData,
                                          TicketFormItemDataDto ticketFormItemDataDto,
                                          TicketDataDto ticketDataDto,
                                          String ticketItemId, String operType, String operId, String operBy, Date operTime)
    {

        List<TicketFormItemDataDto> oldTicketFormItemDataDtoList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList();
        if (CollUtil.isEmpty(oldTicketFormItemDataDtoList)) {
            return;
        }

        if (ObjectUtil.isNotEmpty(ticketFormItemData)) {

            String ticketDataId = ticketFormItemData.getTicketDataId();
            String itemId = ticketFormItemData.getId();

            CompletableFuture.runAsync(() -> {
                try {
                    TicketFormItemDataLog itemDataLog = toTicketFormItemDataLog(ticketFormItemData);

                    itemDataLog.setTicketFormItemDataId(ticketItemId);  // 将 ID 设置为日志表中的 ticket_form_item_data_id
                    itemDataLog.setOperType(operType);
                    itemDataLog.setOperTime(operTime);
                    itemDataLog.setOperId(operId);
                    itemDataLog.setOperBy(operBy);

                    // 保存日志
                    ticketFormItemDataLogService.save(itemDataLog);
                } catch (Exception e) {
                    // 记录日志异常
                    log.error("saveTicketFormItemDataLog Error insert log for ticketData ID: {} itemId:{} and exception {}"
                            , ticketDataId, itemId, e.getMessage());
                }
            }, executor);
        }

        if (ObjectUtil.isNotEmpty(ticketFormItemDataDto)) {

            String ticketDataId = ticketFormItemDataDto.getTicketDataId();
            String itemId = ticketFormItemDataDto.getId();

            CompletableFuture.runAsync(() -> {
                try {
                    TicketFormItemDataLog itemDataLog = toTicketFormItemDataLog(ticketFormItemDataDto);

                    itemDataLog.setTicketFormItemDataId(ticketItemId);  // 将 ID 设置为日志表中的 ticket_form_item_data_id
                    itemDataLog.setOperType(operType);
                    itemDataLog.setOperTime(operTime);
                    itemDataLog.setOperId(operId);
                    itemDataLog.setOperBy(operBy);

                    // 保存日志
                    ticketFormItemDataLogService.save(itemDataLog);
                } catch (Exception e) {
                    // 记录日志异常
                    log.error("saveTicketFormItemDataLog Error insert log for ticketDataDto ID: {} itemId:{} and exception {}"
                            , ticketDataId, itemId, e.getMessage());
                }
            }, executor);
        }

    }


    private void batchAddItemDataLogs(List<TicketFormItemData> ticketFormItemData, String operType, String operId, String operBy, Date operTime){

        if (CollUtil.isNotEmpty(ticketFormItemData)) {
            List<TicketFormItemDataLog> collect = ticketFormItemData.stream()
                    .filter(Objects::nonNull) // 过滤空值
                    .map(this::toTicketFormItemDataLog)
                    .peek(itemDataLog -> {
                        itemDataLog.setOperType(operType);
                        itemDataLog.setOperId(operId);
                        itemDataLog.setOperBy(operBy);
                        itemDataLog.setOperTime(operTime);

                    })
                    .collect(Collectors.toList());
            ticketFormItemDataLogService.saveBatch(collect);
        }

    }

    private List<TicketFormItemDataLog> toTicketFormItemDataLogList(List<TicketFormItemData> ticketFormItemDataList, String operType, String operId, String operBy, Date operTime){

        if (CollUtil.isEmpty(ticketFormItemDataList)) {
            return new ArrayList<>();
        }

        return ticketFormItemDataList.stream()
                .filter(Objects::nonNull) // 过滤空值
                .map(this::toTicketFormItemDataLog)
                .peek(itemDataLog -> {
                    itemDataLog.setOperType(operType);
                    itemDataLog.setOperId(operId);
                    itemDataLog.setOperBy(operBy);
                    itemDataLog.setOperTime(operTime);
                }).collect(Collectors.toList());
    }


    private TicketFormItemDataLog toTicketFormItemDataLog(TicketFormItemData ticketFormItemData){

        TicketFormItemDataLog itemDataLog = new TicketFormItemDataLog();

        if (StrUtil.isNotBlank(ticketFormItemData.getId())) {
            itemDataLog.setTicketFormItemDataId(ticketFormItemData.getId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTicketDataId())) {
            itemDataLog.setTicketDataId(ticketFormItemData.getTicketDataId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTicketFormDataId())) {
            itemDataLog.setTicketFormDataId(ticketFormItemData.getTicketFormDataId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTemplateId())) {
            itemDataLog.setTemplateId(ticketFormItemData.getTemplateId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemParentId())) {
            itemDataLog.setItemParentId(ticketFormItemData.getItemParentId());
        }

        Integer itemOrder = ticketFormItemData.getItemOrder();
        if (itemOrder != null && itemOrder >= 0) {
            itemDataLog.setItemOrder(itemOrder);
        } else {
            itemDataLog.setItemOrder(0); // 默认设置为0
        }

        FormItemTypeEnum itemType = ticketFormItemData.getItemType();
        if (itemType != null) {
            itemDataLog.setItemType(itemType);
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemLabel())) {
            itemDataLog.setItemLabel(ticketFormItemData.getItemLabel());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemConfig())) {
            itemDataLog.setItemConfig(ticketFormItemData.getItemConfig());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemValue())) {
            itemDataLog.setItemValue(ticketFormItemData.getItemValue());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemRequired().getCode())) {
            itemDataLog.setItemRequired(ticketFormItemData.getItemRequired().getCode());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemTips())) {
            itemDataLog.setItemTips(ticketFormItemData.getItemTips());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemAdvancedSearch().getCode())) {
            itemDataLog.setItemAdvancedSearch(ticketFormItemData.getItemAdvancedSearch().getCode());
        }

        return itemDataLog;
    }

    private TicketFormItemDataLog toTicketFormItemDataLog(TicketFormItemDataDto ticketFormItemData){

        TicketFormItemDataLog itemDataLog = new TicketFormItemDataLog();

        if (StrUtil.isNotBlank(ticketFormItemData.getId())) {
            itemDataLog.setTicketFormItemDataId(ticketFormItemData.getId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTicketDataId())) {
            itemDataLog.setTicketDataId(ticketFormItemData.getTicketDataId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTicketFormDataId())) {
            itemDataLog.setTicketFormDataId(ticketFormItemData.getTicketFormDataId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getTemplateId())) {
            itemDataLog.setTemplateId(ticketFormItemData.getTemplateId());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemParentId())) {
            itemDataLog.setItemParentId(ticketFormItemData.getItemParentId());
        }

        Integer itemOrder = ticketFormItemData.getItemOrder();
        if (itemOrder != null && itemOrder >= 0) {
            itemDataLog.setItemOrder(itemOrder);
        } else {
            itemDataLog.setItemOrder(0); // 默认设置为0
        }

        FormItemTypeEnum itemType = ticketFormItemData.getItemType();
        if (itemType != null) {
            itemDataLog.setItemType(itemType);
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemLabel())) {
            itemDataLog.setItemLabel(ticketFormItemData.getItemLabel());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemConfig())) {
            itemDataLog.setItemConfig(ticketFormItemData.getItemConfig());
        }

//        if (StrUtil.isNotBlank(ticketFormItemData.getItemConfigExt())) {
//            itemDataLog.setItemConfigExt(ticketFormItemData.getItemConfigExt());
//        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemValue())) {
            itemDataLog.setItemValue(ticketFormItemData.getItemValue());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemRequired())) {
            itemDataLog.setItemRequired(ticketFormItemData.getItemRequired());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemTips())) {
            itemDataLog.setItemTips(ticketFormItemData.getItemTips());
        }

        if (StrUtil.isNotBlank(ticketFormItemData.getItemAdvancedSearch())) {
            itemDataLog.setItemAdvancedSearch(ticketFormItemData.getItemAdvancedSearch());
        }

        return itemDataLog;
    }

    private Response updateEncrypt(TicketFormItemStdDto item, TicketFormItemTemplate templateInfo, TicketFormItemData data){

        String itemValue = item.getValue();
        String itemMaskValue = com.smy.tfs.common.utils.StringUtils.toMaskString(itemValue);
        String itemConfig = templateInfo.getItemConfig();
        //先设置初始值
        data.setItemConfig("{}");
        data.setItemConfigExt(itemConfig);
        data.setItemValue(itemValue);

        //如果有加密配置，则设置加密值
        if (ObjectHelper.isNotEmpty(itemConfig)) {
            JSONObject itemConfigJsonObj = JSONObject.parseObject(itemConfig);
            if (ObjectHelper.isNotEmpty(itemConfigJsonObj.get("isEncrypted"))) {
                Boolean isEncrypted = (Boolean) itemConfigJsonObj.get("isEncrypted");
                if (isEncrypted) {
                    try {
                        String ciphertext = AesUtil.encrypt(itemValue);
                        itemConfigJsonObj.put("ciphertext", ciphertext);
                        data.setItemConfig("{}");
                        data.setItemConfigExt(JSONUtil.toJsonStr(itemConfigJsonObj));
                    } catch (Exception e) {
                        String errorTips = String.format("表单项(id为%s)的明文(%s)加密失败", data, itemValue);
                        log.error(errorTips, e);
                        return Response.error(BizResponseEnums.DES_ERROR, errorTips);
                    }
                    data.setItemValue(itemMaskValue);
                }
            }
            //设置显示值
            if (StringUtils.isNotEmpty(item.getDisplayValue())) {
                itemConfigJsonObj.put("displayValue", item.getDisplayValue());
                data.setItemConfig("{}");
                data.setItemConfigExt(JSONUtil.toJsonStr(itemConfigJsonObj));
            }

            //设置更新标记
            com.alibaba.fastjson2.JSONObject updateTag = com.alibaba.fastjson2.JSON.parseObject(data.getItemConfig());
            com.alibaba.fastjson2.JSONObject updateTagExt = com.alibaba.fastjson2.JSON.parseObject(data.getItemConfigExt());
            if (StrUtil.isEmpty(data.getItemConfig())) {
                updateTag = new com.alibaba.fastjson2.JSONObject();
            }
            if (StrUtil.isEmpty(data.getItemConfigExt())) {
                updateTagExt = new com.alibaba.fastjson2.JSONObject();
            }
            updateTag.put("updateTag", "TRUE");
            updateTagExt.put("updateTag", "TRUE");
            data.setItemConfig(updateTag.toJSONString());
            data.setItemConfigExt(updateTagExt.toJSONString());

        }
        return Response.success();
    }

    private Response updateDecry(TicketFormItemDataDto data){

        if (data == null) {
            return Response.success();
        }
        String itemConfig = data.getItemConfig();
        if (ObjectHelper.isNotEmpty(itemConfig)) {
            JSONObject itemConfigJsonObj = JSONObject.parseObject(itemConfig);
            if (itemConfigJsonObj.containsKey("isEncrypted") && itemConfigJsonObj.containsKey("ciphertext")) {
                Boolean isEncrypted = (Boolean) itemConfigJsonObj.get("isEncrypted");
                String encryValue = itemConfigJsonObj.getString("ciphertext");
                if (isEncrypted && StringUtils.isNotEmpty(encryValue)) {
                    try {
                        String itemValue = AesUtil.decrypt(encryValue);
                        data.setItemValue(itemValue);
                    } catch (Exception e) {
                        String errorTips = String.format("表单项(id为%s)的明文(%s)解密失败", data.getId(), encryValue);
                        log.error(errorTips, e);
                        return Response.error(BizResponseEnums.DES_ERROR, errorTips);
                    }
                }
            }
        }
        return Response.success();
    }

    private boolean canEdit(JSONObject canModifyFiledJson, String itemLabel){
        //如果没有指定字段，则所有字段可以修改
        if (canModifyFiledJson == null) {
            return true;
        }
        //如果指定字段，不包含当前字段，则不可修改
        if (canModifyFiledJson.get(itemLabel) == null) {
            return false;
        }
        //如果指定那个字段，并且包含当前字段，isEdit=true表示可以修改
        return canModifyFiledJson.getJSONObject(itemLabel).getBoolean("isEdit");
    }

    private TicketFlowNodeApproveDetail addApproveDetail(TicketDataDto ticketDataDto, AccountInfo
            accountInfo, String nodeId, String modifyContent, String dealTypeDes)
    {

        Date now = new Date();
        TicketFlowNodeApproveDetail flowNodeApproveDetail = new TicketFlowNodeApproveDetail();
        flowNodeApproveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        flowNodeApproveDetail.setTicketDataId(ticketDataDto.getId());
        flowNodeApproveDetail.setTicketFlowNodeDataId(nodeId);
        flowNodeApproveDetail.setDealUserId(accountInfo.getAccountId());
        flowNodeApproveDetail.setDealUserType(accountInfo.getAccountType());
        flowNodeApproveDetail.setDealUserName(accountInfo.getAccountName());
        flowNodeApproveDetail.setDealType(ApproveDealTypeEnum.MODIFY);
        if (StringUtils.isNotEmpty(dealTypeDes)) {
            flowNodeApproveDetail.setDealTypeDescription(dealTypeDes);
        } else {
            flowNodeApproveDetail.setDealTypeDescription(ApproveDealTypeEnum.MODIFY.getDesc());
        }
        flowNodeApproveDetail.setDealOpinion(modifyContent);
        String dealUserStr = JSONUtil.toJsonStr(accountInfo);
        flowNodeApproveDetail.setUpdateBy(dealUserStr);
        flowNodeApproveDetail.setUpdateTime(now);
        flowNodeApproveDetail.setCreateBy(dealUserStr);
        flowNodeApproveDetail.setCreateTime(now);
        boolean result = ticketFlowNodeApproveDetailService.save(flowNodeApproveDetail);
        if (!result) {
            throw new RuntimeException("保存记录失败");
        }
        return flowNodeApproveDetail;
    }

    /**
     * 修改工单，重绘节点
     *
     * @param ticketDataDto
     * @param ticketFormUpdateDto
     * @return
     */
    private Response doRenderNode(TicketDataDto ticketDataDto, TicketFormUpdateDto ticketFormUpdateDto, AccountInfo applyUser, String modifyContent){
        /*====2. 计算需要删除的值, 表单，节点，事件，执行组====*/
        //要删除的表单id
        String deleteFormDataId = ticketDataDto.getTicketFormDataDto().getId();
        //要删除的表单项列表ID
        List<String> deleteFormItemIdList = ticketDataDto.getTicketFormDataDto().getTicketFormItemDataDtoList().stream().map(TicketFormItemDataDto::getId).collect(Collectors.toList());
        //用来衔接的Node，也就是前面发送过企业微信卡片的节点，在需要将其disable，手动置为成功
        TicketFlowNodeDataDto linkNode = null;
        //要删除的节点ID
        List<String> deleteNodeIdList = new ArrayList<>();
        //要删除的事件ID
        List<String> deleteEventIdList = new ArrayList<>();
        //要删除的执行组ID
        List<String> deleteExecutorIdList = new ArrayList<>();
        //要删除的动作ID
        List<String> deleteActionIdList = new ArrayList<>();
        for (TicketFlowNodeDataDto node : ticketDataDto.getTicketFlowDataDto().getTicketFlowNodeDataDtoList()) {
            if (Objects.equals(ticketDataDto.getCurrentNodeId(), node.getId())) {
                linkNode = node;
            }
            if (!Objects.equals(node.getNodeStatus(), NodeStatusEnum.APPROVE_PASS) && !Objects.equals(node.getNodeStatus(), NodeStatusEnum.APPROVE_REJECT) && !Objects.equals(node.getNodeStatus(), NodeStatusEnum.APPROVING)) {
                deleteNodeIdList.add(node.getId());
                if (CollectionUtils.isNotEmpty(node.getExcutorList())) {
                    deleteExecutorIdList.addAll(node.getExcutorList().stream().map(TicketFlowNodeExecutorDataDto::getId).collect(Collectors.toList()));
                }

                List<TicketFlowEventData> eventList = flowEventDataService.getEventList(node.getTicketDataId(), node.getId(), null);
                if (CollectionUtils.isNotEmpty(eventList)) {
                    deleteEventIdList.addAll(eventList.stream().map(TicketFlowEventData::getId).collect(Collectors.toList()));
                }
                List<TicketFlowNodeActionData> actionList = ticketFlowNodeActionDataService.lambdaQuery()
                        .eq(TicketFlowNodeActionData::getTicketDataId, node.getTicketDataId())
                        .eq(TicketFlowNodeActionData::getTicketFlowNodeDataId, node.getId()).list();
                if (CollectionUtils.isNotEmpty(actionList)) {
                    deleteActionIdList.addAll(actionList.stream().map(TicketFlowNodeActionData::getId).collect(Collectors.toList()));
                }
            }
        }
        /*====计算需要删除的值, 表单，节点，事件，执行组====*/

        /*====3. 计算需要新增的值, 表单，节点，事件，执行组====*/
        //生成新的数据
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(ticketFormUpdateDto.getTicketDataId());
        ticketDataStdDto.setFormItems(ticketFormUpdateDto.getFormItems());
        ticketDataStdDto.setFlowNodes(ticketFormUpdateDto.getFlowNodes());
        ticketDataStdDto.setTicketTemplateId(ticketDataDto.getTemplateId());
        //TODO 重绘节点， 申请人信息需要传工单创建人，而不是当前修改人
        AccountInfo accountInfo = JSONObject.parseObject(ticketDataDto.getApplyUser(), AccountInfo.class);
        Response<TicketDataAllBO> midBOResponse = updateTicket(ticketDataStdDto, accountInfo, ticketDataDto.getTicketFlowDataDto().getId());
        if (midBOResponse == null || !midBOResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单[%s]表单更新，构建工单数据失败。%s", ticketFormUpdateDto.getTicketDataId(), midBOResponse.getMsg()));
        }
        TicketDataAllBO ticketDataAllBO = midBOResponse.getData();

        UpdateTicketFormDataMidBO updateTicketFormDataBO = new UpdateTicketFormDataMidBO(
                ticketDataAllBO.getTicketData(),
                ticketDataAllBO.getTicketFlowData(),
                ticketDataAllBO.getTicketFlowNodeDataList(),
                ticketDataAllBO.getTicketFlowNodeExecutorDataList(),
                ticketDataAllBO.getTicketFlowNodeActionDataList(),
                ticketDataAllBO.getTicketFlowEventDataList(),
                ticketDataAllBO.getTicketFormData(),
                ticketDataAllBO.getTicketFormItemDataList()
        );
        final TicketFlowNodeDataDto linkNodeFinal = linkNode;
        AtomicReference<TicketFlowNodeApproveDetail> atoApproveDetail = new AtomicReference<>();
        /*====计算需要新增的值, 表单，节点，事件，执行组====*/
        try {
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                // 先更新工单数据，和其他流程顺序一样，防止死锁。
                if (updateTicketFormDataBO.getUpdateTicketData() != null && !ticketDataService.updateById(updateTicketFormDataBO.getUpdateTicketData())) {
                    throw new RuntimeException("工单更新失败");
                }

                //结束老的当前节点
                this.updateEndNode(linkNodeFinal, applyUser);

                //新增操作明细
                TicketFlowNodeApproveDetail ticketApproveDetail = this.addApproveDetail(new TicketDataDto(updateTicketFormDataBO.getUpdateTicketData()), applyUser, linkNodeFinal.getId(), modifyContent, ticketFormUpdateDto.getDealDescription());
                atoApproveDetail.set(ticketApproveDetail);

                updateTicketFormDataBO.getUpdateTicketFlowData().setId(ticketDataDto.getTicketFlowDataDto().getId());

                if (updateTicketFormDataBO.getUpdateTicketFlowData() != null && !ticketFlowDataService.updateById(updateTicketFormDataBO.getUpdateTicketFlowData())) {
                    throw new RuntimeException("工单流程更新失败");
                }
                if (deleteFormDataId != null && !ticketFormDataService.removeById(deleteFormDataId)) {
                    throw new RuntimeException("旧表单删除失败");
                }
                if (updateTicketFormDataBO.getNewTicketFormData() != null && !ticketFormDataService.save(updateTicketFormDataBO.getNewTicketFormData())) {
                    throw new RuntimeException("新表单新增失败");
                }
                if (CollectionUtils.isNotEmpty(deleteFormItemIdList) && !ticketFormItemDataService.removeByIds(deleteFormItemIdList)) {
                    throw new RuntimeException("旧表单项删除失败");
                }
                if (CollectionUtils.isNotEmpty(updateTicketFormDataBO.getNewTicketFormItemDataList()) && !ticketFormItemDataService.saveBatch(updateTicketFormDataBO.getNewTicketFormItemDataList())) {
                    throw new RuntimeException("新表单项新增失败");
                }
                if (CollectionUtils.isNotEmpty(deleteNodeIdList) && !ticketFlowNodeDataService.removeByIds(deleteNodeIdList)) {
                    throw new RuntimeException("旧节点删除失败");
                }
                if (CollectionUtils.isNotEmpty(updateTicketFormDataBO.getNewTicketFlowNodeDataList())) {
                    for (TicketFlowNodeData flowNodeData : updateTicketFormDataBO.getNewTicketFlowNodeDataList()) {
                        if (Objects.equals(flowNodeData.getPreNodeId(), "-1")) {
                            flowNodeData.setPreNodeId(linkNodeFinal.getId());
                        }
                    }
                    if (!ticketFlowNodeDataService.saveBatch(updateTicketFormDataBO.getNewTicketFlowNodeDataList())) {
                        throw new RuntimeException("新节点新增失败");
                    }
                }
                if (CollectionUtils.isNotEmpty(deleteEventIdList) && !ticketFlowEventDataService.removeByIds(deleteEventIdList)) {
                    throw new RuntimeException("旧事件删除失败");
                }
                if (CollectionUtils.isNotEmpty(updateTicketFormDataBO.getNewTicketFlowEventDataList()) && !ticketFlowEventDataService.saveBatch(updateTicketFormDataBO.getNewTicketFlowEventDataList())) {
                    throw new RuntimeException("新事件新增失败");
                }
                if (CollectionUtils.isNotEmpty(deleteExecutorIdList) && !ticketFlowNodeExecutorDataService.removeByIds(deleteExecutorIdList)) {
                    throw new RuntimeException("旧执行组删除失败");
                }
                if (CollectionUtils.isNotEmpty(updateTicketFormDataBO.getNewTicketFlowNodeExecutorDataList()) && !ticketFlowNodeExecutorDataService.saveBatch(updateTicketFormDataBO.getNewTicketFlowNodeExecutorDataList())) {
                    throw new RuntimeException("新执行组新增失败");
                }
                if (CollectionUtils.isNotEmpty(deleteActionIdList) && !ticketFlowNodeActionDataService.removeByIds(deleteActionIdList)) {
                    throw new RuntimeException("旧执行动作删除失败");
                }
                if (CollectionUtils.isNotEmpty(updateTicketFormDataBO.getNewTicketFlowNodeActionDataList()) && !ticketFlowNodeActionDataService.saveBatch(updateTicketFormDataBO.getNewTicketFlowNodeActionDataList())) {
                    throw new RuntimeException("新执行动作新增失败");
                }
            });
        } catch (Exception e) {
            log.error("更新工单持久化失败.", e);
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "更新工单失败");
        }

        if (atoApproveDetail.get() == null) {
            log.error("修改工单，操作日志为空");
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "操作日志为空,更新工单失败");
        }
        //新增节点执行结果
        TicketFlowNodeApproveDetail approveDetail = atoApproveDetail.get();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("node_name", ticketDataDto.getCurrentNodeName());
        paramsMap.put("detail_id", approveDetail.getId());
        paramsMap.put("detail_user_id", approveDetail.getDealUserId());
        paramsMap.put("detail_user_name", approveDetail.getDealUserName());
        paramsMap.put("detail_user_type", approveDetail.getDealUserType());
        paramsMap.put("detail_opinion", approveDetail.getDealOpinion());
        paramsMap.put("detail_type", approveDetail.getDealType().getCode());
        paramsMap.put("detail_type_des", approveDetail.getDealTypeDescription());
        //首先执行当前审批节点的执行前动作，如果执行失败，则终止流程。
        Response<String> beforeResult = ticketDataApproveService.executeEvent(approveDetail.getTicketDataId(), linkNode.getId(), ExecuteStepEnum.DONE_UPDATE.getCode(), paramsMap, null, null);
        if (!beforeResult.getCode().equals(BizResponseEnums.SUCCESS.getCode())) {
            log.error("执行修改后事件失败:" + beforeResult.getMsg(), beforeResult);
        }
        //将原有的节点审批卡片disable
        ticketDataApproveService.disable(ticketDataDto, linkNode, null, "数据已修改");
        //触发新增节点的自动审批，如果不是自动审批，则不做实际处理
        if (StringUtils.isNotEmpty(ticketDataAllBO.getTicketData().getCurrentNodeId())
                && !"-1".equals(ticketDataAllBO.getTicketData().getCurrentNodeId())
                && CollectionUtils.isNotEmpty(ticketDataAllBO.getTicketFlowNodeDataList()))
        {
            var currentNodeOpt = ticketDataAllBO.getTicketFlowNodeDataList().stream().filter(x -> x.getId() == ticketDataAllBO.getTicketData().getCurrentNodeId()).findAny();
            if (currentNodeOpt.isPresent()) {
                ticketDataApproveService.autoApprove(currentNodeOpt.get().getTicketDataId(), currentNodeOpt.get().getAuditedType(), currentNodeOpt.get().getId());
            }
        }
        //4. 推送更新事件, 推送事件不影响工单更新，如果失败，后续使用其他方式补偿
        try {
            List<TicketFlowEventData> flowEventDataList = ticketFlowEventDataService.getTicketUpdateEventList(ticketDataDto.getId());

            Response<String> eventResult = ticketDataApproveService.executeEventList(ticketDataDto.getId(), ticketDataDto.getInterfaceKey(), flowEventDataList, null, null, null);
            if (!eventResult.getEnum().equals(BizResponseEnums.SUCCESS)) {
                log.error("工单[{}]更新推送失败，需要重试", ticketDataDto.getId());
                return new Response<>(null, BizResponseEnums.SUCCESS, "更新工单成功,推送业务方事件执行失败");
            } else {
                return new Response<>(null, BizResponseEnums.SUCCESS, "更新工单成功");
            }
        } catch (
                Exception e) {
            log.error("工单[{}]更新推送失败，需要重试", ticketDataDto.getId(), e);
            return new Response<>(null, BizResponseEnums.SUCCESS, "更新工单成功, 推送业务方事件执行失败");
        }
    }

    private void updateEndNode(TicketFlowNodeDataDto currentNode, AccountInfo accountInfo){

        Date date = new Date();
        String dealUserStr = JSONUtil.toJsonStr(accountInfo);
//        TicketFlowNodeData addNodeData = new TicketFlowNodeData();
//        addNodeData.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_DATA));
//        addNodeData.setNodeName(currentNode.getNodeName());
//        addNodeData.setPreNodeId(currentNode.getPreNodeId());
//        addNodeData.setTemplateId("-99");
//        addNodeData.setTicketDataId(currentNode.getTicketDataId());
//        addNodeData.setTicketFlowDataId(currentNode.getTicketFlowDataId());
//        addNodeData.setAuditedMethod(currentNode.getAuditedMethod());
//        addNodeData.setAuditedType(AuditedType.AUTO_PASS);
//        addNodeData.setNodeOrder(currentNode.getNodeOrder());
//        addNodeData.setCreateBy(dealUserStr);
//        addNodeData.setUpdateBy(dealUserStr);
//        addNodeData.setCreateTime(date);
//        addNodeData.setUpdateTime(date);
//        //默认审批通过
//        addNodeData.setNodeStatus(NodeStatusEnum.APPROVE_PASS);
//        flowNodeDataService.save(addNodeData);
//        return addNodeData.getId();
        //新逻辑
        boolean updateRes = flowNodeDataService.lambdaUpdate().isNull(TicketFlowNodeData::getDeleteTime).eq(TicketFlowNodeData::getId, currentNode.getId())
                .set(TicketFlowNodeData::getNodeStatus, NodeStatusEnum.APPROVE_PASS)
                .set(TicketFlowNodeData::getNodeWxDealCardCode, "")
                .set(TicketFlowNodeData::getNodeWxDealCardMessageId, "")
                .set(TicketFlowNodeData::getUpdateTime, date)
                .set(TicketFlowNodeData::getUpdateBy, dealUserStr)
                .update();
        if (!updateRes) {
            throw new RuntimeException("更新最后一个节点失败");
        }
    }

    public boolean authCheck(String userId, String appId, String dealUsers){

        TicketRemoteAccountDto ticketRemoteAccountDto = ticketAccountService.getTicketRemoteAccountByIdAndApp(userId, appId);
        if (ticketRemoteAccountDto == null) {
            log.warn("账号信息不存在，appId={},账号ID={}", appId, userId);
            return false;
        }
        AccountInfo accountInfo = new AccountInfo(ticketRemoteAccountDto.getSameOriginId(), ticketRemoteAccountDto.getUserType(), ticketRemoteAccountDto.getUserId(), ticketRemoteAccountDto.getUserName());
        List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(dealUsers);
        return ticketDataApproveService.inList(accountInfo, accountInfoList);
    }

    @Override
    public Response<String> dispatchTicket(TicketDispatchDto ticketDispatchDto, String userType, String
            userID, String userName)
    {

        TicketData ticketData = this.getById(ticketDispatchDto.getTicketDataId());
        if (ticketData.getTicketStatus() != TicketDataStatusEnum.APPLYING) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, String.format("工单ID:%s,状态不为审批中", ticketDispatchDto.getTicketDataId()));
        }
        List<AccountInfo> accountInfoList = new ArrayList<>();
        if (StringUtils.isNotEmpty(ticketDispatchDto.getExecutorType())
                && ExecutorTypeEnum.APPLY_GROUP.getCode().equals(ticketDispatchDto.getExecutorType()))
        {
            List<TicketExecutorGroup> ticketExecutorGroupList = ticketExecutorGroupService.lambdaQuery()
                    .select(TicketExecutorGroup::getAccountInfo)
                    .in(TicketExecutorGroup::getId, ticketDispatchDto.getAccountIdList())
                    .isNull(TicketExecutorGroup::getDeleteTime)
                    .list();
            List<String> accountInfoStrList = ticketExecutorGroupList.stream()
                    .filter(it -> StringUtils.isNotEmpty(it.getAccountInfo()))
                    .map(it -> it.getAccountInfo()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(accountInfoStrList)) {
                accountInfoList = AccountInfo.ToAccountInfoList(accountInfoStrList);
            }
        } else {
            accountInfoList = ticketAccountMappingService.getAccountInfoByAccountIdAndType(ticketDispatchDto.getAccountIdList(), ticketDispatchDto.getAccountType());
        }
        String accountInfoStr = AccountInfo.ToAccountInfoListStr(accountInfoList);

        String newDealUserStr = "";
        if (CollectionUtils.isNotEmpty(accountInfoList)) {
            for (var item : accountInfoList) {
                if ("".equals(newDealUserStr)) {
                    newDealUserStr = item.getAccountName();
                } else {
                    newDealUserStr = newDealUserStr + "," + item.getAccountName();
                }
            }
        }
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userID, userType);
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getSameOriginId())) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "未找到有效用户信息");
        }
        AccountInfo accountInfo = new AccountInfo(ticketAccountMapping.getSameOriginId(), userType, userID, userName);
        String createBy = accountInfo.ToJsonString();
        String updateBy = createBy;
        //新增操作记录
        TicketFlowNodeApproveDetail approveDetail = new TicketFlowNodeApproveDetail();
        approveDetail.setId(SequenceUtil.getId(TFSTableIdCode.ID_TICKET_FLOW_NODE_APPROVE_DETAIL));
        approveDetail.setTicketDataId(ticketData.getId());
        approveDetail.setTicketFlowNodeDataId(ticketData.getCurrentNodeId());
        approveDetail.setDealUserType(userType);
        approveDetail.setDealUserId(userID);
        approveDetail.setDealUserName(userName);
        approveDetail.setDealType(ApproveDealTypeEnum.DISPATCH);
        approveDetail.setDealOpinion(userName + "指派处理人：" + newDealUserStr);
        approveDetail.setCreateBy(createBy);
        approveDetail.setUpdateBy(updateBy);

        //新增审批人信息
        TicketFlowNodeExecutorData ticketFlowNodeExecutorData = new TicketFlowNodeExecutorData();
        ticketFlowNodeExecutorData.setTemplateId("-1");
        ticketFlowNodeExecutorData.setTicketDataId(ticketData.getId());
        ticketFlowNodeExecutorData.setTicketFlowNodeDataId(ticketData.getCurrentNodeId());
        ticketFlowNodeExecutorData.setExecutorType(ExecutorTypeEnum.APPLY_MEMBER_LIST);
        ticketFlowNodeExecutorData.setExecutorValue(accountInfoStr);
        ticketFlowNodeExecutorData.setExecutorList(accountInfoStr);
        ticketFlowNodeExecutorData.setExecutorDoneList("");
        ticketFlowNodeExecutorData.setCreateBy(createBy);
        ticketFlowNodeExecutorData.setUpdateBy(updateBy);

        Date now = new Date();

        //执行派单前事件
        HashMap<String, String> beforeParamsMap = new HashMap<>();
        beforeParamsMap.put("detail_type_des", "派单");
        beforeParamsMap.put("ticket_data_id", ticketDispatchDto.getTicketDataId());
        beforeParamsMap.put("account_type", ticketDispatchDto.getAccountType());
        beforeParamsMap.put("executor_type", ticketDispatchDto.getExecutorType());
        beforeParamsMap.put("executor_id_list", JSONObject.toJSONString(ticketDispatchDto.getAccountIdList()));
        Response<String> beforeResult = ticketDataApproveService.executeEvent(ticketData.getId(), ticketData.getCurrentNodeId(), ExecuteStepEnum.BEFORE_DISPATCH.getCode(), beforeParamsMap, null, null);
        if (!beforeResult.isSuccess()) {
            return beforeResult;
        }

        transactionTemplate.execute((status) -> {
            //先删除原先审批人信息
            ticketFlowNodeExecutorDataService.lambdaUpdate()
                    .set(TfsBaseEntity::getDeleteTime, now)
                    .eq(TicketFlowNodeExecutorData::getTicketDataId, ticketData.getId())
                    .eq(TicketFlowNodeExecutorData::getTicketFlowNodeDataId, ticketData.getCurrentNodeId())
                    .isNull(TicketFlowNodeExecutorData::getDeleteTime)
                    .likeRight(TicketFlowNodeExecutorData::getExecutorType, "APPLY_")
                    .update();

            //新增操作记录
            ticketFlowNodeApproveDetailService.save(approveDetail);
            //新增审批人信息
            ticketFlowNodeExecutorDataService.save(ticketFlowNodeExecutorData);
            //更新工单数据信息
            this.lambdaUpdate()
                    .set(TicketData::getCurrentDealUsers, accountInfoStr)
                    .set(TicketData::getUpdateTime, now)
                    .eq(TicketData::getId, ticketData.getId())
                    .eq(TicketData::getCurrentDealUsers, ticketData.getCurrentDealUsers())
                    .isNull(TicketData::getDeleteTime)
                    .update();
            return true;
        });

        //数据查询, 工单数据
        Response<TicketDataDto> ticketDataDtoResponse = ticketDataService.selectFullTicketDataById(new ReqParam(ticketData.getId()));
        if (!ticketDataDtoResponse.isSuccess()) {
            return new Response<>(null, BizResponseEnums.QUERY_ERROR, "工单数据重新查询失败");
        }
        var ticketDataDto = ticketDataDtoResponse.getData();
        if (ticketData.getTicketMsgArriveType() == TicketMsgArriveTypeEnum.WECOM) {
            String title = "【派单】{{apply_user}}提交的{{ticket_name}}，待你处理"
                    .replace("{{apply_user}}", ticketDataDto.getApplyUserName())
                    .replace("{{ticket_name}}", ticketDataDto.getTicketName());
            var urgeRes = notificationBizService.SendDealCard(
                    title,
                    ticketDataDto,
                    new AccountInfo("", userType, userID, userName),
                    ApproveDealTypeEnum.SEND,
                    accountInfoList,
                    false,
                    null
            );
            if (urgeRes.getEnum() != BizResponseEnums.SUCCESS) {
                log.error("派单成功，通知下一个节点审批人失败，错误信息：{}", urgeRes.getMsg());
            }
        }
        return Response.success("派单成功");
    }

    @Override
    public Response<TicketBatchDto> batchDispatchTicket(BatchTicketDispatchDto batchTicketDispatchDto, String userType, String userId, String userName){

        if (CollectionUtils.isEmpty(batchTicketDispatchDto.getTicketDataIdList())
                || StringUtils.isEmpty(batchTicketDispatchDto.getExecutorType())
                || CollectionUtils.isEmpty(batchTicketDispatchDto.getAccountIdList()))
        {
            throw new ServiceException("工单id列表、派单对象类型、用户组/账户成员信息不能为空不能为空");
        }
        if (ExecutorTypeEnum.APPLY_MEMBER_LIST.getCode().equals(batchTicketDispatchDto.getExecutorType())
                && StringUtils.isEmpty(batchTicketDispatchDto.getAccountType()))
        {
            throw new ServiceException("账户体系类型不能为空");
        }
        TicketBatchDto ticketBatchDto = new TicketBatchDto();
        List<BatchDto> batchDtoList = new ArrayList<>();
        List<String> ticketDataIdList = batchTicketDispatchDto.getTicketDataIdList();
        for (String ticketDataId : ticketDataIdList) {
            TicketDispatchDto ticketDispatchDto = new TicketDispatchDto();
            ticketDispatchDto.setTicketDataId(ticketDataId);
            ticketDispatchDto.setExecutorType(batchTicketDispatchDto.getExecutorType());
            ticketDispatchDto.setAccountType(batchTicketDispatchDto.getAccountType());
            ticketDispatchDto.setAccountIdList(batchTicketDispatchDto.getAccountIdList());
            Response<String> response = ticketDataService.dispatchTicket(ticketDispatchDto, userType, userId, userName);
            if (!response.isSuccess()) {
                log.error(String.format("工单(id:%s)派单异常：%s", ticketDataId, response.getMsg()));
                String errorMsg = String.format("工单(id:%s)处理失败", ticketDataId);
                BatchDto batchDto = new BatchDto();
                batchDto.setId(ticketDataId);
                batchDto.setErroMsg(errorMsg);
                batchDtoList.add(batchDto);
            }
        }
        ticketBatchDto.setFailedList(batchDtoList);
        return Response.success(ticketBatchDto);
    }

    public Response<TicketDataAllBO> updateTicket(TicketDataStdDto ticketDataStdDto, AccountInfo applyUser, String
            flowId)
    {

        if (ticketDataStdDto == null || StringUtils.isBlank(ticketDataStdDto.getApplyId())) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "参数缺失");
        }
        TicketData updateTicketData = this.baseMapper.selectById(ticketDataStdDto.getApplyId());// for update
        if (updateTicketData == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "工单数据不存在");
        }
        String ticketTemplateID = ticketDataStdDto.getTicketTemplateId();//工单模版ID
        //获取模版数据
        TicketTemplateAllBO ticketTemplateAllBO = getTicketTemplateAll(ticketTemplateID);
        //数据校验
        Response templateCheckRps = CreateTicketParamCheck(ticketDataStdDto.getApplyId(), updateTicketData, ticketTemplateAllBO);
        if (!templateCheckRps.isSuccess()) {
            return templateCheckRps;
        }
        //声明审批数据
        TicketFlowData updateTicketFlowData = new TicketFlowData();
        updateTicketFlowData.setId(flowId);

        //构建工单数据
        var buildTicketDataAllBORsp = buildTicketDataAllBO(ticketTemplateAllBO, ticketDataStdDto, updateTicketData, applyUser, flowId);
        if (!buildTicketDataAllBORsp.isSuccess()) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "构建工单数据失败：" + buildTicketDataAllBORsp.getMsg());
        }

        TicketDataAllBO ticketDataAllBO = buildTicketDataAllBORsp.getData();

        return new Response<>(ticketDataAllBO, BizResponseEnums.SUCCESS, "工单构建成功");
    }

    //截取一定参数的字符串
    private String cutString(String inputStr, int length){

        if (inputStr == null) {
            return null;
        }
        if (inputStr.length() <= length) {
            return inputStr;
        }
        return inputStr.substring(0, length);
    }

}