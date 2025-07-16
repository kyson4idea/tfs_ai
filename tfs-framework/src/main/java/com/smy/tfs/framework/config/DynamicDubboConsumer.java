package com.smy.tfs.framework.config;

import cn.hutool.core.util.StrUtil;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicDubboConsumer {
    private final ConcurrentHashMap<String, ReferenceConfig> referenceConfigMap = new ConcurrentHashMap<>();
    public Object invokeDubboService(String interfaceName, String methodName, Object[] args, String version, String group) {
        String[] argTypes = getArgTypes(args);
        return invokeDubboService(interfaceName, methodName, argTypes, args, version, group);
    }

    public Object invokeDubboService(String interfaceName, String methodName, String[] argTypes, Object[] args,
                                     String version, String group) {
        String configKey = String.format("%s_%s_%s", interfaceName, group, version);

        ReferenceConfig<GenericService> referenceConfig;
        if ((referenceConfig = referenceConfigMap.get(configKey)) == null){
            // 创建临时的ReferenceConfig并设置属性
            referenceConfig = new ReferenceConfig<>();
            referenceConfig.setInterface(interfaceName);
            referenceConfig.setGeneric(true);

            if (StrUtil.isNotBlank(version)){
                referenceConfig.setVersion(version);
            }
            if (StrUtil.isNotBlank(group)){
                referenceConfig.setGroup(group);
            }
            referenceConfig.setTimeout(60000);
            referenceConfigMap.put(configKey, referenceConfig);
        }

        // 获取GenericService代理对象
        GenericService genericService = referenceConfig.get();

        return genericService.$invoke(methodName, argTypes, args);
    }

    public Object invokeDubboService(String interfaceName, String methodName, String version, String group) {
        String[] argTypes = new String[0];
        Object[] args = new Object[0];
        return invokeDubboService(interfaceName, methodName, argTypes, args, version, group);
    }

    private String[] getArgTypes(Object[] args) {
        if (args == null || args.length == 0) {
            return new String[0];
        }
        String[] argTypes = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass().getName();
        }
        return argTypes;
    }
}
