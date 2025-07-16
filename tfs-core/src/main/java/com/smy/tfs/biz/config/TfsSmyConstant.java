package com.smy.tfs.biz.config;

import com.alibaba.fastjson.JSONObject;
import com.smy.scm.model.ConfigChangeEvent;
import com.smy.scm.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * 规则引擎Razor的参数配置
 *
 * @author yss
 **/
@Service
@Slf4j
@Data
public class TfsSmyConstant {
    @Value("${superAdmin}")
    String superAdmin;

    @ApolloConfigChangeListener("smy.constant")
    private void onChange(ConfigChangeEvent changeEvent) {
        try {
            log.info("接收apollo变更通知:{}", JSONObject.toJSONString(changeEvent.getChange("superAdmin")));
            superAdmin = changeEvent.getChange("superAdmin").getNewValue();
        } catch (Exception e) {
            log.error("apollo变更通知失败:{}",  e.getMessage());
        }
    }

}

