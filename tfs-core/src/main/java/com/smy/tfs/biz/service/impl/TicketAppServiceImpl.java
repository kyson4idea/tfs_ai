package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smy.framework.core.config.Property;
import com.smy.tfs.api.constants.QWCardMD;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dbo.TicketData;
import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dbo.TicketFormItemTemplate;
import com.smy.tfs.api.dto.*;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.dto.dynamic.TicketDataStdDto;
import com.smy.tfs.api.dto.dynamic.TicketFormItemStdDto;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.CallBackMsgStatusEnum;
import com.smy.tfs.api.enums.TicketAnalysisDataTypeEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.config.TfSJumpUrlProperties;
import com.smy.tfs.biz.config.TfsSmyConstant;
import com.smy.tfs.biz.mapper.TicketAppMapper;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.biz.util.BusiTicketDataUtil;
import com.smy.tfs.common.core.domain.AjaxResult;
import com.smy.tfs.common.core.domain.entity.SysUser;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.bean.BeanHelper;
import com.smy.tfs.common.utils.bean.BeanUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.common.utils.notification.QwNotify;
import com.smy.tfs.system.service.ISysRoleService;
import com.smy.tfs.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Component("ticketAppServiceImpl")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "业务相关服务类", apiInterface = ITicketAppService.class)
@Slf4j
public class TicketAppServiceImpl extends ServiceImpl<TicketAppMapper, TicketApp> implements ITicketAppService {

    @Resource
    private ISysUserService sysUserService;
    @Resource
    private ITicketDataService ticketDataService;
    @Resource
    private ISysRoleService roleService;
    @Resource
    private ITicketFormItemTemplateService ticketFormItemTemplateService;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    @Value("${tfs.app.manager.role.id}")
    private String appManagerRoleId;

    @Resource
    private ITicketAccountService accountService;
    @Resource
    private TfSJumpUrlProperties tfSJumpUrlProperties;
    @Resource
    NotificationService notificationService;
    @Resource
    ITicketFlowNodeDataService ticketFlowNodeDataService;


    @Override
    @ApiDoc(value = "查询业务全部信息", description = "根据业务id查询业务全部信息")
    public TicketAppDto selectTicketAppFullById(String id) {
        TicketApp ticketApp = this.baseMapper.selectById(id);
        return BeanHelper.copyObject(ticketApp, TicketAppDto.class);
    }

