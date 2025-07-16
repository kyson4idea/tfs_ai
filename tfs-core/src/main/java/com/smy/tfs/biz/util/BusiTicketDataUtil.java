package com.smy.tfs.biz.util;

import com.alibaba.fastjson2.JSONArray;
import com.smy.tfs.api.dbo.TicketApp;
import com.smy.tfs.api.dto.BusiTicketDataFieldsMappingDto;
import com.smy.tfs.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aes加密
 *
 * @author smy
 */
public class BusiTicketDataUtil {
    public static List<BusiTicketDataFieldsMappingDto> getBusiTicketDataFieldsMapping(TicketApp ticketApp) {
        List<BusiTicketDataFieldsMappingDto> extendFieldsList = new ArrayList<>();
        String extendFields = ticketApp.getExtendFields();
        if (StringUtils.isNotEmpty(extendFields)) {
            extendFieldsList = JSONArray.parseArray(extendFields, BusiTicketDataFieldsMappingDto.class);
        }
        return extendFieldsList;
    }

    public static String objToString(Object obj) {
        if (obj == null) {
            return "[]"; // 空值返回空列表格式
        }
        // 1. 如果已经是 List，直接转换
        if (obj instanceof List) {
            return listToString((List<?>) obj);
        }
        return obj.toString();
    }

    // 通用转换方法
    public static <T> String listToString(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }

        String content = list.stream()
                .map(element -> {
                    if (element instanceof String) {
                        return "\"" + element + "\""; // 字符串加双引号
                    } else {
                        return String.valueOf(element); // 其他类型直接转字符串
                    }
                })
                .collect(Collectors.joining(","));

        return "[" + content + "]";
    }

}
