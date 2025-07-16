package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccurateQueryRespDto implements Serializable {
    private static final long serialVersionUID = 8385266352722830420L;
    //工单数据列表
    private List<AccurateQueryTicketDataListDto> rows;



}
