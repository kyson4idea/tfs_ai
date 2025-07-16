package com.smy.tfs.framework.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.smy.tfs.SpringTestCase;
import org.junit.Test;

import javax.annotation.Resource;

public class DynamicDubboConsumerTest extends SpringTestCase {
    @Resource
    private DynamicDubboConsumer dynamicDubboConsumer;

    @Test
    public void invokeDubboServiceTest(){
        String interfaceName = "com.smy.aut.service.UsrResRlsService";
        String methodName = "getDiffUsrResRls";
        Object[] args = new Object[0];

        String version = "";
        String group = "";
        Object result = dynamicDubboConsumer.invokeDubboService(interfaceName, methodName, args, version, group);
        System.out.println("==");
    }

    @Test
    public void createInternalWorksheetTest(){
        for (int i = 0; i < 10 ; i++) {
            String interfaceName = "com.smy.ows.api.service.OwsInternalWorksheetService";
            String methodName = "createInternalWorksheet";
            String[] argTypes = new String[]{"com.smy.ows.api.domain.dto.OwsInternalWorksheetRequest"};

            JSONObject arg = new JSONObject();
            arg.put("custNo", "1");
            arg.put("custName", "1");
            arg.put("custMobile", "1");
            arg.put("custIdNo", "1");
            arg.put("content", "1");
            Object[] args = new Object[1];
            args[0] = arg;

            String version = "";
            String group = "";
            Object o = dynamicDubboConsumer.invokeDubboService(interfaceName, methodName, argTypes, args, version, group);
            System.out.println(JSONUtil.toJsonStr(o));
        }
    }
}