package com.smy.tfs.api.dto;

import com.smy.tfs.api.dbo.TicketFlowNodeExecutorData;
import com.smy.tfs.api.dto.base.AccountInfo;
import com.smy.tfs.api.enums.DefaultExecutorTypeEnum;
import com.smy.tfs.api.enums.ExecutorTypeEnum;
import com.smy.tfs.api.service.ITicketAccountService;
import com.smy.tfs.common.annotation.Excel;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 工单流程节点执行人数据对象 ticket_flow_node_excutor_data
 *
 * @author zzd
 * @date 2024-04-11
 */
@Data @Getter @Setter
public class TicketFlowNodeExecutorDataDto implements Serializable {

    private static final long serialVersionUID = -6218501564323870471L;
    /**
     * $column.columnComment
     */
    private String id;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String templateId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String ticketDataId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String ticketFlowNodeDataId;

    /** 上级     用户组     发起时指定成员     提交本人 */
    private ExecutorTypeEnum executorType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String executorValue;

    /**
     * 审批人/抄送人列表。
     * 示例：[{"accountId":"o02157","accountName":"Owen","accountType":"oms"}]
     *@see com.smy.tfs.api.dto.base.AccountInfo
    */
    private String executorList;

    //审批完成的人 域账号:02158-Owen,催收:123746-张三催员
    private String executorDoneList;

    /**当executor_type为"APPLY_DEPT_POINT"时，组信息：[{"accountType":"-1","accountId":"","accountName":""}]*/
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

    public  TicketFlowNodeExecutorDataDto() {
    }
    public TicketFlowNodeExecutorDataDto(TicketFlowNodeExecutorData ticketFlowNodeExecutorData) {
        this.id = ticketFlowNodeExecutorData.getId();
        this.templateId = ticketFlowNodeExecutorData.getTemplateId();
        this.ticketDataId = ticketFlowNodeExecutorData.getTicketDataId();
        this.ticketFlowNodeDataId = ticketFlowNodeExecutorData.getTicketFlowNodeDataId();
        this.executorType = ticketFlowNodeExecutorData.getExecutorType();
        this.executorValue = ticketFlowNodeExecutorData.getExecutorValue();
        this.executorList = ticketFlowNodeExecutorData.getExecutorList();
        this.executorDoneList = ticketFlowNodeExecutorData.getExecutorDoneList();
        this.groupValue = ticketFlowNodeExecutorData.getGroupValue();
        this.defaultExecutorType = ticketFlowNodeExecutorData.getDefaultExecutorType();
        this.defaultExecutorValue = ticketFlowNodeExecutorData.getDefaultExecutorValue();


    }
    public TicketFlowNodeExecutorData toTicketFlowNodeExecutorData() {
        TicketFlowNodeExecutorData ticketFlowNodeExecutorData = new TicketFlowNodeExecutorData();
        ticketFlowNodeExecutorData.setId(this.id);
        ticketFlowNodeExecutorData.setTemplateId(this.templateId);
        ticketFlowNodeExecutorData.setTicketDataId(this.ticketDataId);
        ticketFlowNodeExecutorData.setTicketFlowNodeDataId(this.ticketFlowNodeDataId);
        ticketFlowNodeExecutorData.setExecutorType(this.executorType);
        ticketFlowNodeExecutorData.setExecutorValue(this.executorValue);
        ticketFlowNodeExecutorData.setExecutorList(this.executorList);
        ticketFlowNodeExecutorData.setExecutorDoneList(this.executorDoneList);
        ticketFlowNodeExecutorData.setGroupValue(this.groupValue);
        ticketFlowNodeExecutorData.setDefaultExecutorType(this.defaultExecutorType);
        ticketFlowNodeExecutorData.setDefaultExecutorValue(this.defaultExecutorValue);
        return ticketFlowNodeExecutorData;

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
}
