package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.dto.TicketRemoteAccountDto;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.DefaultExecutorTypeEnum;
import com.smy.tfs.api.enums.DeptLevelEnum;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.api.service.ITicketExecutorGroupService;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 工单流程节点执行人数据表
 * //TODO 在工单申请时，完成审批人的运算 executorList
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_node_executor_data")
@Slf4j
public class TicketFlowNodeExecutorData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -889565911383803419L;
    private String id;

    private String templateId;

    private String ticketDataId;

    private String ticketFlowNodeDataId;

    /**
     * @see com.smy.tfs.api.enums.ExecutorTypeEnum
     */
    private ExecutorTypeEnum executorType;

    //案例:
    // executorType是:sMemberList  executorValue是:域账号-ID-NAME&域账号-ID-NAME,
    // executorType是:sGroup       executorValue是:101&102,
    // executorType是:sLeader      executorValue是:域账号-ID-Name
    // executorType是:sSelf      executorValue是:域账号-ID-Name
    /**
     * @see com.smy.tfs.api.enums.ExecutorTypeEnum
     */
    private String executorValue;

    //todo 工单发起时生成数据
    //TYPE:ID-VALUE,TYPE:ID-VALUE
    //示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
    private String executorList;

    //审批完成的人 域账号:02158-Owen,催收:123746-张三催员
    private String executorDoneList;

    /**
     * 当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]
     */
    private String groupValue;

    private DefaultExecutorTypeEnum defaultExecutorType;

    private String defaultExecutorValue;

    public String ToUserList(ExecutorTypeEnum executorType, String executorValue) {
        switch (executorType) {
            case CA_MEMBER_LIST:
            case APPLY_MEMBER_LIST:
            case CE_MEMBER_LIST:
                return executorValue;
            default:
                throw new NotImplementedException("未实现的枚举类型");
        }
    }

    public TicketFlowNodeExecutorData() {

    }

    /**
     * 获取真实存在的人员信息
     * @param ticketAccountService
     * @return
     */
    public String getExecutorValue(ITicketAccountService ticketAccountService){
        //如果是指定成员
        List<AccountInfo> memberList = AccountInfo.ToAccountInfoList(executorValue);
        Iterator<AccountInfo> iterator = memberList.iterator();
        while (iterator.hasNext()) {
            AccountInfo accountInfo = iterator.next();
            var accountObj = ticketAccountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
            if (null == accountObj  || null == accountObj.getUserId()) {
                iterator.remove();
            }
            if (ObjectHelper.isEmpty(memberList)) {
                executorValue = StringUtils.EMPTY;
                return executorValue;
            }
        }
        executorValue = AccountInfo.ToAccountInfoListStr(memberList);
        return executorValue;
    }

    /**
     * 获取真实存在的默认人员信息
     * @param ticketAccountService
     * @return
     */
    public String getDefaultExecutorValue(AccountInfo applyUser, ITicketAccountService ticketAccountService, String nodeName){
        //如果默认是申请人本人
        if (ObjectHelper.isEmpty(defaultExecutorType) || defaultExecutorType == DefaultExecutorTypeEnum.DEFAULT_SELF) {
            defaultExecutorValue = AccountInfo.ToAccountInfoListStr(Arrays.asList(applyUser));
        }
        //如果默认是申请人指定成员
        if (defaultExecutorType == DefaultExecutorTypeEnum.DEFAULT_MEMBER_LIST || defaultExecutorType == DefaultExecutorTypeEnum.DEFAULT_BIZ_MANAGER) {
            List<AccountInfo> defaultMemberList = AccountInfo.ToAccountInfoList(defaultExecutorValue);
            Iterator<AccountInfo> iterator = defaultMemberList.iterator();
            while (iterator.hasNext()) {
                AccountInfo accountInfo = iterator.next();
                var accountObj = ticketAccountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                if (null == accountObj  || null == accountObj.getUserId()) {
                    iterator.remove();
                }
                if (ObjectHelper.isEmpty(defaultMemberList)) {
                    throw new NotImplementedException(String.format("当前节点(%s)触发审批人为空的逻辑，指定成员：未匹配到用户，请联系管理员查看配置信息", nodeName));
                }
            }
            defaultExecutorValue = AccountInfo.ToAccountInfoListStr(defaultMemberList);
        }
        return defaultExecutorValue;
    }

    public TicketFlowNodeExecutorData(
            TicketFlowNodeExecutorTemplate template,
            String id,
            String ticketDataId,
            String ticketFlowNodeDataId,
            String nodeName,
            AccountInfo applyUser,
            ITicketExecutorGroupService ticketExecutorGroupService,
            ITicketAccountService ticketAccountService,
            List<AccountInfo> pointUserList,
            List<AccountInfo> pointccUserList
    ) {
        this.id = id;
        this.templateId = template.getId();
        this.ticketDataId = ticketDataId;
        this.ticketFlowNodeDataId = ticketFlowNodeDataId;
        this.executorType = template.getExecutorType();
        this.executorValue = template.getExecutorValue();
        this.groupValue = template.getGroupValue();
        this.defaultExecutorType = template.getDefaultExecutorType();
        this.defaultExecutorValue = template.getDefaultExecutorValue();
        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());

        switch (this.executorType) {
            case APPLY_MEMBER_LIST:
                this.executorList = getExecutorValue(ticketAccountService);
                if (StringUtils.isEmpty(executorList)) {
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                }
                break;
            case CA_MEMBER_LIST:
            case CE_MEMBER_LIST:
            case APPLY_EXTERNAL_APPROVER:
            case CA_EXTERNAL_APPROVER:
            case CE_EXTERNAL_APPROVER:
                this.executorList = this.executorValue;
                break;
            case CA_GROUP:
            case APPLY_GROUP:
            case CE_GROUP:
                //组信息
                Map<String, AccountInfo> accountInfoMap = getAccountInfoMapByAccountInfo(this.executorValue, ticketExecutorGroupService);
                if (accountInfoMap.isEmpty()) {
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    break;
                }
                List<AccountInfo> accountInfoList = new ArrayList<>(accountInfoMap.values());
                this.executorList = AccountInfo.ToAccountInfoListStr(accountInfoList);
                break;
            case APPLY_SELF:
            case CA_SELF:
            case CE_SELF:
                List<AccountInfo> selfUserList = new ArrayList<>();
                selfUserList.add(applyUser);
                this.executorList = AccountInfo.ToAccountInfoListStr(selfUserList);
                break;
            case APPLY_LEADER:
            case CA_LEADER:
            case CE_LEADER:
                List<AccountInfo> leaderList = new ArrayList<>();
                TicketRemoteAccountDto leader = ticketAccountService.getLeaderByTypeAndId(applyUser.getAccountType(), applyUser.getAccountId());
                if (null == leader) {
                    ticketAccountService.notifyQwMsg(
                            String.format("未找到用户【userType:%s userId:%s】上级，请及时处理！", applyUser.getAccountType(), applyUser.getAccountId()),
                            Arrays.asList("songbing", "owen", "zhangzedong")
                    );
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                } else {
                    leaderList.add(new AccountInfo(leader.getSameOriginId(), leader.getUserType(), leader.getUserId(), leader.getUserName()));
                    this.executorList = AccountInfo.ToAccountInfoListStr(leaderList);
                }
                break;
            case APPLY_DEPT_MANAGERS:
            case CA_DEPT_MANAGERS:
            case CE_DEPT_MANAGERS:
                List<AccountInfo> deptManagerList = new ArrayList<>();
                List<TicketRemoteAccountDto> deptRemoteAccountDtoList = ticketAccountService.getDeptManagersByTypeAndId(applyUser.getAccountType(), applyUser.getAccountId());
                if (CollectionUtils.isNotEmpty(deptRemoteAccountDtoList)) {
                    deptRemoteAccountDtoList.forEach(deptRemoteAccountDto -> deptManagerList.add(new AccountInfo(deptRemoteAccountDto.getSameOriginId(), deptRemoteAccountDto.getUserType(), deptRemoteAccountDto.getUserId(), deptRemoteAccountDto.getUserName())));
                    this.executorList = AccountInfo.ToAccountInfoListStr(deptManagerList);
                } else {
                    //自己兜底
                    ticketAccountService.notifyQwMsg(
                            String.format("未找到用户【userType:%s userId:%s】部门负责人，请及时处理！", applyUser.getAccountType(), applyUser.getAccountId()),
                            Arrays.asList("songbing", "owen", "zhangzedong")
                    );
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                }
                break;
            case APPLY_POINT:
                if (CollectionUtils.isNotEmpty(pointUserList)) {
                    pointUserList = AccountInfo.Distinct(pointUserList);
                    for (AccountInfo accountInfo : pointUserList) {
                        var accountObj = ticketAccountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                        if (null == accountObj || null == accountObj.getUserId()) {
                            throw new NotImplementedException(String.format("指定人：%s，用户不存在工单系统", accountInfo));
                        }
                        accountInfo.setAccountName(accountObj.getUserName());
                    }
                    this.executorList = AccountInfo.ToAccountInfoListStr(pointUserList);
                } else {
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                }
                if (ObjectHelper.isNotEmpty(pointccUserList)) {
                    if (CollectionUtils.isNotEmpty(pointUserList)) {
                        pointUserList = AccountInfo.Distinct(pointUserList);
                        for (AccountInfo accountInfo : pointUserList) {
                            var accountObj = ticketAccountService.getTicketRemoteAccountByIdAndType(accountInfo.getAccountId(), accountInfo.getAccountType());
                            if (null == accountObj || null == accountObj.getUserId()) {
                                throw new NotImplementedException(String.format("指定人：%s，用户不存在工单系统", accountInfo));
                            }
                            accountInfo.setAccountName(accountObj.getUserName());
                        }
                        this.executorList = AccountInfo.ToAccountInfoListStr(pointUserList);
                    }
                }
                break;
            case APPLY_DEPT_POINT:
                //部門人員信息
                List<AccountInfo> deptList = AccountInfo.ToAccountInfoList(this.executorValue);
                if (ObjectHelper.isEmpty(deptList)) {
                    throw new NotImplementedException(String.format("指定部门：%s，部门信息不存在", deptList));
                }

                DeptLevelEnum deptLevel = DeptLevelEnum.getEnumByCode(deptList.get(0).getAccountId());
                List<AccountInfo> deptAccountInfoList = ticketAccountService.getDeptLevelAccountInfoList(deptLevel, applyUser.getAccountId(), applyUser.getAccountType());
                if (CollectionUtils.isEmpty(deptAccountInfoList)) {
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    break;
                }

                //组信息
                Map<String, AccountInfo> actInfoMap = getAccountInfoMapByAccountInfo(this.groupValue, ticketExecutorGroupService);
                List<AccountInfo> groupAccountInfoList = new ArrayList<>();
                if (!actInfoMap.isEmpty()) {
                    groupAccountInfoList = new ArrayList<>(actInfoMap.values());
                }

                if (CollectionUtils.isEmpty(groupAccountInfoList)) {
                    this.executorList = AccountInfo.ToAccountInfoListStr(deptAccountInfoList);
                    break;
                }

                //部門人員信息,符合选择的组信息
                List<AccountInfo> interAccountInfoList = deptAccountInfoList.stream()
                        .filter(groupAccountInfoList::contains)
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(interAccountInfoList)) {
                    this.executorList = getDefaultExecutorValue(applyUser, ticketAccountService, nodeName);
                    break;
                }
                this.executorList = AccountInfo.ToAccountInfoListStr(interAccountInfoList);
                break;

            default:
                throw new NotImplementedException(String.format("未实现的枚举类型:%s", this.executorType));

        }
        this.executorDoneList = "";
    }


    private Map<String, AccountInfo> getAccountInfoMapByAccountInfo(String accountInfoStr, ITicketExecutorGroupService ticketExecutorGroupService) {
        //组信息
        Map<String, AccountInfo> accountInfoMap = new HashMap<>();
        if (StringUtils.isEmpty(accountInfoStr)) {
            return accountInfoMap;
        }
        List<AccountInfo> gdList = AccountInfo.ToAccountInfoList(accountInfoStr);
        if (CollectionUtils.isNotEmpty(gdList)) {
            String[] groupIds = gdList.stream().map(AccountInfo::getAccountId).toArray(String[]::new);
            List<TicketExecutorGroup> groupList = ticketExecutorGroupService.lambdaQuery().in(TicketExecutorGroup::getId, groupIds).isNull(TicketExecutorGroup::getDeleteTime).list();
            if (CollectionUtils.isNotEmpty(groupList)) {
                for (TicketExecutorGroup group : groupList) {
                    List<AccountInfo> accountInfoList = AccountInfo.ToAccountInfoList(group.getAccountInfo());
                    if (CollectionUtils.isNotEmpty(accountInfoList)) {
                        for (AccountInfo accountInfo : accountInfoList) {
                            if (!accountInfoMap.containsKey(accountInfo.getAccountType() + "-" + accountInfo.getAccountId())) {
                                accountInfoMap.put(accountInfo.getAccountType() + "-" + accountInfo.getAccountId(), accountInfo);
                            }
                        }
                    }
                }

            }
        }
        return accountInfoMap;
    }

}
