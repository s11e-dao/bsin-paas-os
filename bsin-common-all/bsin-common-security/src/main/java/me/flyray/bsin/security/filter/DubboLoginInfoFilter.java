package me.flyray.bsin.security.filter;


import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Field;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;
import me.flyray.bsin.security.domain.LoginUser;

/**
 * auth jwt中的用户信息处理
 **/
@Slf4j
@Activate(group = {CommonConstants.PROVIDER}, order = Integer.MAX_VALUE)
public class DubboLoginInfoFilter implements Filter {


    @Override
    @SneakyThrows
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //清空内容
        LoginInfoContextHelper.remove();
        // rpc上下文内容
        Map<String, Object> attachments = RpcContext.getServiceContext().getObjectAttachments();
        // 国际化支持
        LoginInfoContextHelper.set("locale", attachments.get("locale"));
        if (attachments.size() > 0) {
            LoginUser loginUser = new LoginUser();
            for (Field field : loginUser.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (attachments.containsKey(fieldName) && !"null".equals(attachments.get(fieldName))) {
                    LoginInfoContextHelper.set(fieldName, attachments.get(fieldName));
                }
            }
        }
        return invoker.invoke(invocation);
    }
}
