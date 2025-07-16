package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.smy.tfs.api.dbo.*;
import com.smy.tfs.api.dto.TicketAccountDto;
import com.smy.tfs.api.dto.TicketAccountDubboConfigDto;
import com.smy.tfs.api.dto.TicketAppDto;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.DeptLevelEnum;
import com.smy.tfs.api.enums.RecordStatusEnum;
import com.smy.tfs.api.service.*;
import com.smy.tfs.biz.client.DingDingClient;
import com.smy.tfs.biz.mapper.TicketAccountMapper;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.constant.CacheConstants;
import com.smy.tfs.common.core.redis.RedisCache;
import com.smy.tfs.common.exception.ServiceException;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.BeanHelper;
import com.smy.tfs.common.utils.bean.BeanUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import com.smy.tfs.common.utils.notification.QwCorporateEnum;
import com.smy.tfs.common.utils.notification.QwNotify;
import com.smy.tfs.framework.config.DynamicDubboConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 工单账户体系表 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-04-19
 */
@Slf4j
@Service
public class TicketAccountServiceImpl extends ServiceImpl<TicketAccountMapper, TicketAccount> implements ITicketAccountService {

    @Resource
    private DynamicDubboConsumer dynamicDubboConsumer;
    @Resource
    private ITicketAccountSyncRecordService ticketAccountSyncRecordService;
    @Resource
    private ITicketAppService ticketAppService;
    @Resource
    private ITicketExecutorGroupService ticketExecutorGroupService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ITicketOriginAccountService ticketOriginAccountService;
    @Resource
    private NotificationService notificationService;


    @Override
    public TicketAccountDto selectTicketAccountById(String id) {
        TicketAccount ticketAccount = this.baseMapper.selectById(id);
        return BeanHelper.copyObject(ticketAccount, TicketAccountDto.class);
    }

    @Override
    public Response<List<TicketAccountDto>> selectTicketAccountList(TicketAccountDto ticketAccountDto) {
        List<TicketAccountDto> ticketAccountDtoList = this.baseMapper.selectTicketAccountList(ticketAccountDto);
        for (TicketAccountDto accountDto : ticketAccountDtoList) {
            parseAccountConfigStr(accountDto);
            accountDto.setStatus(accountDto.getDeleteTime() != null
                    ? RecordStatusEnum.DELETED.getCode() : RecordStatusEnum.NORMAL.getCode());
        }
        return Response.success(ticketAccountDtoList);
    }

    public Boolean existTicketAccountByUniqueKey(String id, String accountType) {
        if (StrUtil.hasBlank(accountType)) {
            throw new ServiceException("账号体系配置唯一性校验失败，账号类型不能为空");
        }
        LambdaQueryWrapper<TicketAccount> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TicketAccount::getTicketAccountType, accountType);
        queryWrapper.ne(StrUtil.isNotBlank(id), TicketAccount::getId, id);

