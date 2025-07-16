package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.dbo.TicketOriginAccount;
import com.smy.tfs.api.dto.TicketOriginAccountDto;
import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.AccountMatchResultEnum;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.service.ITicketAccountMappingService;
import com.smy.tfs.api.service.ITicketOriginAccountService;
import com.smy.tfs.biz.client.DingDingClient;
import com.smy.tfs.biz.client.DingDingConstant;
import com.smy.tfs.biz.mapper.TicketOriginAccountMapper;
import com.smy.tfs.biz.service.NotificationService;
import com.smy.tfs.common.utils.SecurityUtils;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.notification.QwCorporateEnum;
import com.smy.tfs.common.utils.notification.QwNotify;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzd
 * @since 2024-07-01
 */
@Slf4j
@Service
public class TicketOriginAccountServiceImpl extends ServiceImpl<TicketOriginAccountMapper, TicketOriginAccount> implements ITicketOriginAccountService {

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private NotificationService notificationService;

    @Resource
    private ITicketAccountMappingService ticketAccountMappingService;

    private static final int batchSize = 100;

    //插入一个源用户信息
    @Override
    public TicketOriginAccount insertOriginAccount (TicketAccountMapping ticketAccountMapping) {

        TicketOriginAccount ticketOriginAccount = new TicketOriginAccount();

//        BeanUtil.copyProperties(ticketAccountMapping, ticketOriginAccount, "id", "createBy", "updateBy"
//                , "createTime", "updateTime", "deleteTime", "matchResult");

        ticketOriginAccount.setPhoneNo(ticketAccountMapping.getPhoneNo());
        ticketOriginAccount.setEmail(ticketAccountMapping.getEmail());
        ticketOriginAccount.setAccountId(ticketAccountMapping.getAccountId());
        ticketOriginAccount.setQwUserId(ticketAccountMapping.getQwUserId());
        ticketOriginAccount.setDdUserId(ticketAccountMapping.getDdUserId());
        ticketOriginAccount.setAccountName(ticketAccountMapping.getAccountName());
        ticketOriginAccount.setQyUserName(ticketAccountMapping.getAccountName());
        ticketOriginAccount.setDdUserDeptId(ticketAccountMapping.getDdUserDeptId());
        ticketOriginAccount.setDdUserDeptName(ticketAccountMapping.getDdUserDeptName());
        ticketOriginAccount.setMatchResult(AccountMatchResultEnum.INIT);
        ticketOriginAccount.setSuperiorId(ticketAccountMapping.getSuperiorId());
        ticketOriginAccount.setDeptManagerIds(ticketAccountMapping.getDeptManagerIds());

        transactionTemplate.execute((status) -> {
            this.save(ticketOriginAccount);
            //更新用户源id = id + 100000
            ticketOriginAccount.setSameOriginId((100000 + ticketOriginAccount.getId()) + "");
            this.updateById(ticketOriginAccount);
            return null;
        });

        return ticketOriginAccount;
    }

    @Override
    public TicketOriginAccount selectOrInsertOriginAccount (TicketAccountMapping ticketAccountMapping) {

        if (StrUtil.isNotBlank(ticketAccountMapping.getSameOriginId())) {
            return this.lambdaQuery()
                    .eq(TicketOriginAccount::getSameOriginId, ticketAccountMapping.getSameOriginId())
                    .last("limit 1")
                    .one();
        }
        TicketOriginAccount ticketOriginAccount = selectOriginAccountByMappingInfo(ticketAccountMapping);
        if (ticketOriginAccount == null) {
            ticketOriginAccount = insertOriginAccount(ticketAccountMapping);
        }
        return ticketOriginAccount;
    }

