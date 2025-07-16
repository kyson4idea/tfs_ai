package com.smy.tfs.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class DownloadTicketDataReqDto implements Serializable {
    private static final long serialVersionUID = 4199236881014667566L;

    //工单所属模版列表
    private List<String> ticketDataIdList;

    //导出列表
    private List<TableColumnDto> tableColumnsList;
    /**
     * 最大限制导出5000条
     */
    private int pageSize = 5000;
    private int pageNum=1;



}
