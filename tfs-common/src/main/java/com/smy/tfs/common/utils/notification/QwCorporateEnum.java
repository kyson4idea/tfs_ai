package com.smy.tfs.common.utils.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum QwCorporateEnum {
    SMY("SMY","萨摩耶主体","-smy"),
    HJSD("HJSD","鸿吉事达","-hjsd")

    ;

    private String code;
    private String desc;
    private String corporateSign;

    public static QwCorporateEnum getEnumByCode(String code){
        for (QwCorporateEnum qwCorporateEnum : QwCorporateEnum.values()) {
            if (qwCorporateEnum.getCode().equalsIgnoreCase(code)){
                return qwCorporateEnum;
            }
        }
        return null;
    }
}
