package com.smy.tfs.biz.bo.approve;


import com.smy.tfs.api.dto.base.AccountInfo;

//审批时，用于更新数据的对象定义
public class ApproveBO {
    //操作用户
    private AccountInfo dealUser;
    //工单数据
    private TicketData ticketData;
    //当前节点
    private CurrentNode currentNode;
    //加签节点
    private AddNode addNode;
    //下个节点
    private NextNode nextNode;
    //未加签前的下个节点
    private OldNextNode oldNextNode;
}



