package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class TicketDispatchCountDto implements Serializable {
    private static final long serialVersionUID = 7784477449161386423L;

    // 用户id
    private String userId;
    // 待处理的数量
    private Long waitingDispatchCount;

     public TicketDispatchCountDto(String userId, Long waitingDispatchCount){
        this.userId = userId;
         this.waitingDispatchCount = waitingDispatchCount;
    }



}
