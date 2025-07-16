package com.smy.tfs.api.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.common.utils.StringUtils;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Slf4j
public class TicketRemoteAccountDto implements Serializable {

    private static final long serialVersionUID = -3361030973857701467L;

    private String userId;

    private String sameOriginId;

    private String userName;

    private String fullUserName;

    private String userType;

    private String userPhone;

    private String userEmail;

    private String deptId;

    private String deptName;

    private String superDeptId;

    // 上级ID
    private String superiorId;

    // 部门负责人ID(可能多个，多个用逗号隔开)
    private String deptManagerIds;

    private String qywxId;

    private String dingDingId;

    //是否为工单系统默认用户
    private Boolean systemUser;

    public TicketRemoteAccountDto () {

    }

    public static List<TicketRemoteAccountDto> parseAccountMappingListToRemoteAccountList (List<TicketAccountMapping> accountMappingList) {

        List<TicketRemoteAccountDto> remoteAccountList = new ArrayList<>();
        if (CollUtil.isNotEmpty(accountMappingList)) {
            for (TicketAccountMapping accountMapping : accountMappingList) {
                TicketRemoteAccountDto remoteAccount = parseAccountMappingToRemoteAccount(accountMapping);
                if (remoteAccount != null) {
                    remoteAccountList.add(remoteAccount);
                }
            }
        }
        return remoteAccountList;
    }

    public static TicketRemoteAccountDto parseAccountMappingToRemoteAccount (TicketAccountMapping accountMapping) {

        if (accountMapping == null) {
            return null;
        }
        TicketRemoteAccountDto remoteAccount = new TicketRemoteAccountDto();
        remoteAccount.setUserId(accountMapping.getAccountId());
        remoteAccount.setSameOriginId(accountMapping.getSameOriginId());
        remoteAccount.setUserName(accountMapping.getAccountName());
        remoteAccount.setUserType(accountMapping.getAccountType());
        remoteAccount.setFullUserName(accountMapping.getAccountId() + "-" + accountMapping.getAccountName());

        remoteAccount.setUserPhone(accountMapping.getPhoneNo());
        remoteAccount.setUserEmail(accountMapping.getEmail());

        remoteAccount.setQywxId(accountMapping.getQwUserId());
        remoteAccount.setDingDingId(accountMapping.getDdUserId());

        remoteAccount.setSystemUser(accountMapping.getSystemAccount());

        remoteAccount.setSuperiorId(accountMapping.getSuperiorId());
        remoteAccount.setDeptManagerIds(accountMapping.getDeptManagerIds());

        return remoteAccount;
    }

    public static TicketAccountMapping toTicketAccountMapping (TicketRemoteAccountDto ticketRemoteAccountDto) {

        TicketAccountMapping ticketAccountMapping = new TicketAccountMapping();

        ticketAccountMapping.setAccountId(ticketRemoteAccountDto.getUserId());
        ticketAccountMapping.setSameOriginId(ticketRemoteAccountDto.getSameOriginId());
        ticketAccountMapping.setAccountName(ticketRemoteAccountDto.getUserName());
        ticketAccountMapping.setAccountType(ticketRemoteAccountDto.getUserType());
        ticketAccountMapping.setPhoneNo(ticketRemoteAccountDto.getUserPhone());
        ticketAccountMapping.setEmail(ticketRemoteAccountDto.getUserEmail());
        ticketAccountMapping.setQwUserId(ticketRemoteAccountDto.getQywxId());
        ticketAccountMapping.setDdUserId(ticketRemoteAccountDto.getDingDingId());
        ticketAccountMapping.setSystemAccount((ObjectHelper.isNotEmpty(ticketRemoteAccountDto.getSystemUser()) && ticketRemoteAccountDto.getSystemUser()) ? Boolean.TRUE : Boolean.FALSE);
        ticketAccountMapping.setSuperiorId(ticketRemoteAccountDto.getSuperiorId());
        ticketAccountMapping.setDeptManagerIds(ticketRemoteAccountDto.getDeptManagerIds());
        ticketAccountMapping.setDeptId(ticketRemoteAccountDto.getDeptId());
        ticketAccountMapping.setDeptName(ticketRemoteAccountDto.getDeptName());
        ticketAccountMapping.setSuperDeptId(ticketRemoteAccountDto.getSuperDeptId());

        return ticketAccountMapping;
    }

