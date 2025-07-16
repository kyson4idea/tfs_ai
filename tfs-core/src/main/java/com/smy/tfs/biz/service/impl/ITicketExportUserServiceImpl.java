package com.smy.tfs.biz.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.smy.tfs.api.constants.TfsBaseConstant;
import com.smy.tfs.api.dbo.TicketOriginAccount;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.enums.AccountMatchResultEnum;
import com.smy.tfs.api.service.ITicketExportUserService;
import com.smy.tfs.api.service.ITicketOriginAccountService;
import com.smy.tfs.biz.client.DingDingClient;
import com.smy.tfs.biz.client.DingDingConstant;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.framework.tool.LdapUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.dubbo.apidocs.annotations.ApiDoc;
import org.apache.dubbo.apidocs.annotations.ApiModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component("ticketExportUserService")
@org.apache.dubbo.config.annotation.Service
@ApiModule(value = "用户导出服务类", apiInterface = ITicketExportUserService.class)
public class ITicketExportUserServiceImpl implements ITicketExportUserService {

    @Value("${ldap.server:ldap://192.168.20.23:389}")
    private String ldapServer;

    @Value("${ldap.username:PMtest}")
    private String ldapUserName;

    @Value("${ldap.password:Smytest01}")
    private String ldapPassword;

    @Resource
    ThreadPoolTaskExecutor executor;

    private static final int batchSize = 100;

    @Resource
    private ITicketOriginAccountService ticketOriginAccountService;

    @Override
    @ApiDoc(value = "导出域控用户列表", description = "导出域控用户列表")
    public List<TicketRemoteAccountDto> exportLdapUserList () {

        List<TicketRemoteAccountDto> queryResult = new ArrayList<>();
        try {
            JSONArray allLdapUserList = LdapUtil.getAllLdapUserList(ldapUserName, ldapPassword, ldapServer);
            for (Object ldapUser : allLdapUserList) {
                JSONObject jsonObject = JSONUtil.parseObj(ldapUser);

                TicketRemoteAccountDto remoteAccountDto = JSONUtil.toBean(jsonObject, TicketRemoteAccountDto.class);
                remoteAccountDto.setUserType(TfsBaseConstant.TFS_SYSTEM_ACCOUNT_TYPE);

                queryResult.add(remoteAccountDto);
            }

            CompletableFuture.runAsync(() -> {
                syncLdapOriginAccountDDInfo(queryResult);
            }, executor).exceptionally(throwable -> {
                log.error("导出域控用户列表 异步执行 syncLdapOriginAccountDDInfo 异常:", throwable);
                return null;
            });
        } catch (Exception e) {
            log.error("导出域控用户列表失败，原因：", e);
        }
        return queryResult;
    }


