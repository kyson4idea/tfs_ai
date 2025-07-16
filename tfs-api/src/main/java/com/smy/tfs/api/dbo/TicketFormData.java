package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单表单数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_form_data")
public class TicketFormData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -4953044898554296739L;
    private String id;

    private String ticketDataId;

    /**
     * 模版ID 动态数据 templateId为-1
     */
    private String templateId;


    public TicketFormData(){

    }

    public  TicketFormData(TicketFormTemplate template, String id, String ticketDataId){
        this.id = id;
        this.ticketDataId = ticketDataId;
        this.templateId = template.getId();
        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
    }
}
