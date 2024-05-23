package me.flyray.bsin.security.filter;

import com.alibaba.fastjson.JSON;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.ext.Extension;
import com.alipay.sofa.rpc.filter.AutoActive;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HLW
 */
@Slf4j
@Extension(value = "sofaRpcRequestLogFilter", order = 99)
@AutoActive(providerSide = true)
public class SofaRpcRequestLogFilter extends Filter {


    @Override
    @SneakyThrows
    public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {
        Object[] methodArgs = cloneObjectArr(request.getMethodArgs());
        String baseLog = "AppName =[" + request.getTargetAppName() + "],interfaceName=[" + request.getInterfaceName() + "],methodName=[" + request.getMethodName() + "],version=[" + invoker.getConfig().getUniqueId() + "]";
        SofaResponse response = invoker.invoke(request);
        if (response.getAppResponse() instanceof Exception) {
            log.error("SOFA - 服务异常: {},parameter={}", baseLog, methodArgs, response.getAppResponse());
        } else {
            log.info("SOFA - 服务调用: {},parameter={}", baseLog, methodArgs);
        }
        return response;
    }


    /**
     * 深拷贝数组
     * <pre>
     *     采用深拷贝是为了防止其它的过滤器对请求参数进行修改
     * </pre>
     *
     * @param original
     * @return
     */
    @SneakyThrows
    private static Object[] cloneObjectArr(Object[] original) {
        Object[] copy = new Object[original.length];
        for (int i = 0; i < original.length; i++) {
            String jsonStr = JSON.toJSONString(original[i]);
            copy[i] = JSON.parseObject(jsonStr, original[i].getClass());
        }
        return copy;
    }
}
