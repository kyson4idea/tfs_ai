package com.smy.tfs.api.dto;

import com.smy.tfs.api.dto.query.FuzzyQueryReqDto;
import com.smy.tfs.api.enums.TicketQueryTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageQueryTicketDataReqDto implements Serializable {

    private static final long serialVersionUID = 1L;
    //我发起的 createdByMe
    private TicketQueryTypeEnum queryType;

    //工单所属模版id
    private String templateId;

    //工单所属模版code
    private String TemplateCode;

    //工单所属模版
    private String ticketId;

    //对于系统工单的状态筛选列表
    private List<String> ticketStatusStrList;

    // 工单模版表单项的List
    private List<TicketFormItemAttriDto> formItemList;

    //创建时间范围["2024-11-23 13:00:00","2024-11-24 13:00:00"]
    private String[] createTime;

    //工单结束时间范围["2024-11-23 13:00:00","2024-11-24 13:00:00"]
    private String[] ticketFinishTime;


    private List<String> createdBy;

    private List<String> currentDoneUser;
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;


}
