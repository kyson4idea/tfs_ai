package com.smy.tfs.api.dbo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smy.tfs.api.enums.ActionTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@TableName("ticket_flow_node_action_data")
public class TicketFlowNodeActionData extends TfsBaseEntity implements Serializable {

    private static final long serialVersionUID = -889565911383801205L;
    private String id;

    private String templateId;

    private String ticketDataId;

    private String ticketFlowNodeDataId;

    private String actionName;

    private ActionTypeEnum actionType;

    //{"update": [{"code": "name","value": "aaa"}]}
    private String actionValue;

    private String actionConfig;

    public TicketFlowNodeActionData() {
    }

    public TicketFlowNodeActionData(
            String id,
            String ticketDataId,
            TicketFlowNodeActionTemplate template,
            TicketFlowNodeData nodeData
    ) {
        Date now = new Date();
        this.id = id;
        this.ticketDataId = ticketDataId;
        if (nodeData == null) {
            throw new RuntimeException("nodeData is null");
        }
        this.ticketFlowNodeDataId = nodeData.getId();
        if (template != null) {
            this.templateId = template.getId();
            this.actionName = template.getActionName();
            this.actionType = template.getActionType();
            this.actionValue = template.getActionValue();
            this.actionConfig = template.getActionConfig();
        }
        this.setDeleteTime(null);
        this.setCreateTime(now);
        this.setUpdateTime(now);
        this.setCreateBy("system");
        this.setUpdateBy("system");
    }
}