    /**
     * 将查询出的域账户 与 钉钉数据进行同步
     * //TODO 同步域账户上级id 完成后 还原原本的定时任务
     *
     * @param remoteAccountDtoList
     */
    private void syncLdapOriginAccountDDInfo (List<TicketRemoteAccountDto> remoteAccountDtoList) {

        log.info("导出域控用户列表后 同步钉钉账户任务启动");

        //转换实体类，进行dingding同步
        List<TicketOriginAccount> needToMatch = convertDtoListToOriginList(remoteAccountDtoList);
        if (CollUtil.isEmpty(needToMatch)) {
            log.info("域控用户列表为空：{}", needToMatch.size());
            return;
        }
        HashMap<String, String> ddUserAccountMap = needToMatch.stream()
                .filter(item -> ObjectUtil.isNotEmpty(item.getDdUserId()))
                .collect(Collectors.toMap(
                        TicketOriginAccount::getDdUserId,
                        TicketOriginAccount::getAccountId,
                        (v1, v2) -> v1,
                        HashMap::new));

        // 查询钉钉列表
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = new ArrayList<>();
        DingDingClient.geAllDingDingDepartmentList(deptList, Collections.singletonList(DingDingConstant.DINGDING_HEADQUARTER));

        Map<String, OapiV2UserListResponse.ListUserResponse> allDingDingUserMap = DingDingClient.getAllDingDingUserMap(deptList);
        if (CollectionUtils.isEmpty(allDingDingUserMap)) {
            log.info("get syncLdapOriginAccountDDInfo from dd return empty");
            return;
        }

        Map<Long, OapiV2DepartmentGetResponse.DeptGetResponse> allDingDingDeptMap = DingDingClient.getAllDingDingDepartmentMap(deptList);
        if (CollectionUtils.isEmpty(allDingDingDeptMap)) {
            log.info("get syncLdapOriginAccountDDInfo from dd return empty");
            return;
        }

        Map<Long, String> deptIdNameMap = deptList.stream()
                .collect(Collectors.toMap(OapiV2DepartmentListsubResponse.DeptBaseResponse::getDeptId,
                        OapiV2DepartmentListsubResponse.DeptBaseResponse::getName));
        // match 循环
        List<String> failAccountList = new ArrayList<>();
        List<TicketOriginAccount> successAccountList = new ArrayList<>();
        for (TicketOriginAccount mp : needToMatch) {

            String ddUserId = mp.getDdUserId();
            String ddUserDeptId = mp.getDdUserDeptId();
            String ddUserDeptName = mp.getDdUserDeptName();
            String ddSuperiorId = mp.getSuperiorId();
            String ddDeptManagerIds = mp.getDeptManagerIds();

            //获取钉钉
            OapiV2UserListResponse.ListUserResponse ddUser = Stream.of(
                            mp.getAccountId(),
                            mp.getEmail(),
                            mp.getPhoneNo(),
                            mp.getAccountName()
                    )
                    .filter(StrUtil::isNotBlank)
                    .map(allDingDingUserMap::get)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (ObjectUtil.isNotEmpty(ddUser)) {

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
                        if (StringUtils.isNotEmpty(ddUserAccountMap.get(ddUserDeptManager))) {
                            tmpUserAccountIdList.add(ddUserAccountMap.get(ddUserDeptManager));
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
                        ddSuperiorId = ddUserAccountMap.get(ddUserInfo.getManagerUserid());
                    }
                }
            }

            mp.setDdUserId(ddUserId);
            mp.setDdUserDeptId(ddUserDeptId);
            mp.setDdUserDeptName(ddUserDeptName);
            mp.setSuperiorId(ddSuperiorId);
            mp.setDeptManagerIds(ddDeptManagerIds);
            mp.setUpdateTime(new Date());

            if (StrUtil.isBlank(ddUserId)) {
                mp.setMatchResult(AccountMatchResultEnum.FAIL);
                failAccountList.add(mp.getAccountId());
            } else {
                mp.setMatchResult(AccountMatchResultEnum.SUCCESS);
                mp.setQyUserName(mp.getAccountName());
                successAccountList.add(mp);
            }
        }

        List<List<TicketOriginAccount>> partitions = ListUtil.partition(needToMatch, batchSize);
        partitions.forEach(batch -> ticketOriginAccountService.updateBatchById(batch));

        ticketOriginAccountService.syncSuccessAccountToMappingNew(successAccountList);
        log.info("导出域控用户列表后 同步钉钉账户任务结束 总计同步 {} 条数据", needToMatch.size());
    }

    private List<TicketOriginAccount> convertDtoListToOriginList (List<TicketRemoteAccountDto> dtoList) {

        List<String> userIds = Optional.ofNullable(dtoList)
                .orElseGet(ArrayList::new)
                .stream()
                .map(TicketRemoteAccountDto::getUserId)
                .filter(ObjectUtil::isNotEmpty)
                .distinct()  // 去重
                .collect(Collectors.toList());

        List<TicketOriginAccount> list = Collections.emptyList();
        if (!CollectionUtil.isEmpty(userIds)) {
            list = ticketOriginAccountService.lambdaQuery()
                    .in(TicketOriginAccount::getAccountId, userIds)
                    //.select(TicketOriginAccount::getId, TicketOriginAccount::getDdUserId, TicketOriginAccount::getAccountId)
                    .list();

        }
        return list;
    }

}
