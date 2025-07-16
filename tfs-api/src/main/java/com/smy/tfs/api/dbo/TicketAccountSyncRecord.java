package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单账户体系同步记录表
 * </p>
 *
 * @author zzd
 * @since 2024-04-23
 */
@Getter
@Setter
@TableName("ticket_account_sync_record")
public class TicketAccountSyncRecord extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -4973184196276198925L;
    /**
     * ID
     */
    private String id;

    /**
     * 账户配置id
     */
    private String ticketAccountId;

    /**
     * 同步结果
     */
    private String syncResult;

    /**
     * 同步结果描述
     */
    private String syncResultDes;

}
