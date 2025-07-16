package com.smy.tfs.api.config;

import com.smy.tfs.api.dto.base.Response;
import com.smy.tfs.api.enums.BizResponseEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author z01140
 * @Package: com.smy.tfs.biz.bo
 * @Description: String interfaceName, String methodName, String[] argTypes, Object[] args,
 * String version, String group
 * @CreateDate 2024/4/22 17:32
 * @UpdateDate 2024/4/22 17:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DubboServiceConfig implements Serializable {

    String interfaceName;
    String methodName;
    String[] argTypes;
    Object[] args;
    String version;
    String group;
    
    public static Response<DubboServiceConfig> parseStrToDubboConfig(String dubboConfigStr) {
        //com.smy.tfs.api.service.ITicketCreateAppService?methods=createTicketAppFromTicketForm&version=&group=
        DubboServiceConfig dubboServiceConfig = new DubboServiceConfig();
        String[] configArr = dubboConfigStr.split("\\?");
        if (configArr.length != 2) {
            return new Response<>(dubboServiceConfig, BizResponseEnums.CHECK_PARAMS_EXCEPTION, String.format("参数格式不正确：%s",dubboConfigStr));
        }
        dubboServiceConfig.setInterfaceName(configArr[0]);
        String args = configArr[1];
        String[] argList = args.split("&");
        for (String arg : argList) {
            if (arg.contains("methods=")) {
                dubboServiceConfig.setMethodName(arg.replace("methods=", ""));
            }
            if (arg.contains("version=")) {
                dubboServiceConfig.setVersion(arg.replace("version=", ""));
            }
            if (arg.contains("group=")) {
                dubboServiceConfig.setGroup(arg.replace("group=", ""));
            }
        }
        return new Response<>(dubboServiceConfig, BizResponseEnums.SUCCESS, "成功");
    }
}
