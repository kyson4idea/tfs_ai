package com.smy.tfs.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TableColumnDto implements Serializable{

    private static final long serialVersionUID = -7354932789254040595L;

    /**
     * 工单分类id
     */
    private String fieldCode;

    /**
     * 工单分类code
     */
    private String fieldName;

}
