package com.smy.tfs.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smy.tfs.api.dbo.TicketAccountMapping;
import com.smy.tfs.api.valid.AddGroup;
import com.smy.tfs.api.valid.DeleteGroup;
import com.smy.tfs.api.valid.OperateGroup;
import com.smy.tfs.api.valid.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
public class TicketAccountMappingDto implements Serializable {


    private static final long serialVersionUID = 4276435900412980524L;
    /**
     * ID
     */
    @NotBlank(message = "账户体系映射id不能为空", groups = {DeleteGroup.class, UpdateGroup.class, OperateGroup.class})
    private String id;

    /**
     * 账户id
     */
    @NotBlank(message = "账户id不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String accountId;

    /**
     * 账户name
     */
    @NotBlank(message = "账户名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String accountName;

    /**
     * 账户类型
     */
    @NotBlank(message = "账户类型不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String accountType;

    private String accountTypeName;

    /**
     * 手机号
     */
    private String phoneNo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 企业微信userid
     */
    private String qwUserId;

    /**
     * dingding用户id
     */
    private String ddUserId;


    /**
     * 企业微信username
     */
    private String qyUserName;

    /**
     * 匹配次数
     */
    private Integer matchCount;

    /**
     * 匹配结果 init, success, fail, ignore
     */
    private String matchResult;

    /**
     * 是否系统用户
     */
    private Boolean systemAccount;

    /**
     * 同源用户ID
     */
    private String sameOriginId;

    /**
     * 是否有同源用户ID
     */
    private String hasOriginId;

    /**
     * 上级账号ID
     */
    private String superiorId;

    /**
     * 部门负责人ID(s)
     */
    private String deptManagerIds;

    //部门ID
    private String deptId;
    //部门名称
    private String deptName;
    //上级部门ID
    private String superDeptId;

    /**     * 创建者     */
    private String createBy;

    /* 创建时间 yyyy-MM-dd HH:mm:ss */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**     * 更新者     */
    private String updateBy;

    /**     * 更新时间 yyyy-MM-dd HH:mm:ss     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deleteTime;

    public TicketAccountMappingDto() {}

    public TicketAccountMappingDto(TicketAccountMapping ticketAccountMapping){
        this.setId(ticketAccountMapping.getId());
        this.setAccountName(ticketAccountMapping.getAccountName());
        this.setAccountType(ticketAccountMapping.getAccountType());
        this.setPhoneNo(ticketAccountMapping.getPhoneNo());
        this.setEmail(ticketAccountMapping.getEmail());
        this.setAccountId(ticketAccountMapping.getAccountId());
        this.setQwUserId(ticketAccountMapping.getQwUserId());
        this.setDdUserId(ticketAccountMapping.getDdUserId());
        this.setQyUserName(ticketAccountMapping.getQyUserName());
        this.setMatchCount(ticketAccountMapping.getMatchCount());
        this.setMatchResult(ticketAccountMapping.getMatchResult());
        this.setSystemAccount(ticketAccountMapping.getSystemAccount());
        this.setSameOriginId(ticketAccountMapping.getSameOriginId());
        this.setSuperiorId(ticketAccountMapping.getSuperiorId());
        this.setDeptManagerIds(ticketAccountMapping.getDeptManagerIds());
        this.setDeptId(ticketAccountMapping.getDeptId());
        this.setDeptName(ticketAccountMapping.getDeptName());
        this.setSuperDeptId(ticketAccountMapping.getSuperDeptId());
        this.setCreateBy(ticketAccountMapping.getCreateBy());
        this.setCreateTime(ticketAccountMapping.getCreateTime());
        this.setUpdateBy(ticketAccountMapping.getUpdateBy());
        this.setUpdateBy(ticketAccountMapping.getUpdateBy());
        this.setDeleteTime(ticketAccountMapping.getDeleteTime());
    }

    public TicketAccountMapping toTicketAccountMapping(TicketAccountMappingDto ticketAccountMappingDto){
        TicketAccountMapping ticketAccountMapping = new TicketAccountMapping();
        ticketAccountMapping.setId(ticketAccountMappingDto.getId());
        ticketAccountMapping.setAccountName(ticketAccountMappingDto.getAccountName());
        ticketAccountMapping.setAccountType(ticketAccountMappingDto.getAccountType());
        ticketAccountMapping.setPhoneNo(ticketAccountMappingDto.getPhoneNo());
        ticketAccountMapping.setEmail(ticketAccountMappingDto.getEmail());
        ticketAccountMapping.setAccountId(ticketAccountMappingDto.getAccountId());
        ticketAccountMapping.setSameOriginId(ticketAccountMappingDto.getSameOriginId());
        ticketAccountMapping.setSuperiorId(ticketAccountMappingDto.getSuperiorId());
        ticketAccountMapping.setDeptManagerIds(ticketAccountMappingDto.getDeptManagerIds());
        ticketAccountMapping.setDeptId(ticketAccountMappingDto.getDeptId());
        ticketAccountMapping.setDeptName(ticketAccountMappingDto.getDeptName());
        ticketAccountMapping.setSuperDeptId(ticketAccountMappingDto.getSuperDeptId());
        ticketAccountMapping.setQwUserId(ticketAccountMappingDto.getQwUserId());
        ticketAccountMapping.setDdUserId(ticketAccountMappingDto.getDdUserId());
        ticketAccountMapping.setQyUserName(ticketAccountMappingDto.getQyUserName());
        ticketAccountMapping.setMatchCount(ticketAccountMappingDto.getMatchCount());
        ticketAccountMapping.setMatchResult(ticketAccountMappingDto.getMatchResult());
        ticketAccountMapping.setSystemAccount(ticketAccountMappingDto.getSystemAccount());
        ticketAccountMapping.setCreateBy(ticketAccountMappingDto.getCreateBy());
        ticketAccountMapping.setCreateTime(ticketAccountMappingDto.getCreateTime());
        ticketAccountMapping.setUpdateBy(ticketAccountMappingDto.getUpdateBy());
        ticketAccountMapping.setUpdateBy(ticketAccountMappingDto.getUpdateBy());
        ticketAccountMapping.setDeleteTime(ticketAccountMappingDto.getDeleteTime());
        return ticketAccountMapping;
    }

}
