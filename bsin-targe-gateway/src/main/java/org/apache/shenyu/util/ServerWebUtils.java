package org.apache.shenyu.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson2.JSON;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.plugin.api.utils.BodyParamUtils;
import org.apache.shenyu.plugin.base.utils.HttpParamConverter;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerWebUtils {

    public static String getHeader(ServerHttpRequest request, String name) {
        String value = request.getHeaders().getFirst(name);
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        return urlDecode(value);
    }

    public static String getClientIP(ServerHttpRequest request, String... otherHeaderNames) {
        String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtil.isNotEmpty(otherHeaderNames)) {
            headerNames = ArrayUtil.addAll(headerNames, otherHeaderNames);
        }
        String ip;
        for (String header : headerNames) {
            ip = request.getHeaders().getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return NetUtil.getMultistageReverseProxyIp(ip);
            }
        }
        ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
        return NetUtil.getMultistageReverseProxyIp(ip);
    }


    private static String findFirstValidIp(ServerHttpRequest request, String[] headerNames) {
        for (String header : headerNames) {
            String ip = request.getHeaders().getFirst(header);
            if (!NetUtil.isUnknown(ip)) {
                return ip;
            }
        }
        return null;
    }

    public static Mono<Map<String, String>> extractParams(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = request.getHeaders().getContentType();

        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            return extractJsonParams(request);
        } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return extractFormParams(request);
        } else {
            return extractQueryParams(request);
        }
    }

    public static Mono<String> extractQueryParams(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType mediaType = request.getHeaders().getContentType();

        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            return extractJsonParam(request);
        } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            return extractFormParam(request);
        } else {
            return extractQueryParam(request);
        }
    }

    private static Mono<Map<String, String>> extractJsonParams(ServerHttpRequest request) {
        return extractBody(request)
                .map(JSON::parseObject)
                .map(jsonObject -> {
                    Map<String, String> result = new HashMap<>();
                    for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                        result.put(entry.getKey(), String.valueOf(entry.getValue()));
                    }
                    return result;
                });
    }

    private static Mono<String> extractJsonParam(ServerHttpRequest request) {
        return extractBody(request);
    }

    private static Mono<Map<String, String>> extractFormParams(ServerHttpRequest request) {
        return extractBody(request)
                .map(bodyStr -> URLDecoder.decode(bodyStr, StandardCharsets.UTF_8))
                .map(ServerWebUtils::parseQueryString);
    }

    private static Mono<String> extractFormParam(ServerHttpRequest request) {
        return extractBody(request)
                .map(param -> URLDecoder.decode(param, StandardCharsets.UTF_8))
                .map(BodyParamUtils::buildBodyParams)
                .map(linkedMultiValueMap -> HttpParamConverter.toMap(() -> linkedMultiValueMap));
    }

    private static Mono<String> extractQueryParam(ServerHttpRequest request) {
        return Mono.just(HttpParamConverter.ofString(() -> request.getURI().getQuery()));
    }

    private static Mono<Map<String, String>> extractQueryParams(ServerHttpRequest request) {
        return Mono.just(request.getQueryParams())
                .map(queryParams -> {
                    Map<String, String> result = new HashMap<>();
                    queryParams.forEach((key, values) -> result.put(key, values.isEmpty() ? "" : values.get(0)));
                    return result;
                });
    }

    private static Mono<String> extractBody(ServerHttpRequest request) {
        return DataBufferUtils.join(request.getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    System.err.println("1111"+new String(bytes, StandardCharsets.UTF_8));
                    return new String(bytes, StandardCharsets.UTF_8);
                });
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    public static String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }
}