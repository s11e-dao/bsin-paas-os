package org.apache.shenyu.plugin.rulesEngine;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import me.flyray.bsin.dubbo.invoke.BsinServiceInvoke;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.shenyu.common.dto.RuleData;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.plugin.api.ShenyuPluginChain;
import org.apache.shenyu.plugin.base.AbstractShenyuPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RulesEnginePlugin extends AbstractShenyuPlugin {

    @Autowired
    private BsinServiceInvoke bsinServiceInvoke;

    @Override
    protected Mono<Void> doExecute(ServerWebExchange exchange, ShenyuPluginChain chain, SelectorData selector, RuleData rule) {
        if (bsinServiceInvoke == null) return responseError(exchange.getResponse(), "RERELEASESERVICE_NULL");
        String path = exchange.getRequest().getPath().toString();
        String body = exchange.getRequest().getBody().toString();
        ExecuteParams executeParams = new ExecuteParams();
        executeParams.setEventCode(path);
        executeParams.setJsonParams(JSONObject.parseObject(body));

        // 异步调用（泛化调用解耦）订单完成方法统一处理： 根据订单类型后续处理
        Map<?, ?> result = (Map<?, ?>) bsinServiceInvoke.genericInvoke("LogLoginService", "add", "dev", BeanUtil.beanToMap(executeParams));

        Object o = result.get("pass");
        if (o == null) return responseError(exchange.getResponse(), "NOT_PASS");
        if ((Boolean) o) return chain.execute(exchange);
        else return responseError(exchange.getResponse(), "NOT_PASS");
    }

    private Mono<Void> responseError(ServerHttpResponse response, String errorMessage) {
        DataBuffer dataBuffer = response.bufferFactory().wrap(errorMessage.getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String named() {
        return "rulesEngine";
    }

}