        return this.baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Response<Boolean> insertTicketAccount(TicketAccountDto ticketAccountDto) {
        if (existTicketAccountByUniqueKey(null, ticketAccountDto.getTicketAccountType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "应用账户类型已存在，请重新输入");
        }

        parseAccountConfigObject(ticketAccountDto);
        if (!checkTicketAccountConfigValid(ticketAccountDto)) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "账户体系配置无效,请检查接口配置是否正常");
        }

        TicketAccount ticketAccount = BeanHelper.copyObject(ticketAccountDto, TicketAccount.class);

        boolean saveResult = this.save(ticketAccount);
        if (saveResult) {
            doSyncTicketAccountConfig(ticketAccount.getId());
            saveOrUpdateAccountTypeNameCache(ticketAccount);
        }

        return Response.success(saveResult);
    }

    /**
     * 保存或更新账户类型名称缓存
     *
     * @param ticketAccount
     */
    private void saveOrUpdateAccountTypeNameCache(TicketAccount ticketAccount) {
        String cacheRedisKey = CacheConstants.TFS_TICKET_ACCOUNT_TYPE_NAME + ticketAccount.getTicketAccountType();
        redisCache.setCacheObject(cacheRedisKey, ticketAccount.getTicketAccountName());
    }

    @Override
    public Response<Boolean> updateTicketAccount(TicketAccountDto ticketAccountDto) {
        if (existTicketAccountByUniqueKey(ticketAccountDto.getId(), ticketAccountDto.getTicketAccountType())) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "应用账户类型已存在，请重新输入");
        }

        parseAccountConfigObject(ticketAccountDto);
        if (!checkTicketAccountConfigValid(ticketAccountDto)) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "账户体系配置无效,请检查接口配置是否正常");

        }

        TicketAccount ticketAccount = BeanHelper.copyObject(ticketAccountDto, TicketAccount.class);
        boolean updateResult = this.updateById(ticketAccount);
        if (updateResult) {
            saveOrUpdateAccountTypeNameCache(ticketAccount);
        }
        return Response.success(updateResult);
    }

    /**
     * 解析用户组用户信息json串到对象，方便前端展示
     *
     * @param ticketAccountDto
     */
    private void parseAccountConfigStr(TicketAccountDto ticketAccountDto) {
        String accountValue = ticketAccountDto.getTicketAccountValue();
        if (StrUtil.isBlank(accountValue)) {
            return;
        }
        try {
            TicketAccountDubboConfigDto ticketAccountDubboConfig = JSONUtil.toBean(accountValue, TicketAccountDubboConfigDto.class);
            BeanUtils.copyProperties(ticketAccountDubboConfig, ticketAccountDto);
        } catch (Exception e) {
            log.error("解析账户体系配置信息失败，解析内容：{} 原因：", accountValue, e);
        }
    }

    private void parseAccountConfigObject(TicketAccountDto ticketAccountDto) {
        TicketAccountDubboConfigDto ticketAccountDubboConfig = new TicketAccountDubboConfigDto();
        BeanUtils.copyProperties(ticketAccountDto, ticketAccountDubboConfig);

        ticketAccountDto.setTicketAccountValue(JSONUtil.toJsonStr(ticketAccountDubboConfig));
    }

    @Override
    public Response<String> deleteTicketAccountById(String id) {
        TicketAccount ticketAccount = new TicketAccount();
        ticketAccount.setId(id);
        ticketAccount.setDeleteTime(new Date());
        this.baseMapper.updateById(ticketAccount);
        return Response.success();
    }

    @Override
    public Response<String> doSyncTicketAccountConfig(String id) {
        if (StrUtil.isBlank(id)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "同步账号体系配置失败，id不能为空");
        }
        TicketAccount ticketAccount = null;
        Optional<TicketAccount> optional = this.lambdaQuery().eq(TicketAccount::getId, id).isNull(TicketAccount::getDeleteTime).oneOpt();
        if (optional.isPresent()) {
            ticketAccount = optional.get();
        }
        if (ticketAccount == null) {
            optional = this.lambdaQuery().eq(TicketAccount::getTicketAccountType, id).isNull(TicketAccount::getDeleteTime).oneOpt();
            if (optional.isPresent()) {
                ticketAccount = optional.get();
            }
        }
        if (ticketAccount == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应ID/TYPE:%s 账户配置不存在", id));
        }
        TicketAccount finalTicketAccount = ticketAccount;
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, "用户态信息为空");
        }
        threadPoolTaskExecutor.execute(() -> {
            SecurityUtils.wrapContext(authentication, () -> {
                doSyncTicketAccountConfig(finalTicketAccount);
            });
        });
        return Response.success();
    }

    @Override
    public Response<String> doSyncTicketAccountConfigNoAuth(String id) {
        if (StrUtil.isBlank(id)) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, "同步账号体系配置失败，id不能为空");
        }
        TicketAccount ticketAccount = null;
        Optional<TicketAccount> optional = this.lambdaQuery().eq(TicketAccount::getId, id).isNull(TicketAccount::getDeleteTime).oneOpt();
        if (optional.isPresent()) {
            ticketAccount = optional.get();
        }
        if (ticketAccount == null) {
            optional = this.lambdaQuery().eq(TicketAccount::getTicketAccountType, id).isNull(TicketAccount::getDeleteTime).oneOpt();
            if (optional.isPresent()) {
                ticketAccount = optional.get();
            }
        }
        if (ticketAccount == null) {
            return new Response<>(null, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应ID/TYPE:%s 账户配置不存在", id));
        }
        doSyncTicketAccountConfig(ticketAccount);
        return Response.success();
    }

    /**
     * @param appID
     * @param groupName
     * @param groupDesc
     * @param accountIdList
     * @return
     */
    @Override
    public Response<String> syncTicketAccountGroup(String appID, String groupName, String groupDesc, List<String> accountIdList) {
        Optional<TicketApp> ticketAppOpt = ticketAppService.lambdaQuery().eq(TicketApp::getId, appID).isNull(TicketApp::getDeleteTime).oneOpt();
        if (!ticketAppOpt.isPresent()) {
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("对应 appID:%s 数据不存在", appID));
        }
        TicketApp ticketApp = ticketAppOpt.get();
        Optional<TicketExecutorGroup> ticketExecutorGroupOpt = ticketExecutorGroupService.lambdaQuery()
                .eq(TicketExecutorGroup::getExecutorGroupName, groupName)
                .eq(TicketExecutorGroup::getAppId, appID)
                .isNull(TicketExecutorGroup::getDeleteTime).oneOpt();

        List<TicketAccountMapping> ticketAccountMappingList = null;
        if (ObjectHelper.isNotEmpty(accountIdList)) {
            ticketAccountMappingList = ticketAccountMappingService.lambdaQuery()
                    .eq(TicketAccountMapping::getAccountType, ticketApp.getAccountType())
                    .isNull(TicketAccountMapping::getDeleteTime)
                    .in(TicketAccountMapping::getAccountId, accountIdList).list();
        }
        if (ObjectHelper.isEmpty(ticketAccountMappingList)) {
            log.info("用户组用户信息为空:{}", accountIdList);
        }
        List<AccountInfo> accountInfoList = new ArrayList<>();
        if (ObjectHelper.isNotEmpty(ticketAccountMappingList)) {
            accountInfoList = ticketAccountMappingList.stream()
                    .map(ticketAccountMapping ->
                            new AccountInfo(ticketAccountMapping.getSameOriginId(),
                                    ticketAccountMapping.getAccountType(),
                                    ticketAccountMapping.getAccountId(),
                                    ticketAccountMapping.getAccountName()
                            )).collect(Collectors.toList());
        }
        String accountListStr = StringUtils.EMPTY;
        if (ObjectHelper.isNotEmpty(accountInfoList)) {
            accountListStr = AccountInfo.ToAccountInfoListStr(accountInfoList);
        }
        if (ticketExecutorGroupOpt.isPresent()) {
            TicketExecutorGroup ticketExecutorGroup = ticketExecutorGroupOpt.get();
            ticketExecutorGroupService.lambdaUpdate()
                    .eq(TicketExecutorGroup::getId, ticketExecutorGroup.getId())
                    .isNull(TicketExecutorGroup::getDeleteTime)
                    .set(TicketExecutorGroup::getAccountInfo, accountListStr)
                    .set(TicketExecutorGroup::getUpdateTime, new Date())
                    .set(TicketExecutorGroup::getUpdateBy, new AccountInfo("10000", "ldap", "admin", "admin").ToJsonString())
                    .update();
        } else {
            TicketExecutorGroup ticketExecutorGroup = new TicketExecutorGroup();
            ticketExecutorGroup.setExecutorGroupName(groupName);
            ticketExecutorGroup.setAccountInfo(accountListStr);
            ticketExecutorGroup.setAppId(appID);
            ticketExecutorGroup.setExecutorGroupDesc(groupDesc);
            ticketExecutorGroup.setCreateBy(new AccountInfo("10000", "ldap", "admin", "admin").ToJsonString());
            ticketExecutorGroup.setCreateTime(new Date());
            ticketExecutorGroup.setUpdateBy(new AccountInfo("10000", "ldap", "admin", "admin").ToJsonString());
            ticketExecutorGroup.setUpdateTime(new Date());
            ticketExecutorGroupService.save(ticketExecutorGroup);
        }
        return Response.success();
    }

    private void doSyncTicketAccountConfig(TicketAccount ticketAccount) {
        String syncResult = RecordStatusEnum.ERROR.getCode();
        String syncResultDesc = RecordStatusEnum.ERROR.getDesc();
        try {
            String ticketAccountType = ticketAccount.getTicketAccountType();
            String ticketAccountValue = ticketAccount.getTicketAccountValue();
            //1.远程数据
            List<TicketRemoteAccountDto> remoteTicketAccountDtoList = doInvokeDubboService(ticketAccountValue);
            log.info("获取源账号体系数据 参数:{}, 获取数据:{}", ticketAccountValue, remoteTicketAccountDtoList);
            if (CollUtil.isEmpty(remoteTicketAccountDtoList)) {
                return;
            }
            //默认方式一个系统用户
            remoteTicketAccountDtoList.add(buildSystemUserAccount(ticketAccount));

            //2.本地数据
            List<TicketAccountMapping> localTicketAccountMappingList = ticketAccountMappingService.selectTicketAccountMappingList(ticketAccountType);
            Map<String, TicketAccountMapping> localAccountIdMap = new HashMap<>();
            if (ObjectHelper.isNotEmpty(localTicketAccountMappingList)) {
                for (TicketAccountMapping localTicketAccountMapping : localTicketAccountMappingList) {
                    localAccountIdMap.putIfAbsent(localTicketAccountMapping.getAccountId(), localTicketAccountMapping);
                }
            }

            /**
             * 删除数据集：找出在源数据集ticketAccountMappingList中，不在目标数据集ticketRemoteAccountDtoList中的数据
             */
            Set<String> deleteAccountIdSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(localTicketAccountMappingList) && CollectionUtils.isNotEmpty(remoteTicketAccountDtoList)) {
                Set<String> remoteUserISet = new HashSet<>();
                remoteTicketAccountDtoList.forEach(ticketRemoteAccountDto -> {
                            if (!remoteUserISet.contains(ticketRemoteAccountDto.getUserId())) {
                                remoteUserISet.add(ticketRemoteAccountDto.getUserId());
                            }
                        }
                );
                log.info("新增数据集合:{}", remoteUserISet);
                localTicketAccountMappingList.forEach(ticketAccountMapping -> {
                    if (!ticketAccountMapping.getSystemAccount() && !remoteUserISet.contains(ticketAccountMapping.getAccountId())) {
                        if (!deleteAccountIdSet.contains(ticketAccountMapping.getAccountId())) {
                            deleteAccountIdSet.add(ticketAccountMapping.getAccountId());
                        }
                    }
                });
            }
            /**
             * 组装新增和更新的数据集
             */
            List<TicketAccountMapping> addTicketAccountMappingList = new ArrayList<>();
            List<TicketAccountMapping> updateTicketAccountMappingList = new ArrayList<>();
            for (TicketRemoteAccountDto ticketRemoteAccountDto : remoteTicketAccountDtoList) {
                ticketRemoteAccountDto.setUserType(ticketAccountType);
                if (StrUtil.hasBlank(ticketRemoteAccountDto.getUserId(), ticketRemoteAccountDto.getUserName())) {
                    log.error("账号id和账号名不能为空，具体内容：{}", ticketRemoteAccountDto);
                    continue;
                }

                if (ObjectHelper.isNotEmpty(localAccountIdMap.get(ticketRemoteAccountDto.getUserId()))) {
                    //修改
                    TicketAccountMapping localTicketAccountMapping = localAccountIdMap.get(ticketRemoteAccountDto.getUserId());
                    if (localTicketAccountMapping.getSystemAccount()) {
                        log.info("当前账号accountId:{},accountType:{}系统账号，不进行更新", localTicketAccountMapping.getAccountId(), localTicketAccountMapping.getAccountType());
                        continue;
                    }
                    //远程更新本地数据
                    //localTicketAccountMapping = TicketRemoteAccountDto.updateLocalAccountMapping(localTicketAccountMapping, ticketRemoteAccountDto);
                    //更新判断+数据更新
                    Boolean isSame = new TicketRemoteAccountDto().equalTicketAccountMapping(ticketRemoteAccountDto, localTicketAccountMapping);
                    if (StrUtil.isBlank(localTicketAccountMapping.getSameOriginId())) {
                        TicketOriginAccount ticketOriginAccount = ticketOriginAccountService.selectOriginAccountByMappingInfo(localTicketAccountMapping);
                        if (ticketOriginAccount == null) {
                            ticketOriginAccount = ticketOriginAccountService.insertOriginAccount(localTicketAccountMapping);
                        }
                        ticketOriginAccountService.parseOriginAccountToAccountMapping(ticketOriginAccount, localTicketAccountMapping);
                        localTicketAccountMapping.setUpdateBy(SecurityUtils.getAccountUserInfo());
                        localTicketAccountMapping.setUpdateTime(new Date());
                        updateTicketAccountMappingList.add(localTicketAccountMapping);
                        continue;
                    }
                    if (!isSame) {
                        TicketOriginAccount ticketOriginAccount = ticketOriginAccountService.lambdaQuery().isNull(TicketOriginAccount::getDeleteTime).eq(TicketOriginAccount::getSameOriginId, localTicketAccountMapping.getSameOriginId()).oneOpt().orElse(null);
                        if (ticketOriginAccount == null) {
                            log.error("当前账号originId:{}不存在，请检查", localTicketAccountMapping.getSameOriginId());
                            continue;
                        }
                        ticketOriginAccountService.parseOriginAccountToAccountMapping(ticketOriginAccount, localTicketAccountMapping);
                        localTicketAccountMapping.setUpdateBy(SecurityUtils.getAccountUserInfo());
                        localTicketAccountMapping.setUpdateTime(new Date());
                        updateTicketAccountMappingList.add(localTicketAccountMapping);
                        continue;
                    }
                } else {
                    //新增
                    TicketAccountMapping newTicketAccountMapping = TicketRemoteAccountDto.toTicketAccountMapping(ticketRemoteAccountDto);
                    TicketOriginAccount ticketOriginAccount = ticketOriginAccountService.selectOriginAccountByMappingInfo(newTicketAccountMapping);
                    if (ticketOriginAccount == null) {
                        ticketOriginAccount = ticketOriginAccountService.insertOriginAccount(newTicketAccountMapping);
                    }
                    ticketOriginAccountService.parseOriginAccountToAccountMapping(ticketOriginAccount, newTicketAccountMapping);
                    newTicketAccountMapping.setUpdateBy(SecurityUtils.getAccountUserInfo());
                    newTicketAccountMapping.setUpdateTime(new Date());
                    addTicketAccountMappingList.add(newTicketAccountMapping);
                }
            }

            if (ObjectHelper.isNotEmpty(addTicketAccountMappingList)) {
                ticketAccountMappingService.saveBatch(addTicketAccountMappingList);
            }
            if (ObjectHelper.isNotEmpty(updateTicketAccountMappingList)) {
                ticketAccountMappingService.saveOrUpdateBatch(updateTicketAccountMappingList);
            }
            if (deleteAccountIdSet.size() > 0) {
                for (String accountId : deleteAccountIdSet) {
                    if (StringUtils.isNotEmpty(accountId)) {
                        Date delDate = new Date();
                        try {
                            ticketAccountMappingService.lambdaUpdate()
                                    .eq(TicketAccountMapping::getAccountId, accountId)
                                    .eq(TicketAccountMapping::getAccountType, ticketAccountType)
                                    .isNull(TicketAccountMapping::getDeleteTime)
                                    .set(TicketAccountMapping::getDeleteTime, delDate)
                                    .set(TicketAccountMapping::getUpdateBy, SecurityUtils.getAccountUserInfo())
                                    .set(TicketAccountMapping::getUpdateTime, delDate)
                                    .update();
                        } catch (Exception ex) {
                            log.error("删除异常账户异常：accountId:{} ticketAccountType:{} ex:{}", accountId, ticketAccountType, ex.getMessage());
                        }
                    }
                }
            }
            syncResult = RecordStatusEnum.SUCCESS.getCode();
            syncResultDesc = String.format("同步账户体系配置成功 type:%s，获取账户记录%s条, 新增记录%s条, 修改记录%s条, 删除记录%s条", ticketAccount.getTicketAccountType(), remoteTicketAccountDtoList.size(), addTicketAccountMappingList.size(), updateTicketAccountMappingList.size(), deleteAccountIdSet.size());
        } catch (Exception e) {
            log.error("同步账户体系配置失败，原因: ", e);
            syncResult = RecordStatusEnum.ERROR.getCode();
            syncResultDesc = String.format("同步账户体系配置失败 type:%s，原因：%s", ticketAccount.getTicketAccountType(), e.getMessage());
        } finally {
            //插入同步日志
            ticketAccountSyncRecordService.createTicketAccountSyncRecord(ticketAccount.getId(), syncResult, syncResultDesc);
        }
    }


    /**
     * 构建一个系统账号用户
     *
     * @param ticketAccount
     * @return
     */
    private TicketRemoteAccountDto buildSystemUserAccount(TicketAccount ticketAccount) {
        TicketRemoteAccountDto ticketRemoteAccountDto = new TicketRemoteAccountDto();
        ticketRemoteAccountDto.setUserId("tfs_system");
        ticketRemoteAccountDto.setUserName("tfs_system");
        ticketRemoteAccountDto.setUserType(ticketAccount.getTicketAccountType());
        ticketRemoteAccountDto.setSystemUser(true);
        return ticketRemoteAccountDto;
    }

    @Override
    public List<TicketRemoteAccountDto> getTicketRemoteAccountListByType(String ticketAccountType) {
        List<TicketAccountMapping> ticketAccountMappingList = ticketAccountMappingService.selectEnableTicketAccountMappingList(ticketAccountType);
        return TicketRemoteAccountDto.parseAccountMappingListToRemoteAccountList(ticketAccountMappingList);
    }

    @Override
    public List<TicketRemoteAccountDto> getTicketRemoteAccountListByAppId(String appId) {
        TicketAppDto ticketAppDto = ticketAppService.selectTicketAppFullById(appId);
        if (ticketAppDto == null) {
            throw new ServiceException(String.format("未找到appId: %s对应的应用配置信息", appId));
        }
        return getTicketRemoteAccountListByType(ticketAppDto.getAccountType());
    }

    @Override
    public TicketRemoteAccountDto getTicketRemoteAccountByIdAndType(String userId, String ticketAccountType) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, ticketAccountType);
        return TicketRemoteAccountDto.parseAccountMappingToRemoteAccount(ticketAccountMapping);
    }

    public TicketRemoteAccountDto getTicketRemoteAccountByQywxIdAndType(String qywxId, String ticketAccountType) {
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByQywxIdAndType(qywxId, ticketAccountType);
        return TicketRemoteAccountDto.parseAccountMappingToRemoteAccount(ticketAccountMapping);
    }

    @Override
    public TicketRemoteAccountDto getTicketRemoteAccountByIdAndApp(String userId, String appId) {
        TicketAppDto ticketAppDto = ticketAppService.selectTicketAppFullById(appId);
        if (ticketAppDto == null) {
            throw new ServiceException(String.format("未找到appId: %s对应的应用配置信息", appId));
        }
        return getTicketRemoteAccountByIdAndType(userId, ticketAppDto.getAccountType());
    }

    @Override
    public TicketRemoteAccountDto getTicketRemoteAccountByQywxIdAndApp(String qywxId, String appId) {
        TicketAppDto ticketAppDto = ticketAppService.selectTicketAppFullById(appId);
        if (ticketAppDto == null) {
            throw new ServiceException(String.format("未找到appId: %s对应的应用配置信息", appId));
        }
        return getTicketRemoteAccountByQywxIdAndType(qywxId, ticketAppDto.getAccountType());
    }

    /**
     * @param userType
     * @param userId
     * @return
     */
    @Override
    public TicketRemoteAccountDto getLeaderByTypeAndId(String userType, String userId) {
        log.info("开始查找用户【{}】的上级用户", userId);
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping != null && StringUtils.isNotEmpty(ticketAccountMapping.getSuperiorId())) {
            TicketRemoteAccountDto superiorAccount = getTicketRemoteAccountByIdAndType(ticketAccountMapping.getSuperiorId(), userType);
            if (superiorAccount == null) {
                log.error("未找到userId: {}, userType: {} 相关信息", userId, userType);
                return null;
            }
            log.info("通过上级ID【{}】查找到上级用户【{}】", ticketAccountMapping.getSuperiorId(), superiorAccount.getUserName());
            return superiorAccount;
        }

        // 通过钉钉兜底
        if (ticketAccountMapping == null || StrUtil.isBlank(ticketAccountMapping.getDdUserId())) {
            log.error("未找到userId: {}, userType: {} 对应的用户钉钉信息", userId, userType);
            return null;
        }

        try {
            OapiV2UserGetResponse.UserGetResponse supervisorUserInfo = DingDingClient.getSupervisorUserInfo(ticketAccountMapping.getDdUserId());

            TicketAccountMapping supervisorAccount = ticketAccountMappingService.selectAccountByTypeAndDdUserId(userType, supervisorUserInfo.getUserid());
            if (supervisorAccount == null && !"ldap".equals(userType)) {
                supervisorAccount = ticketAccountMappingService.selectAccountByTypeAndDdUserId("ldap", supervisorUserInfo.getUserid());
                if (supervisorAccount == null) {
                    return null;
                }
            }

            log.info("通过调用钉钉API查找到用户【{}】的上级用户【{}】", userId, supervisorAccount.getAccountName());
            return TicketRemoteAccountDto.parseAccountMappingToRemoteAccount(supervisorAccount);
            /*if (supervisorUserInfo != null) {
                TicketRemoteAccountDto ticketRemoteAccountDto = new TicketRemoteAccountDto();

                String jobNumber = supervisorUserInfo.getJobNumber();
                String email = supervisorUserInfo.getEmail();
                String fullJobNumber = email.charAt(0) + jobNumber;

                ticketRemoteAccountDto.setUserId(fullJobNumber);
                ticketRemoteAccountDto.setUserName(supervisorUserInfo.getName());
                ticketRemoteAccountDto.setUserType(TfsBaseConstant.defaultUserType);
                ticketRemoteAccountDto.setFullUserName(ticketRemoteAccountDto.getUserId() + "-" + ticketRemoteAccountDto.getUserName());

                ticketRemoteAccountDto.setUserPhone(supervisorUserInfo.getMobile());
                ticketRemoteAccountDto.setUserEmail(email);

                ticketRemoteAccountDto.setDingDingId(supervisorUserInfo.getUserid());

                //找到上级之后，需要跟新下mapping库
                ticketAccountMappingService.updateTicketAccountMappingByRemoteAccount(ticketRemoteAccountDto);
                return ticketRemoteAccountDto;
            }*/
        } catch (Exception e) {
            log.error("获取用户userId: {}, userType: {} 钉钉上级信息失败", userId, userType, e);
        }

        return null;
    }

    /**
     * 获取用户部门负责人列表
     */
    @Override
    public List<TicketRemoteAccountDto> getDeptManagersByTypeAndId(String userType, String userId) {
        log.info("开始查找用户【{}】的部门负责人", userId);
        TicketAccountMapping ticketAccountMapping = ticketAccountMappingService.selectAccountMappingByAccountIdAndType(userId, userType);
        if (ticketAccountMapping != null && StringUtils.isNotEmpty(ticketAccountMapping.getDeptManagerIds())) {
            List<TicketRemoteAccountDto> deptManagerAccountList = new ArrayList<>();
            for (String deptManagerId : ticketAccountMapping.getDeptManagerIds().split(",")) {
                TicketRemoteAccountDto deptManagerAccount = getTicketRemoteAccountByIdAndType(deptManagerId, userType);
                if (deptManagerAccount != null && StringUtils.isNotEmpty(deptManagerAccount.getUserId())) {
                    deptManagerAccountList.add(deptManagerAccount);
                }
            }

            if (deptManagerAccountList.isEmpty()) {
                log.error("未找到userId: {}, userType: {} 相关信息", userId, userType);
                return null;
            }
            log.info("查找到用户【{}】的部门的负责人【{}】", userId, deptManagerAccountList.stream()
                    .map(TicketRemoteAccountDto::getUserName)
                    .collect(Collectors.joining(", ")));
            return deptManagerAccountList;
        }
        return null;
    }

    @Override
    public void syncTicketRemoteAccount() {
        List<TicketAccount> ticketAccountList = this.lambdaQuery().isNull(TicketAccount::getDeleteTime).list();
        if (CollectionUtils.isNotEmpty(ticketAccountList)) {
            for (TicketAccount ticketAccount : ticketAccountList) {
                doSyncTicketAccountConfig(ticketAccount);
            }
        }
    }

    @Override
    public void notifyQwMsg(String message, List<String> accountList) {
        if (CollectionUtils.isEmpty(accountList)) {
            return;
        }
        notificationService.notifyQw(message, accountList);
    }

    @Override
    public List<AccountInfo> getDeptLevelAccountInfoList(DeptLevelEnum deptLevel, String accountId, String accountType) {
        log.info("开始查找申請人({})對應層級({})的部门人員", accountId, deptLevel);
        List<AccountInfo> deptLevelAccountInfoList = new ArrayList<>();
        if (null == deptLevel)
            return deptLevelAccountInfoList;
        //查询当前部门的部门id、上级部门的id。
        Optional<TicketAccountMapping> tapOpt = ticketAccountMappingService.lambdaQuery()
                .eq(TicketAccountMapping::getAccountId, accountId)
                .eq(TicketAccountMapping::getAccountType, accountType)
                .oneOpt();
        String depId = StringUtils.EMPTY;
        String superDeptId = StringUtils.EMPTY;
        if (tapOpt.isPresent()) {
            TicketAccountMapping tap = tapOpt.get();
            depId = tap.getDeptId();
            superDeptId = tap.getSuperDeptId();
        }
        List<TicketAccountMapping> tapList;
        switch (deptLevel) {
            case DEPT:
                if (StringUtils.isEmpty(depId))
                    return deptLevelAccountInfoList;
                tapList = ticketAccountMappingService.lambdaQuery()
                        .eq(TicketAccountMapping::getDeptId, depId)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (CollectionUtils.isNotEmpty(tapList)) {
                    deptLevelAccountInfoList = tapList.stream().map(tap ->
                                    new AccountInfo(tap.getSameOriginId(), tap.getAccountType(), tap.getAccountId(), tap.getAccountName()))
                            .collect(Collectors.toList());
                }
                break;
            case ONE_LEVEL_DEPT:
                if (StringUtils.isEmpty(superDeptId))
                    return deptLevelAccountInfoList;
                tapList = ticketAccountMappingService.lambdaQuery()
                        .eq(TicketAccountMapping::getDeptId, superDeptId)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (CollectionUtils.isEmpty(tapList))
                    return deptLevelAccountInfoList;
                deptLevelAccountInfoList = tapList.stream().map(tap ->
                                new AccountInfo(tap.getSameOriginId(), tap.getAccountType(), tap.getAccountId(), tap.getAccountName()))
                        .collect(Collectors.toList());
                break;
            case TWO_LEVEL_DEPT:
                if (StringUtils.isEmpty(superDeptId))
                    return deptLevelAccountInfoList;
                //获取上两级部门id
                List<TicketAccountMapping> twotapList = ticketAccountMappingService.lambdaQuery()
                        .select(TicketAccountMapping::getSuperDeptId)
                        .eq(TicketAccountMapping::getDeptId, superDeptId)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (ObjectHelper.isEmpty(twotapList))
                    return deptLevelAccountInfoList;
                TicketAccountMapping ticketAccountMapping = twotapList.get(0);
                String twoLevel = ticketAccountMapping.getSuperDeptId();

                //根据上两级部门id查询部门人员
                if (StringUtils.isEmpty(twoLevel))
                    return deptLevelAccountInfoList;
                tapList = ticketAccountMappingService.lambdaQuery()
                        .eq(TicketAccountMapping::getDeptId, twoLevel)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (CollectionUtils.isEmpty(tapList))
                    return deptLevelAccountInfoList;
                deptLevelAccountInfoList = tapList.stream().map(tap ->
                                new AccountInfo(tap.getSameOriginId(), tap.getAccountType(), tap.getAccountId(), tap.getAccountName()))
                        .collect(Collectors.toList());
                break;
            case THREE_LEVEL_DEPT:
                if (StringUtils.isEmpty(superDeptId))
                    return deptLevelAccountInfoList;
                //获取上两级部门id
                List<TicketAccountMapping> ttoList = ticketAccountMappingService.lambdaQuery()
                        .select(TicketAccountMapping::getSuperDeptId)
                        .eq(TicketAccountMapping::getDeptId, superDeptId)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (ObjectHelper.isEmpty(ttoList))
                    return deptLevelAccountInfoList;
                TicketAccountMapping twotap = ttoList.get(0);
                String twol = twotap.getSuperDeptId();

                //获取上三级部门id
                List<TicketAccountMapping> threetapList = ticketAccountMappingService.lambdaQuery()
                        .select(TicketAccountMapping::getSuperDeptId)
                        .eq(TicketAccountMapping::getDeptId, twol)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (ObjectHelper.isEmpty(threetapList))
                    return deptLevelAccountInfoList;
                TicketAccountMapping threetap = threetapList.get(0);
                String threeLevel = threetap.getSuperDeptId();

                //根据上三级部门id查询部门人员
                if (StringUtils.isEmpty(threeLevel))
                    return deptLevelAccountInfoList;
                tapList = ticketAccountMappingService.lambdaQuery()
                        .eq(TicketAccountMapping::getDeptId, threeLevel)
                        .eq(TicketAccountMapping::getAccountType, accountType)
                        .list();
                if (CollectionUtils.isEmpty(tapList))
                    return deptLevelAccountInfoList;
                deptLevelAccountInfoList = tapList.stream().map(tap ->
                                new AccountInfo(tap.getSameOriginId(), tap.getAccountType(), tap.getAccountId(), tap.getAccountName()))
                        .collect(Collectors.toList());
                break;
            default:
                throw new NotImplementedException(String.format("账户id:%s，未实现的枚举类型:%s", accountId, deptLevel));

        }
        return deptLevelAccountInfoList;

    }

    @Override
    public void syncAccountMappingQywxId(String accountType, String qwCorporate) {
        List<TicketAccountMapping> accountMappingList = ticketAccountMappingService.lambdaQuery()
                .eq(TicketAccountMapping::getAccountType, accountType)
                .eq(TicketAccountMapping::getSystemAccount, false)
                .isNull(TicketAccountMapping::getDeleteTime)
                .list();
        QwCorporateEnum qwCorporateEnum = QwCorporateEnum.getEnumByCode(qwCorporate);
        if (qwCorporateEnum == null) {
            return;
        }

        List<Integer> depIdList = QwNotify.getDepIds(qwCorporateEnum);
        if (CollUtil.isEmpty(depIdList)) {
            log.info("get depId from qw return empty");
            return;
        }
        Map<String, String> qwUserIdMap = depIdList.stream()
                .flatMap(depId -> Optional.ofNullable(QwNotify.getUserFromDepId(depId, qwCorporateEnum))
                        .orElse(Collections.emptyList()).stream())
                .collect(HashMap::new, (m, v) -> m.put(v.getUserId(), v.getUserName()), HashMap::putAll);

        for (TicketAccountMapping ticketAccountMapping : accountMappingList) {
            if (StrUtil.isNotBlank(ticketAccountMapping.getPhoneNo())) {
                String accessToken = QwNotify.getAccessToken(qwCorporateEnum);
                String userId = QwNotify.getUserIdFromPhone(ticketAccountMapping.getPhoneNo(), accessToken);
                if (StrUtil.isNotBlank(userId)) {
                    ticketAccountMapping.setQwUserId(userId + qwCorporateEnum.getCorporateSign());
                    if (qwUserIdMap.containsKey(userId)) {
                        ticketAccountMapping.setQyUserName(qwUserIdMap.get(userId));
                    }
                }
            }
        }
        ticketAccountMappingService.updateBatchById(accountMappingList, 100);
    }

    /**
     * 检查账户体系配置是否有效
     *
     * @param ticketAccountDto
     * @return
     */
    private Boolean checkTicketAccountConfigValid(TicketAccountDto ticketAccountDto) {
        String ticketAccountValue = ticketAccountDto.getTicketAccountValue();
        if (StrUtil.isBlank(ticketAccountValue)) {
            log.error("账户体系配置无效，入参为空");
            return false;
        }

        try {
            doInvokeDubboService(ticketAccountValue);
            return true;
        } catch (Exception e) {
            log.error("账户体系配置无效，检查失败原因：", e);
            return false;
        }
    }

    /**
     * 根据配置真正泛化调用远程接口
     *
     * @param dubboConfig
     * @return
     */
    private List<TicketRemoteAccountDto> doInvokeDubboService(String dubboConfig) {
        TicketAccountDubboConfigDto ticketAccountDubboConfig = JSONUtil.toBean(dubboConfig, TicketAccountDubboConfigDto.class);


        String interfaceName = ticketAccountDubboConfig.getInterfaceName();
        String methodName = ticketAccountDubboConfig.getMethodName();
        if (StrUtil.hasBlank(interfaceName, methodName)) {
            log.error("账户体系配置无效，缺少必要参数");
            throw new ServiceException("账户体系配置无效，缺少必要参数");
        }
        String version = ticketAccountDubboConfig.getVersion();
        String group = ticketAccountDubboConfig.getGroup();
        Object invokeResult = dynamicDubboConsumer.invokeDubboService(interfaceName, methodName, version, group);
        return JSONUtil.toBean(JSONUtil.parse(invokeResult), new TypeReference<List<TicketRemoteAccountDto>>() {
        }, true);

    }

}
