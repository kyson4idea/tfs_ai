package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 工单流程数据表
 * </p>
 *
 * @author zzd
 * @since 2024-04-16
 */
@Getter
@Setter
@TableName("ticket_flow_data")
public class TicketFlowData extends TfsBaseEntity implements Serializable {


    private static final long serialVersionUID = -2437023862939459084L;
    private String id;

    /**
     * 工单ID
     */
    private String ticketDataId;

    /**
     * 流程模版ID
     */
    private String templateId;

    /**
     * 开始时抄送 [{},{}]
     */
    private String startCc;

    /**
     * 结束时抄送
     */
    private String endCc;

    public TicketFlowData() {

    }

    public TicketFlowData(TicketFlowTemplate template, String id, String ticketDataId) {
        Date now = new Date();
        this.id = id;
        this.ticketDataId = ticketDataId;
        this.templateId = template.getId();
        this.startCc = template.getStartCc();
        this.endCc = template.getEndCc();
        this.setCreateBy("system");
        this.setUpdateBy("system");
        this.setCreateTime(now);
        this.setUpdateTime(now);
    }
}
