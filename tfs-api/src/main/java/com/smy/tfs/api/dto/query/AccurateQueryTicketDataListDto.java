package com.smy.tfs.api.dto.query;

import com.smy.tfs.api.enums.TicketDataStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AccurateQueryTicketDataListDto implements Serializable {
    private static final long serialVersionUID = -7890418778233324492L;
    //工单id
    private String id;
    //工单名称
    private String ticketName;
    //工单状态
    private TicketDataStatusEnum ticketStatus;
    //申请人
    private String applyUser;
    //创建时间,格式"yyyy-MM-dd HH:mm:ss"
    private String createTime;
    //工单标签
    private String tags;

}
