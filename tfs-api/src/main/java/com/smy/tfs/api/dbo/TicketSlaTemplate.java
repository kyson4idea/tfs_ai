package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单sla模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_sla_template")
public class TicketSlaTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 7119793169481770317L;
    private String id;

    private String ticketTemplateId;

}
