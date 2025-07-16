package com.smy.tfs.biz.bo;

import com.smy.tfs.api.dbo.TicketFlowNodeData;
import com.smy.tfs.api.dto.TicketFlowNodeDataDto;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 审批后，用于更新审批节点数据的对象集合
 */
@ToString
@Data
public class TicketFlowNodeApproveFinishBO implements Serializable {

    //当前节点
    private TicketFlowNodeDataDto currentNode;
    /**
     * 下个节点
     */
    private TicketFlowNodeDataDto nextNode;
    /**
     * 未加签前的下个节点
     */
    private TicketFlowNodeDataDto oldNextNode;
    /**
     * 加签节点
     */
    private TicketFlowNodeDataDto addNode;

    public TicketFlowNodeApproveFinishBO() {
    }

    public TicketFlowNodeApproveFinishBO(TicketFlowNodeDataDto currentNode, TicketFlowNodeDataDto nextNode, TicketFlowNodeDataDto oldNextNode, TicketFlowNodeDataDto addNode) {
        this.currentNode = currentNode;
        this.nextNode = nextNode;
        this.oldNextNode = oldNextNode;
        this.addNode = addNode;
    }
}
