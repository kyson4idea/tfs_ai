package com.smy.tfs.api.dto.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.smy.tfs.api.enums.BusiCompareType;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class BusiCompareInfo implements Serializable {
    private static final long serialVersionUID = 2005578862727892686L;
    private String compareId;
    private BusiCompareType compareType;
    private Object compareValue;

    @JsonCreator
    public static BusiCompareType fromValue(String s) {
        if (ObjectHelper.isNotEmpty(s)) {
            for (BusiCompareType f : BusiCompareType.values()) {
                if (f.toString().equalsIgnoreCase(s)) {
                    return f;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant " + BusiCompareType.class.getName() + "." + s);
    }

    public static void main(String[] args) {

    }
}
