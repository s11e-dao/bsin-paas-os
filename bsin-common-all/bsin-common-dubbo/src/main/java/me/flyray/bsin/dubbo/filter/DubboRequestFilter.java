package me.flyray.bsin.dubbo.filter;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.dubbo.enums.RequestLogEnum;
import me.flyray.bsin.dubbo.properties.DubboCustomProperties;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * dubbo日志过滤器
 *
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = Integer.MAX_VALUE)
public class DubboRequestFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        DubboCustomProperties properties = SpringUtil.getBean(DubboCustomProperties.class);
        if (properties == null || properties.getRequestLog() == null || !properties.getRequestLog()) {
            // 未开启则跳过日志逻辑
            return invoker.invoke(invocation);
        }
        String client = CommonConstants.PROVIDER;
        if (RpcContext.getServiceContext().isConsumerSide()) {
            client = CommonConstants.CONSUMER;
        }
        String baselog = "Client[" + client + "],InterfaceName=[" + invocation.getInvoker().getInterface().getSimpleName() + "],MethodName=[" + invocation.getMethodName() + "]";
        Result result;
        //如果是生产环境 则只有发生异常时 打印日志
        if (RequestLogEnum.PROD == properties.getLogLevel()) {
            // 执行接口调用逻辑
            result = invoker.invoke(invocation);
            if (result.hasException()) {
                log.error("DUBBO - 服务异常: {},Parameter={}", baselog, invocation.getArguments());
            }
        } else {
            if (properties.getLogLevel() == RequestLogEnum.INFO) {
                log.info("DUBBO - 服务调用: {}", baselog);
            } else {
                log.info("DUBBO - 服务调用: {},Parameter={}", baselog, invocation.getArguments());
            }
            long startTime = System.currentTimeMillis();
            // 执行接口调用逻辑
            result = invoker.invoke(invocation);
            // 调用耗时
            long elapsed = System.currentTimeMillis() - startTime;
            // 如果发生异常 则打印异常日志
            if (result.hasException()) {
                log.error("DUBBO - 服务异常: {}", baselog, result.getException());
            } else {
                if (properties.getLogLevel() == RequestLogEnum.INFO) {
                    log.info("DUBBO - 服务响应: {},SpendTime=[{}ms]", baselog, elapsed);
                } else if (properties.getLogLevel() == RequestLogEnum.FULL) {
                    log.info("DUBBO - 服务响应: {},SpendTime=[{}ms],Response={}", baselog, elapsed, JSON.toJSONString(new Object[]{result.getValue()}));
                }
            }
        }
        return result;
    }

}
