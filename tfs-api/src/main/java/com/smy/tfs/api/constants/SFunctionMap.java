package com.smy.tfs.api.constants;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.smy.tfs.api.dbo.TicketFormItemValues;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SFunctionMap implements Serializable {
    private static final long serialVersionUID = -8576135949044328529L;
    private static final Map<String, SFunction<TicketFormItemValues, ?>> FUNCTION_MAP = new HashMap<>();

    static {
        // 初始化映射，将字段名映射到对应的SFunction
        FUNCTION_MAP.put("form_item_value1", TicketFormItemValues::getFormItemValue1);
        FUNCTION_MAP.put("form_item_value2", TicketFormItemValues::getFormItemValue2);
        FUNCTION_MAP.put("form_item_value3", TicketFormItemValues::getFormItemValue3);
        FUNCTION_MAP.put("form_item_value4", TicketFormItemValues::getFormItemValue4);
        FUNCTION_MAP.put("form_item_value5", TicketFormItemValues::getFormItemValue5);
        FUNCTION_MAP.put("form_item_value6", TicketFormItemValues::getFormItemValue6);
        FUNCTION_MAP.put("form_item_value7", TicketFormItemValues::getFormItemValue7);
        FUNCTION_MAP.put("form_item_value8", TicketFormItemValues::getFormItemValue8);
        FUNCTION_MAP.put("form_item_value9", TicketFormItemValues::getFormItemValue9);
        FUNCTION_MAP.put("form_item_value10", TicketFormItemValues::getFormItemValue10);
        FUNCTION_MAP.put("form_item_value11", TicketFormItemValues::getFormItemValue11);
        FUNCTION_MAP.put("form_item_value12", TicketFormItemValues::getFormItemValue12);
        FUNCTION_MAP.put("form_item_value13", TicketFormItemValues::getFormItemValue13);
        FUNCTION_MAP.put("form_item_value14", TicketFormItemValues::getFormItemValue14);
        FUNCTION_MAP.put("form_item_value15", TicketFormItemValues::getFormItemValue15);
        FUNCTION_MAP.put("form_item_value16", TicketFormItemValues::getFormItemValue16);
        FUNCTION_MAP.put("form_item_value17", TicketFormItemValues::getFormItemValue17);
        FUNCTION_MAP.put("form_item_value18", TicketFormItemValues::getFormItemValue18);
        FUNCTION_MAP.put("form_item_value19", TicketFormItemValues::getFormItemValue19);
        FUNCTION_MAP.put("form_item_value20", TicketFormItemValues::getFormItemValue20);
        FUNCTION_MAP.put("form_item_value21", TicketFormItemValues::getFormItemValue21);
        FUNCTION_MAP.put("form_item_value22", TicketFormItemValues::getFormItemValue22);
        FUNCTION_MAP.put("form_item_value23", TicketFormItemValues::getFormItemValue23);
        FUNCTION_MAP.put("form_item_value24", TicketFormItemValues::getFormItemValue24);
        FUNCTION_MAP.put("form_item_value25", TicketFormItemValues::getFormItemValue25);
        FUNCTION_MAP.put("form_item_value26", TicketFormItemValues::getFormItemValue26);
        FUNCTION_MAP.put("form_item_value27", TicketFormItemValues::getFormItemValue27);
        FUNCTION_MAP.put("form_item_value28", TicketFormItemValues::getFormItemValue28);
        FUNCTION_MAP.put("form_item_value29", TicketFormItemValues::getFormItemValue29);
        FUNCTION_MAP.put("form_item_value30", TicketFormItemValues::getFormItemValue30);
    }

    public static SFunction<TicketFormItemValues, ?> getSFunction(String colName) {
        if (ObjectHelper.isNotEmpty(FUNCTION_MAP.get(colName)))
            return FUNCTION_MAP.get(colName);
        else {
            log.error(String.format("未找到相对应的字段(%s)映射函数",colName));
            throw new RuntimeException(String.format("未找到相对应的字段(%s)映射函数",colName));
        }
    }
}
