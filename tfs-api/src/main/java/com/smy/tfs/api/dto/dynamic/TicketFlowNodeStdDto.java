package com.smy.tfs.api.dto.dynamic;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class TicketFlowNodeStdDto implements Serializable {
    private static final long serialVersionUID = -1052875911115801806L;


    public TicketFlowNodeStdDto(String templateId,String userType,String userId,String userName){
        this.templateId = templateId;
        this.dealUsers =  new ArrayList<>();
        TicketDealUser ticketDealUser = new TicketDealUser();
        ticketDealUser.setUserType(userType);
        ticketDealUser.setUserId(userId);
        ticketDealUser.setUserName(userName);
        this.dealUsers.add(ticketDealUser);
    }

    public TicketFlowNodeStdDto(String templateId, List<TicketDealUser> dealUsers) {
        this.templateId = templateId;
        this.dealUsers = dealUsers;
    }

    public TicketFlowNodeStdDto() {

    }

    @RequestParam(value = "审批节点(ID或名称)", example = " ", description = "审批节点")
    private  String templateId;

    @RequestParam(value = "审批人信息", example = " ", description = "审批人信息")
    List<TicketDealUser> dealUsers;

    @RequestParam(value = "抄送人信息", example = " ", description = "抄送人信息")
    List<TicketDealUser> ccUsers;

}
