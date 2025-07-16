package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;

/**
 * <p>
 * 应用人员组表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_executor_group")
public class TicketExecutorGroup extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -7011638348106461182L;
    @RequestParam(value = "应用人员组id",example="1001",description = "应用人员组id")
    private String id;


    @RequestParam(value = "所属appID",example="1001",description = "所属appID")
    private String appId;

    /**
     * 应用组名称
     */
    @RequestParam(value = "应用组名称",example="催收部门组",description = "应用组名称")
    private String executorGroupName;

    /**
     * 应用组描述
     */
    @RequestParam(value = "应用组描述",example="催收部门组",description = "应用组描述")
    private String executorGroupDesc;

    /**
     * 账户信息
     * [
     * {accountType:””,accountId:””},
     * {account_type:””,account_id:””},
     * {account_type:””,account_id:””},
     * ]
     */
    @RequestParam(value = "账户信息",example="[{accountType:”ldap”,accountId:”1001”},{account_type:”ldap”,account_id:”1002”},",description = "账户信息")
    private String accountInfo;


}
