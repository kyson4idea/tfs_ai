package com.smy.tfs.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class BusiTicketDataResponseDto implements Serializable {
    private static final long serialVersionUID = 8405993150533748299L;
    /**
     * 业务工单对象列表
     */
    private List<BusiTicketDataDto> busiTicketDataDtoList = new ArrayList<>();



}
