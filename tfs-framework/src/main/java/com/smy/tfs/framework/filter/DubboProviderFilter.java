/*
package com.smy.tfs.framework.filter;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = Constants.PROVIDER)
public class DubboProviderFilter implements Filter {

    // 再 META-INFO/dubbo/org.apache.dubbo.rpc.Filter 文件中配置
    // dubboProviderFilter=com.smy.tfs.framework.filter.DubboProviderFilter 可生效
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 在方法调用之前执行的逻辑
        System.out.println("Before method call");

        // 调用原方法
        Result result = invoker.invoke(invocation);

        // 在方法调用之后执行的逻辑
        System.out.println("After method call");

        return result;
    }
}
*/
