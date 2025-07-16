package com.smy.tfs.api.dto;

import com.smy.tfs.api.enums.YESNOEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExtendFieldsMappingDto implements Serializable {
    private static final long serialVersionUID = 3867393171294190021L;

    private YESNOEnum extendEnabled;
    /**
     * 业务工单通用字段映射列表
     */
    private List<BusiTicketDataFieldsMappingDto> extendFields = new ArrayList<>();


}
