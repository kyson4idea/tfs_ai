package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GeneralQueryRespDto implements Serializable {
    private static final long serialVersionUID = 1934069401518000348L;
    //工单查询数据列表
    private List<GeneralQueryTicketDataListDto> rows;



}
