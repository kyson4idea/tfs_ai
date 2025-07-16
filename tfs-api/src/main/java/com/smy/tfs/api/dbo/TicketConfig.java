package com.smy.tfs.api.dbo;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("ticket_config")
public class TicketConfig extends TfsBaseEntity implements Serializable {
    private static final long serialVersionUID = -3750612024012797869L;

    private String id;

    //优先级 ticket_flow_node_data_id>ticket_data_id>ticket_tempalte_id
    private String ticketTemplateId;

    //{"":[""]}
    private String ticketConfigStr;

}
