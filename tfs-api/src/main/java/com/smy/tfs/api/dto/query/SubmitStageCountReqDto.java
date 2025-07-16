package com.smy.tfs.api.dto.query;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubmitStageCountReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;

}
