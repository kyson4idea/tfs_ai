package com.smy.tfs.api.dto.dynamic;


import lombok.Data;

import java.io.Serializable;

@Data
public class BatchDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String erroMsg;

}
