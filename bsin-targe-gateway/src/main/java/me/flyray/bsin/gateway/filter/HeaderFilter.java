package me.flyray.bsin.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shenyu.common.utils.IpUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author HLW
 **/
@Component
@Order(-99)
@Slf4j
public class HeaderFilter implements WebFilter {
    @Override
    @SuppressWarnings("all")
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(h -> h.set("ip",IpUtils.getHost()))
                .build();
        // 创建一个新的ServerWebExchange，包含修改后的请求
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();
        return  chain.filter(mutatedExchange);
    }
}
