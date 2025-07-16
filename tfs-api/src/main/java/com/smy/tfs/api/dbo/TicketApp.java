package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.YESNOEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_app")
public class TicketApp extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -3750612024012797839L;

    private String id;

    /**
     * 应用名称
     */
    private String appName;


    /**
     * 账户类型类型 例如域账号、催收、ows等
     **/
    private String accountType;

    /**
     * 应用描述
     */
    private String appDesc;

    /**
     * 应用管理员（只能域账号）
     * []
     */
    private String appAdminUsers;

    /**
     * 企微群聊ID
     */
    private String wxChatGroupId;

    /**
     * 是否开启工单分类
     */
    private YESNOEnum categoryEnabled;

    /**
     * 是否开启工单扩展字段
     */
    private YESNOEnum extendEnabled;

    /**
     * 扩展字段
     */
    private String extendFields;
}
