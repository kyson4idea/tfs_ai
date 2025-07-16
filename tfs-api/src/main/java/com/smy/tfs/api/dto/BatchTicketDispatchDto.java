package com.smy.tfs.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
public class BatchTicketDispatchDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> ticketDataIdList;

    //APPLY_MEMBER_LIST:指定成员  APPLY_GROUP:指定用户组
    private String executorType;

    //为账户成员时必传
    private String accountType;

    private List<String> accountIdList;
}
