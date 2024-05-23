package me.flyray.bsin.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.rpc.context.RpcInvokeContext;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.ext.Extension;
import com.alipay.sofa.rpc.filter.AutoActive;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;

import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.contex.LoginInfoContextHelper;

/**
 * @author HLW
 **/
@Slf4j
@Extension(value = "sofaRpcHeaderFilter", order = 100)
@AutoActive(providerSide = true)
public class SofaLoginInfoFilter extends Filter {

    private static final String HEADER_KEY = "headers";

    @Override
    @SuppressWarnings("unchecked")
    public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {
        RpcInvokeContext context = RpcInvokeContext.getContext();
        try {
            Map<String, String> requestBaggage = context.getAllRequestBaggage();
            if (ObjectUtil.isNotEmpty(requestBaggage) && requestBaggage.containsKey(HEADER_KEY)) {
                JSONObject headersJson = JSONObject.parseObject(requestBaggage.get(HEADER_KEY));
                LoginInfoContextHelper.setLoginUser(headersJson);
            } else {
                Object[] requestMethodArgs = request.getMethodArgs();
                if (requestMethodArgs.length > 0) {
                    if (requestMethodArgs[0] instanceof Map) {
                        Map<String, Object> requestMap = (Map<String, Object>) requestMethodArgs[0];
                        JSONObject requestJson = new JSONObject(requestMap);
                        if (requestJson.containsKey(HEADER_KEY)) {
                            Map<String, Object> headers = requestJson.getJSONObject(HEADER_KEY);
                            if (ObjectUtil.isNotEmpty(headers)) {
                                context.putRequestBaggage(HEADER_KEY, requestJson.getJSONObject(HEADER_KEY).toJSONString());
                                LoginInfoContextHelper.setLoginUser(headers);
                            }
                            requestMap.remove(HEADER_KEY);
                        }
                    }
                }
            }
            log.info("SOFA-获取当前登录信息:{}", LoginInfoContextHelper.getJsonString());
            return invoker.invoke(request);
        } finally {
            LoginInfoContextHelper.remove();
        }
    }
}