    @Override
    public TicketOriginAccount selectOriginAccountByMappingInfo (TicketAccountMapping ticketAccountMapping) {

        LambdaQueryWrapper<TicketOriginAccount> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TicketOriginAccount::getAccountId, ticketAccountMapping.getAccountId());
        if (StringUtils.isNotBlank(ticketAccountMapping.getEmail())) {
            lambdaQueryWrapper.or().eq(TicketOriginAccount::getEmail, ticketAccountMapping.getEmail());
        }
        if (StringUtils.isNotBlank(ticketAccountMapping.getPhoneNo())) {
            lambdaQueryWrapper.or().eq(TicketOriginAccount::getPhoneNo, ticketAccountMapping.getPhoneNo());
        }
        lambdaQueryWrapper.last("limit 1");
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public void parseOriginAccountToAccountMapping (TicketOriginAccount ticketOriginAccount, TicketAccountMapping ticketAccountMapping) {

        if (ticketOriginAccount == null || ticketAccountMapping == null || StrUtil.isBlank(ticketOriginAccount.getSameOriginId())) {
            return;
        }
        if (ticketOriginAccount.getMatchResult() == AccountMatchResultEnum.NO_NEED_UPDATE) {
            if (StringUtils.isNotEmpty(ticketOriginAccount.getQwUserId())) {
                ticketAccountMapping.setQwUserId(ticketOriginAccount.getQwUserId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getAccountName())) {
                ticketAccountMapping.setQyUserName(ticketOriginAccount.getAccountName());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserId())) {
                ticketAccountMapping.setDdUserId(ticketOriginAccount.getDdUserId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptId())) {
                ticketAccountMapping.setDdUserDeptId(ticketOriginAccount.getDdUserDeptId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptName())) {
                ticketAccountMapping.setDdUserDeptName(ticketOriginAccount.getDdUserDeptName());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getSameOriginId())) {
                ticketAccountMapping.setSameOriginId(ticketOriginAccount.getSameOriginId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getSuperiorId())) {
                ticketAccountMapping.setSuperiorId(ticketOriginAccount.getSuperiorId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDeptManagerIds())) {
                ticketAccountMapping.setDeptManagerIds(ticketOriginAccount.getDeptManagerIds());
            }
        } else {
            if (StringUtils.isEmpty(ticketAccountMapping.getQwUserId())) {
                ticketAccountMapping.setQwUserId(ticketOriginAccount.getQwUserId());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getQyUserName())) {
                ticketAccountMapping.setQyUserName(ticketOriginAccount.getQyUserName());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getDdUserId())) {
                ticketAccountMapping.setDdUserId(ticketOriginAccount.getDdUserId());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getSameOriginId())) {
                ticketAccountMapping.setSameOriginId(ticketOriginAccount.getSameOriginId());
            }
            //如果是ldap的账户,并且是非系统账户，则取源账户数据。
            if (TfsBaseConstant.TFS_SYSTEM_ACCOUNT_TYPE.equals(ticketAccountMapping.getAccountType()) && (false == ticketAccountMapping.getSystemAccount())) {
                if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptId())) {
                    ticketAccountMapping.setDdUserDeptId(ticketOriginAccount.getDdUserDeptId());
                }
                if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptName())) {
                    ticketAccountMapping.setDdUserDeptName(ticketOriginAccount.getDdUserDeptName());
                }
                if (StringUtils.isNotEmpty(ticketOriginAccount.getSuperiorId())) {
                    ticketAccountMapping.setSuperiorId(ticketOriginAccount.getSuperiorId());
                }
                if (StringUtils.isNotEmpty(ticketOriginAccount.getDeptManagerIds())) {
                    ticketAccountMapping.setDeptManagerIds(ticketOriginAccount.getDeptManagerIds());
                }
            } else { //如果是其他类型账户，若本地账户（同远程账户）值为空，则取源账户数据;若本地账户（同远程账户）有值，则取本地账户（同远程账户），不改变当前值。
                if (StringUtils.isEmpty(ticketAccountMapping.getDdUserDeptId())) {
                    ticketAccountMapping.setDdUserDeptId(ticketOriginAccount.getDdUserDeptId());
                }
                if (StringUtils.isEmpty(ticketAccountMapping.getDdUserDeptName())) {
                    ticketAccountMapping.setDdUserDeptName(ticketOriginAccount.getDdUserDeptName());
                }
                if (StringUtils.isEmpty(ticketAccountMapping.getSuperiorId())) {
                    ticketAccountMapping.setSuperiorId(ticketOriginAccount.getSuperiorId());
                }
                if (StringUtils.isEmpty(ticketAccountMapping.getDeptManagerIds())) {
                    ticketAccountMapping.setDeptManagerIds(ticketOriginAccount.getDeptManagerIds());
                }
            }
        }
    }


    @Override
    public void parseOriginAccountToAccountMappingNew (TicketOriginAccount ticketOriginAccount, TicketAccountMapping ticketAccountMapping) {

        if (ticketOriginAccount == null || ticketAccountMapping == null || StrUtil.isBlank(ticketOriginAccount.getSameOriginId())) {
            return;
        }

        if (ticketOriginAccount.getMatchResult() == AccountMatchResultEnum.NO_NEED_UPDATE) {
            if (StringUtils.isNotEmpty(ticketOriginAccount.getQwUserId())) {
                ticketAccountMapping.setQwUserId(ticketOriginAccount.getQwUserId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getAccountName())) {
                ticketAccountMapping.setQyUserName(ticketOriginAccount.getAccountName());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserId())) {
                ticketAccountMapping.setDdUserId(ticketOriginAccount.getDdUserId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptId())) {
                ticketAccountMapping.setDdUserDeptId(ticketOriginAccount.getDdUserDeptId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDdUserDeptName())) {
                ticketAccountMapping.setDdUserDeptName(ticketOriginAccount.getDdUserDeptName());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getSameOriginId())) {
                ticketAccountMapping.setSameOriginId(ticketOriginAccount.getSameOriginId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getSuperiorId())) {
                ticketAccountMapping.setSuperiorId(ticketOriginAccount.getSuperiorId());
            }
            if (StringUtils.isNotEmpty(ticketOriginAccount.getDeptManagerIds())) {
                ticketAccountMapping.setDeptManagerIds(ticketOriginAccount.getDeptManagerIds());
            }
        } else {
            if (StringUtils.isEmpty(ticketAccountMapping.getQwUserId())) {
                ticketAccountMapping.setQwUserId(ticketOriginAccount.getQwUserId());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getQyUserName())) {
                ticketAccountMapping.setQyUserName(ticketOriginAccount.getQyUserName());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getDdUserId())) {
                ticketAccountMapping.setDdUserId(ticketOriginAccount.getDdUserId());
            }
            if (StrUtil.isNotEmpty(ticketOriginAccount.getDdUserDeptId()) && !StrUtil.equals(ticketAccountMapping.getDdUserDeptId(), ticketOriginAccount.getDdUserDeptId())) {
                ticketAccountMapping.setDdUserDeptId(ticketOriginAccount.getDdUserDeptId());
            }
            if (StrUtil.isNotEmpty(ticketOriginAccount.getDdUserDeptName()) && !StrUtil.equals(ticketAccountMapping.getDdUserDeptName(), ticketOriginAccount.getDdUserDeptName())) {
                ticketAccountMapping.setDdUserDeptName(ticketOriginAccount.getDdUserDeptName());
            }
            if (StringUtils.isEmpty(ticketAccountMapping.getSameOriginId())) {
                ticketAccountMapping.setSameOriginId(ticketOriginAccount.getSameOriginId());
            }
            if (StrUtil.isNotEmpty(ticketOriginAccount.getSuperiorId()) && !StrUtil.equals(ticketAccountMapping.getSuperiorId(), ticketOriginAccount.getSuperiorId())) {
                ticketAccountMapping.setSuperiorId(ticketOriginAccount.getSuperiorId());
            }
            if (StrUtil.isNotEmpty(ticketOriginAccount.getDeptManagerIds()) && !StrUtil.equals(ticketAccountMapping.getDeptManagerIds(), ticketOriginAccount.getDeptManagerIds())) {
                ticketAccountMapping.setDeptManagerIds(ticketOriginAccount.getDeptManagerIds());
            }
        }

    }


    @Override
    public void batchMatchOriginAccountInfo () {

        batchMatchOriginAccountInfoWithParam(false);
    }

    @Override
    public void batchSyncFullMatchOriginAccountInfo () {

        batchMatchOriginAccountInfoWithParam(true);
    }

    public void batchMatchOriginAccountInfoWithParam (boolean syncFull) {

        log.info("#####################开始批量同步账户信息，参数syncFull:{}#####################", syncFull);
        List<String> matchResultList = new ArrayList<>();
        matchResultList.add("init");
        matchResultList.add("fail");
        if (syncFull) {
            matchResultList.add("success");
        }
        var needToMatch = this.lambdaQuery()
                .in(TicketOriginAccount::getMatchResult, matchResultList)
//                .eq(TicketOriginAccount::getAccountId, "w02313")
                .list();
        if (CollectionUtils.isEmpty(needToMatch)) {
            return;
        }
        var allTicketAccountMappingList = this.lambdaQuery().select(TicketOriginAccount::getDdUserId, TicketOriginAccount::getAccountId).list();
        Map<String, String> dUser2AccountIdMap = allTicketAccountMappingList.stream()
                .filter(item -> StringUtils.isNoneEmpty(item.getAccountId(), item.getDdUserId()))
                .collect(Collectors.toMap(
                        TicketOriginAccount::getDdUserId,
                        TicketOriginAccount::getAccountId,
                        (v1, v2) -> v1,
                        HashMap::new));

        // 查询企微列表
        List<Integer> depIdList = QwNotify.getDepIds(QwCorporateEnum.SMY);
        if (depIdList == null) {
            log.info("get depId from qw return empty");
            return;
        }
        Map<String, String> qwUserIdMap = depIdList.stream()
                .flatMap(depId -> Optional.ofNullable(QwNotify.getUserFromDepId(depId, QwCorporateEnum.SMY))
                        .orElse(Collections.emptyList()).stream())
                .collect(HashMap::new, (m, v) -> m.put(v.getUserName(), v.getUserId()), HashMap::putAll);

        /*
        //通过qw接口查询部门用户详细信息的集合
        List<QwNotify.UDetails> userList = depIdList.stream()
                .flatMap(depId -> Optional.ofNullable(QwNotify.getUserListFromDepId(depId, QwCorporateEnum.SMY))
                        .orElse(Collections.emptyList()).stream())
                .collect(Collectors.toList());

        // 根据 userList 构建基于 name 和 email 的 Map
        Map<String, String> nameMap = userList.stream()
                .filter(u -> u.getUserName() != null)  // 确保 name 不为 null
                .collect(Collectors.toMap(QwNotify.UDetails::getUserName, QwNotify.UDetails::getUserId, (u1, u2) -> u1));

        Map<String, String> emailMap = userList.stream()
                .filter(u -> u.getEmail() != null)  // 确保 email 不为 null
                .collect(Collectors.toMap(QwNotify.UDetails::getEmail, QwNotify.UDetails::getUserId, (u1, u2) -> u1));
        */

        // 查询钉钉列表
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = new ArrayList<>();
        DingDingClient.geAllDingDingDepartmentList(deptList, Collections.singletonList(DingDingConstant.DINGDING_HEADQUARTER));

        Map<String, OapiV2UserListResponse.ListUserResponse> allDingDingUserMap = DingDingClient.getAllDingDingUserMap(deptList);
        if (CollectionUtils.isEmpty(allDingDingUserMap)) {
            log.info("get allDingDingUserMap from dd return empty");
            return;
        }

        Map<Long, OapiV2DepartmentGetResponse.DeptGetResponse> allDingDingDeptMap = DingDingClient.getAllDingDingDepartmentMap(deptList);
        if (CollectionUtils.isEmpty(allDingDingDeptMap)) {
            log.info("get getAllDingDingDepartmentMap from dd return empty");
            return;
        }

        Map<Long, String> deptIdNameMap = deptList.stream()
                .collect(Collectors.toMap(OapiV2DepartmentListsubResponse.DeptBaseResponse::getDeptId,
                        OapiV2DepartmentListsubResponse.DeptBaseResponse::getName));


        // match 循环
        List<String> failAccountList = new ArrayList<>();
        List<TicketOriginAccount> successAccountList = new ArrayList<>();
        for (TicketOriginAccount mp : needToMatch) {
            String qwUserId = mp.getQwUserId();
            String ddUserId = mp.getDdUserId();
            String ddUserDeptId = mp.getDdUserDeptId();
            String ddUserDeptName = mp.getDdUserDeptName();
            String ddSuperiorId = mp.getSuperiorId();
            String ddDeptManagerIds = mp.getDeptManagerIds();

            //获取企业微信
            if (StrUtil.isBlank(qwUserId)) {
                qwUserId = qwUserIdMap.get(mp.getAccountName());
                if (StrUtil.isBlank(qwUserId)) {
                    if (StrUtil.isNotEmpty(mp.getEmail())) {
                        qwUserId = QwNotify.getUserIdFromEmail(mp.getEmail(), null, 1);
                        if (StrUtil.isBlank(qwUserId)) {
                            qwUserId = QwNotify.getUserIdFromEmail(mp.getEmail(), null, 2);
                        }
                    }
                }
            }

            //获取钉钉
            if (StrUtil.isBlank(ddUserId) || syncFull) {
                OapiV2UserListResponse.ListUserResponse ddUser = allDingDingUserMap.get(mp.getAccountId());
                if (ddUser == null && StrUtil.isNotBlank(mp.getEmail())) {
                    ddUser = allDingDingUserMap.get(mp.getEmail());
                }
                if (ddUser == null && StrUtil.isNotBlank(mp.getPhoneNo())) {
                    ddUser = allDingDingUserMap.get(mp.getPhoneNo());
                }
                if (ddUser == null && StrUtil.isNotBlank(mp.getAccountName())) {
                    ddUser = allDingDingUserMap.get(mp.getAccountName());
                }
                if (ddUser != null) {
                    ddUserId = ddUser.getUserid();
                    if (CollUtil.isNotEmpty(ddUser.getDeptIdList())) {
                        ddUserDeptId = String.valueOf(ddUser.getDeptIdList().get(0));
                        ddUserDeptName = deptIdNameMap.getOrDefault(ddUser.getDeptIdList().get(0), ddUserDeptName);

                        // 设置用户所在部门负责人
                        Set<String> ddUserDeptManagers = new HashSet<>();
                        ddUser.getDeptIdList().forEach(deptId -> {
                            OapiV2DepartmentGetResponse.DeptGetResponse ddUserDeptInfo = allDingDingDeptMap.get(deptId);
                            while (ddUserDeptInfo != null && CollUtil.isEmpty(ddUserDeptInfo.getDeptManagerUseridList())) {
                                ddUserDeptInfo = allDingDingDeptMap.get(ddUserDeptInfo.getParentId());
                                if (ddUserDeptInfo == null || ddUserDeptInfo.getParentId() == null) {
                                    break;
                                }
                            }
                            if (ddUserDeptInfo != null && CollUtil.isNotEmpty(ddUserDeptInfo.getDeptManagerUseridList())) {
                                ddUserDeptManagers.addAll(ddUserDeptInfo.getDeptManagerUseridList());
                            }
                        });
                        List<String> tmpUserAccountIdList = new ArrayList<>();
                        ddUserDeptManagers.forEach(ddUserDeptManager -> {
                            if (StringUtils.isNotEmpty(dUser2AccountIdMap.get(ddUserDeptManager))) {
                                tmpUserAccountIdList.add(dUser2AccountIdMap.get(ddUserDeptManager));
                            }
                        });
                        if (CollUtil.isNotEmpty(tmpUserAccountIdList)) {
                            ddDeptManagerIds = CollUtil.join(tmpUserAccountIdList, ",");
                        }
                    }

                    // 设置用户的上级ID
                    if (StrUtil.isNotBlank(ddUserId)) {
                        OapiV2UserGetResponse.UserGetResponse ddUserInfo = DingDingClient.getUserInfo(ddUserId);
                        if (ddUserInfo != null && StrUtil.isNotBlank(ddUserInfo.getManagerUserid())) {
                            ddSuperiorId = dUser2AccountIdMap.get(ddUserInfo.getManagerUserid());
                        }
                    }
                }
            }

            //赋值
            mp.setQwUserId(qwUserId);
            mp.setDdUserId(ddUserId);
            mp.setDdUserDeptId(ddUserDeptId);
            mp.setDdUserDeptName(ddUserDeptName);
            mp.setSuperiorId(ddSuperiorId);
            mp.setDeptManagerIds(ddDeptManagerIds);
            mp.setUpdateTime(new Date());
            if (StrUtil.hasBlank(qwUserId, ddUserId)) {
                mp.setMatchResult(AccountMatchResultEnum.FAIL);
                failAccountList.add(mp.getAccountId());
            } else {
                mp.setMatchResult(AccountMatchResultEnum.SUCCESS);
                mp.setQyUserName(mp.getAccountName());
                successAccountList.add(mp);
            }
        }

        int totalSize = needToMatch.size();
        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);
            List<TicketOriginAccount> subNeedToMatch = needToMatch.subList(start, end);
            this.updateBatchById(subNeedToMatch);
        }

        // 企业通知管理员去手动匹配失败的,然后同步成功用户到映射表
        sendNoticeToManagerForFailAccount(failAccountList);
        syncSuccessAccountToMapping(successAccountList);
        log.info("#####################结束批量同步账户信息#####################");
    }


    @Override
    public Response<String> syncOriginAccountToMapping (String id) {

        TicketOriginAccount ticketOriginAccount = this.getById(id);
        if (AccountMatchResultEnum.SUCCESS != ticketOriginAccount.getMatchResult()) {
            return Response.error(BizResponseEnums.UNKNOW_EXCEPTION_CODE, "该账号未匹配成功,无法同步");
        }
        this.syncSuccessAccountToMapping(Collections.singletonList(ticketOriginAccount));
        return Response.success();
    }

    @Override
    public List<TicketOriginAccount> selectOriginAccountList (TicketOriginAccountDto ticketOriginAccountDto) {

        return this.baseMapper.selectOriginAccountList(ticketOriginAccountDto);
    }

    @Override
    public void deleteOriginAccount (String id) {

        this.lambdaUpdate().eq(TicketOriginAccount::getId, id)
                .set(TicketOriginAccount::getDeleteTime, new Date())
                .update();
    }

    @Override
    public Response<String> updateTicketOriginAccount (TicketOriginAccount ticketOriginAccount) {

        this.updateById(ticketOriginAccount);
        return Response.success();
    }

    private void sendNoticeToManagerForFailAccount (List<String> failAccountList) {

        if (CollUtil.isEmpty(failAccountList)) {
            return;
        }
        String message = String.format("以下账号【%s】匹配失败，请管理员及时去手动匹配", CollUtil.join(failAccountList, ","));
        notificationService.notifyQw(message, Arrays.asList("yinshasha", "songbing"));
    }

    @Override
    public void syncSuccessAccountToMapping (List<TicketOriginAccount> successAccountList) {

        if (CollUtil.isEmpty(successAccountList)) {
            return;
        }
        Map<String, TicketOriginAccount> originAccountMap = successAccountList.stream().collect(Collectors.toMap(TicketOriginAccount::getSameOriginId, a -> a));
        List<String> originIdList = successAccountList.stream().map(TicketOriginAccount::getSameOriginId).collect(Collectors.toList());

        List<TicketAccountMapping> accountMappingList = ticketAccountMappingService.lambdaQuery()
                .in(TicketAccountMapping::getSameOriginId, originIdList)
                .list();
        for (TicketAccountMapping ticketAccountMapping : accountMappingList) {
            TicketOriginAccount ticketOriginAccount = originAccountMap.get(ticketAccountMapping.getSameOriginId());
            if (ticketOriginAccount == null) {
                continue;
            }
            //替换为原本的
            //parseOriginAccountToAccountMappingNew(ticketOriginAccount, ticketAccountMapping);
            parseOriginAccountToAccountMapping(ticketOriginAccount, ticketAccountMapping);
        }

        int totalSize = accountMappingList.size();
        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);
            List<TicketAccountMapping> subAccountMappingList = accountMappingList.subList(start, end);
            ticketAccountMappingService.updateBatchById(subAccountMappingList);
        }
    }

    @Override
    public void syncSuccessAccountToMappingNew (List<TicketOriginAccount> successAccountList) {

        if (CollUtil.isEmpty(successAccountList)) {
            return;
        }
        Map<String, TicketOriginAccount> originAccountMap = successAccountList.stream().collect(Collectors.toMap(TicketOriginAccount::getSameOriginId, a -> a));
        List<String> originIdList = successAccountList.stream().map(TicketOriginAccount::getSameOriginId).collect(Collectors.toList());

        List<TicketAccountMapping> accountMappingList = ticketAccountMappingService.lambdaQuery()
                .in(TicketAccountMapping::getSameOriginId, originIdList)
                .list();
        for (TicketAccountMapping ticketAccountMapping : accountMappingList) {
            TicketOriginAccount ticketOriginAccount = originAccountMap.get(ticketAccountMapping.getSameOriginId());
            if (ticketOriginAccount == null) {
                continue;
            }
            //替换为原本的
            parseOriginAccountToAccountMappingNew(ticketOriginAccount, ticketAccountMapping);
        }

        int totalSize = accountMappingList.size();
        for (int start = 0; start < totalSize; start += batchSize) {
            int end = Math.min(start + batchSize, totalSize);
            List<TicketAccountMapping> subAccountMappingList = accountMappingList.subList(start, end);
            ticketAccountMappingService.updateBatchById(subAccountMappingList);
        }
    }

}
