package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单账户体系表
 * </p>
 *
 * @author zzd
 * @since 2024-04-19
 */
@Getter
@Setter
@TableName("ticket_account")
public class TicketAccount extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -2280180821445177370L;
    /**
     * ID
     */
    private String id;

    /**
     * 账户名称
     */
    private String ticketAccountName;

    /**
     * 账户类型
     */
    private String ticketAccountType;

    /**
     * 账户配置
     */
    private String ticketAccountValue;

    /**
     * 描述
     */
    private String ticketAccountDescription;

}
