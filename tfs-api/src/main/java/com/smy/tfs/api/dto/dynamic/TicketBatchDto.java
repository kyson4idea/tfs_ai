package com.smy.tfs.api.dto.dynamic;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TicketBatchDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<BatchDto> failedList;

}
