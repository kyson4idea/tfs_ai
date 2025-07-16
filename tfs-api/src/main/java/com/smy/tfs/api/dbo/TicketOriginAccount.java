package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.AccountMatchResultEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzd
 * @since 2024-07-01
 */
@Getter
@Setter
@TableName("ticket_origin_account")
public class TicketOriginAccount extends TfsBaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id,数据库自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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
     * 账户name
     */
    private String accountName;

    /**
     * 企业微信username
     */
    private String qyUserName;

    /**
     * dingding用户部门id
     */
    private String ddUserDeptId;

    /**
     * dingding用户部门名称
     */
    private String ddUserDeptName;

    /**
     * 同源用户id:10000自增
     */
    private String sameOriginId;

    /**
     * 匹配结果 init, success, fail, ignore
     */
    private AccountMatchResultEnum matchResult;

    /**
     * 上级ID
     */
    private String superiorId;

    /**
     * 部门负责人ID(可能多个，多个用逗号隔开)
     */
    private String deptManagerIds;
}
