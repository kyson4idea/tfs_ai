package com.smy.tfs.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class TicketDispatchDto implements Serializable {

    private static final long serialVersionUID = 3084132855433270586L;
    @NotBlank(message = "选择派单工单id不能为空")
    private String ticketDataId;
    //APPLY_MEMBER_LIST:指定成员  APPLY_GROUP:指定用户组
    private String executorType;

    @NotBlank(message = "选择派单账户类型不能为空")
    private String accountType;

    @NotEmpty(message = "选择派单成员不能为空")
    private List<String> accountIdList;
}
