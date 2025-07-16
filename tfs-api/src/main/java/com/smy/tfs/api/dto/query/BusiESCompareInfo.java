package com.smy.tfs.api.dto.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.smy.tfs.api.enums.BusiESCompareType;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class BusiESCompareInfo implements Serializable {
    private static final long serialVersionUID = 2005578862727892686L;
    private String compareKey;
    private BusiESCompareType compareType;
    private Object compareValue;

}
