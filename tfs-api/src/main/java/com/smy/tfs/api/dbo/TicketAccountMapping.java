package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 账户体系映射表
 * </p>
 *
 * @author zzd
 * @since 2024-05-07
 */
@Getter
@Setter
@TableName("ticket_account_mapping")
public class TicketAccountMapping extends TfsBaseEntity implements Serializable{


    private static final long serialVersionUID = -6577717587300488227L;
    private String id;

    /**
     * 手机号
     */
    private String phoneNo;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户id
     */
    private String accountId;

    /**
     * 企业微信userid
     */
    private String qwUserId;

    /**
     * dingding用户id
     */
    private String ddUserId;

    /**
     * 钉钉用户部门id
     */
    private String ddUserDeptId;

    /**
     * 钉钉用户部门名称
     */
    private String ddUserDeptName;

    /**
     * 账户name
     */
    private String accountName;

    /**
     * 账户类型
     */
    private String accountType;

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
     * 上级ID
     */
    private String superiorId;

    /**
     * 部门负责人ID(可能多个，多个用逗号隔开)
     */
    private String deptManagerIds;

    //部门ID
    private String deptId;
    //部门名称
    private String deptName;
    //上级部门ID
    private String superDeptId;
}
