package com.smy.tfs.api.dto.base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smy.tfs.api.enums.BizResponseEnums;
import com.smy.tfs.api.enums.FormItemCompareType;
import com.smy.tfs.common.utils.bean.ObjectHelper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class CompareInfo implements Serializable {
    private static final long serialVersionUID = 5240489790913695014L;
    private String compareId;
    private FormItemCompareType compareType;
    private Object compareValue;
    private Object compareLabel;

    @JsonCreator
    public static FormItemCompareType fromValue(String s) {
        if (ObjectHelper.isNotEmpty(s)) {
            for (FormItemCompareType f : FormItemCompareType.values()) {
                if (f.toString().equalsIgnoreCase(s)) {
                    return f;
                }
            }
        }
        throw new IllegalArgumentException("No enum constant " + FormItemCompareType.class.getName() + "." + s);
    }

    /**
     * string的字符串转换为二维的List。
     *
     * @param compareInfoTwoDString
     * @return
     */
    public static Response<List<List<CompareInfo>>> getTwoDList(String compareInfoTwoDString) {
        ObjectMapper mapper = new ObjectMapper();
        List<List<CompareInfo>> compareInfoTwoDList = new ArrayList<>();
        try {
            compareInfoTwoDList = mapper.readValue(compareInfoTwoDString, new TypeReference<List<List<CompareInfo>>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("error", e);
            return Response.error(BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("解析失败，%s", e.getMessage()));
        }
        return new Response().success(compareInfoTwoDList);
    }

    public static void main(String[] args) {
        //String itemVisibleRuleStr = "[[{\"compareId\":\"1162405080000460001\",\"compareType\":\"EQUAL\",\"compareValue\":\"女\"}],[{\"compareId\":\"1162405080000460003\",\"compareType\":\"EQUAL\",\"compareValue\":\"上海\"},{\"compareId\":\"1162405080000460001\",\"compareType\":\"EQUAL\",\"compareValue\":\"男\"},{\"compareId\":\"1162405080000460004\",\"compareType\":\"GREATER\",\"compareValue\":\"18\"}]]";
        String itemVisibleRuleStr = "[[{\"compareId\":\"1162405110000710041\",\"compareLabel\":\"compareLabel\",\"compareType\":\"CHOOSED\",\"compareValue\":[\"选项一\",\"选项二\"]}]]";

        var rep = getTwoDList(itemVisibleRuleStr);
        if (!rep.getCode().equals("200")) {
            System.out.println("error");
        }
        var compareInfoTwoDList = rep.getData();
        for (List<CompareInfo> compareInfoList : compareInfoTwoDList) {
            for (CompareInfo compareInfo : compareInfoList) {
                System.out.println(compareInfo.getCompareId());
            }
        }
        String reqStr = rep.toString();
        System.out.println(reqStr);
    }
}
