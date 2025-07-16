package com.smy.tfs.api.dto.dynamic;


import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.apidocs.annotations.RequestParam;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ConvertDataParamDto implements Serializable {

    private static final long serialVersionUID = 2167632493512036003L;

    private String ticketDataId;

    private String convertKey;

    private String convertValue;
}