    public static TicketAccountMapping updateLocalAccountMapping (TicketAccountMapping local, TicketRemoteAccountDto remote) {

        if (local == null) {
            local = new TicketAccountMapping();
            local.setSystemAccount(false);
        }
        if (remote == null) {
            return local;
        }

        if (StringUtils.isNotEmpty(remote.getUserId())) {
            local.setAccountId(remote.getUserId());
        }
        if (StringUtils.isNotEmpty(remote.getSameOriginId())) {
            local.setSameOriginId(remote.getSameOriginId());
        }
        if (StringUtils.isNotEmpty(remote.getUserName())) {
            local.setAccountName(remote.getUserName());
        }
        if (StringUtils.isNotEmpty(remote.getUserType())) {
            local.setAccountType(remote.getUserType());
        }

        if (StringUtils.isNotEmpty(remote.getUserPhone())) {
            local.setPhoneNo(remote.getUserPhone());
        }
        if (StringUtils.isNotEmpty(remote.getUserEmail())) {
            local.setEmail(remote.getUserEmail());
        }

        if (StringUtils.isNotEmpty(remote.getDeptId())) {
            local.setDeptId(remote.getDeptId());
        }
        if (StringUtils.isNotEmpty(remote.getDeptName())) {
            local.setDeptName(remote.getDeptName());
        }
        if (StringUtils.isNotEmpty(remote.getSuperDeptId())) {
            local.setSuperDeptId(remote.getSuperDeptId());
        }

        if (StringUtils.isNotEmpty(remote.getSuperiorId())) {
            local.setSuperiorId(remote.getSuperiorId());
        }

        if (StringUtils.isNotEmpty(remote.getDeptManagerIds())) {
            local.setDeptManagerIds(remote.getDeptManagerIds());
        }

        if (StringUtils.isNotEmpty(remote.getQywxId())) {
            local.setQwUserId(remote.getQywxId());
        }
        if (StringUtils.isNotEmpty(remote.getDingDingId())) {
            local.setDdUserId(remote.getDingDingId());
        }

        return local;
    }

    public Boolean equalTicketAccountMapping (TicketRemoteAccountDto ticketRemoteAccountDto, TicketAccountMapping ticketAccountMapping) {

        Boolean flag = Boolean.TRUE;
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getUserName()) && !StringUtils.equals(ticketAccountMapping.getAccountName(), ticketRemoteAccountDto.getUserName())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setAccountName(ticketRemoteAccountDto.getUserName());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getUserType()) && !StringUtils.equals(ticketAccountMapping.getAccountType(), ticketRemoteAccountDto.getUserType())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setAccountType(ticketRemoteAccountDto.getUserType());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getUserPhone()) && !StringUtils.equals(ticketAccountMapping.getPhoneNo(), ticketRemoteAccountDto.getUserPhone())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setPhoneNo(ticketRemoteAccountDto.getUserPhone());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getUserEmail()) && !StringUtils.equals(ticketAccountMapping.getEmail(), ticketRemoteAccountDto.getUserEmail())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setEmail(ticketRemoteAccountDto.getUserEmail());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getQywxId()) && !StringUtils.equals(ticketAccountMapping.getQwUserId(), ticketRemoteAccountDto.getQywxId())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setQwUserId(ticketRemoteAccountDto.getQywxId());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getDingDingId()) && !StringUtils.equals(ticketAccountMapping.getDdUserId(), ticketRemoteAccountDto.getDingDingId())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setDdUserId(ticketRemoteAccountDto.getDingDingId());
        }
        if (null != ticketRemoteAccountDto.getSystemUser() && ticketAccountMapping.getSystemAccount() != ticketRemoteAccountDto.getSystemUser()) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setSystemAccount(null != ticketRemoteAccountDto.getSystemUser() && ticketRemoteAccountDto.getSystemUser() ? Boolean.TRUE : Boolean.FALSE);
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getSuperiorId()) && !StringUtils.equals(ticketAccountMapping.getSuperiorId(), ticketRemoteAccountDto.getSuperiorId())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setSuperiorId(ticketRemoteAccountDto.getSuperiorId());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getDeptManagerIds()) && !StringUtils.equals(ticketAccountMapping.getDeptManagerIds(), ticketRemoteAccountDto.getDeptManagerIds())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setDeptManagerIds(ticketRemoteAccountDto.getDeptManagerIds());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getDeptId()) && !StringUtils.equals(ticketAccountMapping.getDeptId(), ticketRemoteAccountDto.getDeptId())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setDeptId(ticketRemoteAccountDto.getDeptId());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getDeptName()) && !StringUtils.equals(ticketAccountMapping.getDeptName(), ticketRemoteAccountDto.getDeptName())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setDeptName(ticketRemoteAccountDto.getDeptName());
        }
        if (StringUtils.isNotEmpty(ticketRemoteAccountDto.getSuperDeptId()) && !StringUtils.equals(ticketAccountMapping.getSuperDeptId(), ticketRemoteAccountDto.getSuperDeptId())) {
            flag = Boolean.FALSE;
            ticketAccountMapping.setSuperDeptId(ticketRemoteAccountDto.getSuperDeptId());
        }
        if (!flag) {
            log.warn("对象不相等，ticketAccountMapping:{}，ticketRemoteAccountDto:{}", JSONUtil.toJsonStr(ticketAccountMapping), JSONUtil.toJsonStr(ticketRemoteAccountDto));
        }
        return flag;
    }

}
