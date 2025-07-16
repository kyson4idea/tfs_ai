package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 工单表单模版表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_form_template")
public class TicketFormTemplate extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = 824765144162923726L;
    private String id;

    private String ticketTemplateId;

}