    @Override
    public Response<TicketApp> queryTicketAppById(String id) {
        if (ObjectHelper.isEmpty(id))
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "id不能为空");
        TicketApp ticketApp = this.baseMapper.selectById(id);
        if (ObjectHelper.isEmpty(ticketApp))
            return Response.error(BizResponseEnums.QUERY_ERROR, "根据应用id查询的应用为空");
        return Response.success(ticketApp);
    }

    private Boolean existTicketAppById(String id) {
        return this.baseMapper.countTicketAppByIdAndName(id, null) > 0;
    }

    private Boolean countTicketAppByNameExtendId(String id, String appName) {
        return this.baseMapper.countTicketAppByNameExtendId(id, appName) > 0;
    }

    @Override
    @ApiDoc(value = "查询业务信息列表", description = "查询业务信息列表")
    public Response<List<TicketAppDto>> selectTicketAppListWithDayAnalysis(TicketAppDto ticketAppDto) {
        ticketAppDto.setCategory(TicketAnalysisDataTypeEnum.DAY.getCode());
        DateTime yesterday = DateUtil.offsetDay(new Date(), -1);
        ticketAppDto.setStartDateStr(DateUtil.formatDateTime(DateUtil.beginOfDay(yesterday)));
        ticketAppDto.setEndDateStr(DateUtil.formatDateTime(DateUtil.endOfDay(yesterday)));

        List<TicketAppDto> ticketAppDtoList = this.baseMapper.selectTicketAppList(ticketAppDto);
        String username = SecurityUtils.getOriginUserInfoForSearch();

        String analysisDateStr = DateUtil.formatDate(yesterday);

        for (TicketAppDto appDto : ticketAppDtoList) {
            appDto.setAnalysisDateStr(analysisDateStr);
            Integer ticketAvgEfficiency = appDto.getTicketAvgEfficiency();
            if (ticketAvgEfficiency != null && ticketAvgEfficiency > 0) {
                String ticketAvgEfficiencyStr = String.format("%.2f", ticketAvgEfficiency / (60.00 * 60));
                appDto.setTicketAvgEfficiencyStr(ticketAvgEfficiencyStr + "h");
            }
            appDto.setAppAdminUserList(AccountInfo.parseAccountInfoStrToUserList(appDto.getAppAdminUsers()));
            //appDto.setIsAppAdmin(SecurityUtils.isAdmin() || appDto.getAppAdminUsers().contains(username));
            appDto.setIsAppAdmin(SecurityUtils.isAdmin());
        }
        return Response.success(ticketAppDtoList);
    }

    @Override
    public Response<List<TicketAppDto>> selectOnlyTicketAppList(boolean needControl) {
        LambdaQueryWrapper<TicketApp> queryWrapper = new LambdaQueryWrapper<>();
        if (needControl && !SecurityUtils.isAdmin()) {
            String username = SecurityUtils.getOriginUserInfoForSearch();
            queryWrapper.like(TicketApp::getAppAdminUsers, username);
        }
        queryWrapper.orderByDesc(TicketApp::getCreateTime);

        List<TicketApp> ticketApps = this.list(queryWrapper);
        return Response.success(BeanHelper.copyList(ticketApps, TicketAppDto.class));
    }

    @Override
    public Page<TicketAppDto> selectTicketAppPage(PageParam<TicketAppDto> page) {
        return null;
    }

    @Override
    @ApiDoc(value = "创建业务信息", description = "创建业务信息")
    public Response<String> createTicketApp(TicketAppDto ticketAppDto) {
        if (!checkAppIdValid(ticketAppDto.getId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务唯一键不符合规范，仅允许字母、数字、下划线和短横线");
        }
        if (existTicketAppById(ticketAppDto.getId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务唯一键已存在，请重新输入");
        }
        if (countTicketAppByNameExtendId(null, ticketAppDto.getAppName())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务名称已存在，请重新输入");
        }
        doCreateTicketApp(ticketAppDto);
        return Response.success("创建业务成功");
    }

    private String doCreateTicketApp(TicketAppDto ticketAppDto) {
        List<String> userIdList = ticketAppDto.getAppAdminUserList().stream().map(user -> user.split("-")[0]).collect(Collectors.toList());
        List<AccountInfo> accountInfoList = ticketAccountMappingService.getAccountInfoByAccountIdAndType(userIdList, TfsBaseConstant.defaultUserType);
        ticketAppDto.setAppAdminUsers(AccountInfo.ToAccountInfoListStr(accountInfoList));
        TicketApp ticketApp = new TicketApp();
        BeanUtils.copyProperties(ticketAppDto, ticketApp);
        this.save(ticketApp);

        //授权业务管理员角色
        authorizeAppAdminRole(ticketAppDto.getAppAdminUserList());
        return ticketApp.getId();
    }

    @Override
    public Response<String> applyTicketApp(TicketAppDto ticketAppDto) {
        if (SecurityUtils.isAdmin()) {
            return createTicketApp(ticketAppDto);
        }

        if (!checkAppIdValid(ticketAppDto.getId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务唯一键不符合规范，仅允许字母、数字、下划线和短横线");
        }
        if (existTicketAppById(ticketAppDto.getId())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务唯一键已存在，请重新输入");
        }
        if (countTicketAppByNameExtendId(null, ticketAppDto.getAppName())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务名称已存在，请重新输入");
        }

        Response<String> applyIDRes = ticketDataService.getTicketApplyId("tfs");
        assert applyIDRes != null && Objects.equals(applyIDRes.getCode(), BizResponseEnums.SUCCESS.getCode());
        String applyID = applyIDRes.getData();
        TicketDataStdDto ticketDataStdDto = new TicketDataStdDto();
        ticketDataStdDto.setApplyId(applyID);

        //目前写死,可以改成配置
        String applyAppTemplate = Property.getProperty(TfsBaseConstant.TFS_APPLY_APP_TEMPLATE);
        if (StrUtil.isBlank(applyAppTemplate)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务申请模板ID不能为空");
        }
        List<TicketFormItemTemplate> ticketFormItemTemplateList = ticketFormItemTemplateService.lambdaQuery().eq(TicketFormItemTemplate::getTicketTemplateId, applyAppTemplate)
                .isNull(TicketFormItemTemplate::getDeleteTime).list();
        if (CollUtil.isEmpty(ticketFormItemTemplateList)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务申请模板ID不存在,未找到对应的模版表单内容");
        }

        //生成模版map
        Map<String, String> templateItemMap = ticketFormItemTemplateList.stream().collect(Collectors.toMap(TicketFormItemTemplate::getId, TicketFormItemTemplate::getItemLabel));

        //生成值map
        Map<String, String> formValueMap = new HashMap<>();
        formValueMap.put("业务唯一键", ticketAppDto.getId());
        formValueMap.put("业务名称", ticketAppDto.getAppName());
        formValueMap.put("业务描述", ticketAppDto.getAppDesc());
        formValueMap.put("业务管理员", ticketAppDto.getAppAdminUserList().toString());
        formValueMap.put("业务账户类型", ticketAppDto.getAccountType());

        ticketDataStdDto.setTicketTemplateId(applyAppTemplate);
        List<TicketFormItemStdDto> formItems = new ArrayList<>();
        for (String templateItemId : templateItemMap.keySet()) {
            formItems.add(new TicketFormItemStdDto(templateItemId, formValueMap.getOrDefault(templateItemMap.get(templateItemId), "")));
        }
        ticketDataStdDto.setFormItems(formItems);
        ticketDataService.createTicket(ticketDataStdDto, SecurityUtils.getAccountUserType(),
                SecurityUtils.getAccountUserId(), SecurityUtils.getAccountUserName());
        return Response.success("申请成功，请前往工单列表跟踪审批");
    }


    private void authorizeAppAdminRole(List<String> appAdminUserList) {
        List<String> userNameList = appAdminUserList.stream().map(userFullName -> {
            return userFullName.split("-", 2)[0];
        }).collect(Collectors.toList());
        List<SysUser> sysUsers = sysUserService.selectUserListByUserName(userNameList);
        for (SysUser sysUser : sysUsers) {
            sysUserService.addSystemUserRole(sysUser.getUserId(), Long.valueOf(appManagerRoleId));
        }

    }

    /**
     * 检查手动输入的appId是否符合规则
     *
     * @param appId
     * @return
     */
    private Boolean checkAppIdValid(String appId) {
        // 正则表达式模式，允许字母、数字、下划线和短横线
        Pattern pattern = Pattern.compile("^[\\w\\-]+$");
        Matcher matcher = pattern.matcher(appId);
        return matcher.matches();
    }

    @Override
    public Response<TicketAppDto> updateTicketAppFull(TicketAppDto ticketAppDto) {
        if (countTicketAppByNameExtendId(ticketAppDto.getId(), ticketAppDto.getAppName())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "业务名称已存在，请重新输入");
        }
        List<String> userIdList = ticketAppDto.getAppAdminUserList().stream().map(user -> user.split("-")[0]).collect(Collectors.toList());
        List<AccountInfo> accountInfoList = ticketAccountMappingService.getAccountInfoByAccountIdAndType(userIdList, TfsBaseConstant.defaultUserType);
        ticketAppDto.setAppAdminUsers(AccountInfo.ToAccountInfoListStr(accountInfoList));

        TicketApp ticketAppDb = getById(ticketAppDto.getId());

        TicketApp ticketApp = new TicketApp();
        BeanUtils.copyProperties(ticketAppDto, ticketApp);
        this.updateById(ticketApp);

        //授权业务管理员角色 and 当用户没有业务管理员身份时，删掉角色
        authorizeAppAdminRole(ticketAppDto.getAppAdminUserList());
        deleteAuthorizeAppAdminRole(ticketAppDto.getAppAdminUsers(), ticketAppDb.getAppAdminUsers());

        return Response.success(ticketAppDto);
    }

    private void deleteAuthorizeAppAdminRole(String newAccountStr, String oldAccountStr) {
        List<AccountInfo> newAccountList = AccountInfo.ToAccountInfoList(newAccountStr);
        List<AccountInfo> oldAccountList = AccountInfo.ToAccountInfoList(oldAccountStr);
        oldAccountList.removeAll(newAccountList);
        if (CollUtil.isEmpty(oldAccountList)) {
            return;
        }

        //如果不为空，说明被删除了，判断当前用户是否还在其他应用有管理员
        List<AccountInfo> needDeleteIdList = new ArrayList<>();
        for (AccountInfo accountInfo : oldAccountList) {
            if (!checkAccountHasAppAdmin(accountInfo)) {
                needDeleteIdList.add(accountInfo);
            }
        }

        List<String> userNameList = needDeleteIdList.stream().map(AccountInfo::getAccountId).collect(Collectors.toList());
        if (CollUtil.isEmpty(userNameList)) {
            return;
        }
        List<SysUser> sysUsers = sysUserService.selectUserListByUserName(userNameList);
        List<Long> userIdList = sysUsers.stream().map(SysUser::getUserId).collect(Collectors.toList());
        if (CollUtil.isEmpty(userIdList)) {
            return;
        }
        Long[] userArray = new Long[userIdList.size()];
        userArray = userIdList.toArray(userArray);
        roleService.deleteAuthUsers(Long.valueOf(appManagerRoleId), userArray);
    }

    /**
     * 判断知道用户是否还有其他应用有管理员
     *
     * @param accountInfo
     * @return
     */
    private boolean checkAccountHasAppAdmin(AccountInfo accountInfo) {
        return this.lambdaQuery()
                .isNull(TicketApp::getDeleteTime)
                .like(TicketApp::getAppAdminUsers, accountInfo.ToJsonString())
                .count() > 0;
    }

    @Override
    public TicketAppDto updateTicketApp(TicketAppDto ticketAppDto) {
        return null;
    }

    @Override
    public void deleteTicketApp(String id) {
        TicketApp ticketAppDb = getById(id);

        TicketApp ticketApp = new TicketApp();
        ticketApp.setId(id);
        ticketApp.setDeleteTime(new Date());
        this.baseMapper.updateById(ticketApp);

        deleteAuthorizeAppAdminRole("", ticketAppDb.getAppAdminUsers());
    }

    @Override
    public Boolean checkCurrentUserIsAppAdmin(String appId) {
        boolean isSystemAdmin = SecurityUtils.isAdmin();
        if (isSystemAdmin) {
            return true;
        }
        String username = SecurityUtils.getOriginUserInfoForSearch();
        TicketApp ticketApp = this.getById(appId);
        return ticketApp.getAppAdminUsers().contains(username);
    }

    @Override
    public List<String> queryAdminAppListForCurrentUser() {
        String username = SecurityUtils.getOriginUserInfoForSearch();
        return queryAdminAppListForPointUser(username);
    }

    @Override
    public List<String> queryAdminAppListForPointUser(String userName) {
        LambdaQueryWrapper<TicketApp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(TicketApp::getAppAdminUsers, userName);

        List<TicketApp> ticketAppList = this.list(queryWrapper);
        return ticketAppList.stream().map(TicketApp::getId).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> selectNameMapByIdList(List<String> appIdList) {
        if (CollUtil.isEmpty(appIdList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<TicketApp> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TicketApp::getId, appIdList);
        List<TicketApp> ticketAppList = this.list(queryWrapper);

        return ticketAppList.stream().collect(Collectors.toMap(TicketApp::getId, TicketApp::getAppName));
    }

    @Autowired
    private TfsSmyConstant tfsSmyConstant;

    //建群并发送消息
    //涉及成员： 业务管理员+owen+yss+sy
    public Response<String> createQWGroupAndSendMsgByNode(String ticketFlowNodeDataId, CallBackMsgStatusEnum callBackMsgStatus) {
        if (ObjectHelper.isEmpty(ticketFlowNodeDataId)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("节点(id:%s)不存在", ticketFlowNodeDataId));
        }
        if (Objects.isNull(callBackMsgStatus) || CallBackMsgStatusEnum.SEND_INIT == callBackMsgStatus) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "消息通知类型为空或者错误");
        }
        Optional<TicketFlowNodeData> ticketFlowNodeDataOpt = ticketFlowNodeDataService.lambdaQuery()
                .eq(TicketFlowNodeData::getId, ticketFlowNodeDataId)
                .isNull(TicketFlowNodeData::getDeleteTime)
                .oneOpt();
        if (!ticketFlowNodeDataOpt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("节点数据(id:%s)不存在", ticketFlowNodeDataId));
        }
        TicketFlowNodeData ticketFlowNodeData = ticketFlowNodeDataOpt.get();
        String ticketDataId = ticketFlowNodeData.getTicketDataId();
        //查询节点名称
        String nodeName = ticketFlowNodeData.getNodeName();
        CallBackMsgStatusEnum nodeCallBackMsgStatus = ticketFlowNodeData.getCallBackMsgStatus();
        if (Objects.isNull(nodeCallBackMsgStatus)
                && CallBackMsgStatusEnum.SUCCESS_MSG_SENG == callBackMsgStatus) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("节点(%s)未发送失败消息之前不能发送成功消息", ticketFlowNodeDataId));
        }
        if (Objects.nonNull(nodeCallBackMsgStatus)
                && nodeCallBackMsgStatus == callBackMsgStatus) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("同个节点(%s)同状态消息(%s)已发送", ticketFlowNodeDataId, callBackMsgStatus));
        }
        Optional<TicketData> tdOpt = ticketDataService.lambdaQuery()
                .eq(TicketData::getId, ticketDataId)
                .isNull(TicketData::getDeleteTime)
                .oneOpt();
        if (!tdOpt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单(%s)不存在", ticketDataId));
        }
        TicketData ticketData = tdOpt.get();
        String ticketAppId = ticketData.getAppId();
        Optional<TicketApp> opt = this.lambdaQuery()
                .eq(TicketApp::getId, ticketAppId)
                .isNull(TicketApp::getDeleteTime)
                .oneOpt();
        if (!opt.isPresent()) {
            log.info(String.format("相关业务(%s)不存在", opt.get().getAppName()));
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("相关业务(%s)不存在", opt.get().getAppName()));
        }
        //获取申请人
        AccountInfo applyAccountInfo = AccountInfo.ToAccountInfo(ticketData.getApplyUser());
        TicketApp ticketApp = opt.get();
        String appName = ticketApp.getAppName();
        //构造消息内容
        String msg;
        if (ObjectHelper.isNotEmpty(callBackMsgStatus) && CallBackMsgStatusEnum.SUCCESS_MSG_SENG == callBackMsgStatus) {
            msg = QWCardMD.qwGroupCardSuccessStrByTicketApp.
                    replace("{{ticket_id}}", ticketData.getId()).
                    replace("{{ticket_name}}", ticketData.getTicketName()).
                    replace("{{node_name}}", nodeName).
                    replace("{{ticket_detail_url}}", tfSJumpUrlProperties.getTicketDetailUrl() + ticketData.getId());
        } else {
            msg = QWCardMD.qwGroupCardFailedStrByTicketApp.
                    replace("{{node_name}}", nodeName).
                    replace("{{ticket_id}}", ticketData.getId()).
                    replace("{{ticket_name}}", ticketData.getTicketName()).
                    replace("{{apply_user}}", applyAccountInfo.getAccountName()).
                    replace("{{apply_time}}", DateFormat.getInstance().format(ticketData.getCreateTime())).
                    replace("{{ticket_detail_url}}", tfSJumpUrlProperties.getTicketDetailUrl() + ticketData.getId());
        }

        //如果业务已绑定群号，则发送消息
        if (StringUtils.isNotEmpty(ticketApp.getWxChatGroupId())) {
            log.info(String.format("相关业务(%s)群(%s)已建,直接推送消息。", ticketApp.getAppName(), ticketApp.getWxChatGroupId()));
            String chatId = ticketApp.getWxChatGroupId();
            QwNotify.sendMsgToChatGroup(msg, chatId);
            boolean updateTfnd = ticketFlowNodeDataService.lambdaUpdate()
                    .eq(TicketFlowNodeData::getId, ticketFlowNodeDataId)
                    .isNull(TicketFlowNodeData::getDeleteTime)
                    .set(TicketFlowNodeData::getCallBackMsgStatus, callBackMsgStatus)
                    .update();
            if (!updateTfnd) {
                return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, String.format("节点: %s, 发送消息成功，但更新消息状态失败", ticketFlowNodeDataId));
            }
            return Response.success();
        }

        //去重取企业微信号
        Set<String> userQWSet = new HashSet<>();
        //应用管理员
        List<AccountInfo> appAdminAccountList = AccountInfo.ToAccountInfoList(ticketApp.getAppAdminUsers());
        String superAdmin = tfsSmyConstant.getSuperAdmin();
        if (ObjectHelper.isNotEmpty(superAdmin)) {
            List<AccountInfo> superAdminList = AccountInfo.ToAccountInfoList(superAdmin);
            appAdminAccountList.addAll(superAdminList);
        }
        if (CollectionUtils.isNotEmpty(appAdminAccountList)) {
            for (AccountInfo accountInfo : appAdminAccountList) {
                TicketRemoteAccountDto accountDto = accountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                if (accountDto != null && StringUtils.isNotBlank(accountDto.getQywxId())) {
                    accountInfo.setAccountName(accountDto.getUserName());
                    accountInfo.setSameOriginId(accountDto.getSameOriginId());
                    userQWSet.add(accountDto.getQywxId());
                }
            }
        }

        if (userQWSet.size() <= 2) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("工单ID: %s, 企业微信相关人员不足3人", ticketData.getId()));
        }

        //建群并发送消息
        List<String> userList = Arrays.asList(userQWSet.toArray(new String[0]));
        NotificationDto.CreateChatGroup createChatGroup = new NotificationDto.CreateChatGroup(
                userList,
                String.format("[%s]工单处理群", appName),
                userList.get(0),
                "",
                msg);
        String chatId = notificationService.createChatGroup(createChatGroup);
        boolean updateBool = this.lambdaUpdate().eq(TicketApp::getId, ticketApp.getId()).isNull(TicketApp::getWxChatGroupId).isNull(TicketApp::getDeleteTime).set(TicketApp::getWxChatGroupId, chatId).update();
        if (!updateBool) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, String.format("工单ID: %s, 建群成功但数据保存失败", ticketData.getId()));
        }
        boolean updateTfnd = ticketFlowNodeDataService.lambdaUpdate()
                .eq(TicketFlowNodeData::getId, ticketFlowNodeDataId)
                .isNull(TicketFlowNodeData::getDeleteTime)
                .set(TicketFlowNodeData::getCallBackMsgStatus, callBackMsgStatus)
                .update();
        if (!updateTfnd) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, String.format("节点: %s, 发送消息成功，但更新消息状态失败", ticketFlowNodeDataId));
        }
        return Response.success();
    }

    @Override
    public Response<ExtendFieldsMappingDto> getExtendFieldsMapping(String ticketAppId) {
        Optional<TicketApp> opt = this.lambdaQuery()
                .eq(TicketApp::getId, ticketAppId)
                .oneOpt();
        if (!opt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "根据业务id查询的业务对象不存在");
        }
        TicketApp ticketApp = opt.get();
        ExtendFieldsMappingDto extendFieldsMappingDto = new ExtendFieldsMappingDto();
        try {
            List<BusiTicketDataFieldsMappingDto> busiTicketDataFieldsMappingDtoList = BusiTicketDataUtil.getBusiTicketDataFieldsMapping(ticketApp);
            extendFieldsMappingDto.setExtendFields(busiTicketDataFieldsMappingDtoList);
        } catch (Exception e) {
            return Response.error(BizResponseEnums.CONVERT_ERROR, "业务通用字段转换异常");
        }
        extendFieldsMappingDto.setExtendEnabled(ticketApp.getExtendEnabled());
        return Response.success(extendFieldsMappingDto);
    }


}
