package me.flyray.bsin.security.filter;

import cn.hutool.extra.spring.SpringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.authentication.AuthenticationProvider;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@Slf4j
public class WebsocketLoginInfoInterceptor extends ServerEndpointConfig.Configurator {

    // 实现你需要的方法覆盖
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        // 自定义逻辑
        // return true; // 允许所有来源，生产环境建议限制
        return super.checkOrigin(originHeaderValue);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        try {
            String SECRET_KEY = SpringUtil.getProperty("bsin.security.authentication-secretKey");
            // 1. 从请求头或查询参数中获取token
            String token = extractTokenInfo(request);
            log.info("websocket-登录的的token :{}", token);

            if (token != null && AuthenticationProvider.validateToken(token, SECRET_KEY)) {
                // 2. 解析token，获取用户信息
                Claims claims = AuthenticationProvider.parseToken(token, SECRET_KEY);
                log.info("websocket-获取当前登录信息claims :{}", claims);

                String tenantId = String.valueOf(claims.get("tenantId"));
                String bizRoleType = String.valueOf(claims.get("bizRoleType"));
                String bizRoleTypeNo = String.valueOf(claims.get("bizRoleTypeNo"));
                String username = String.valueOf(claims.get("username"));

                // 3. 将用户信息存储在ServerEndpointConfig的用户属性中
                sec.getUserProperties().put("bizRoleType", bizRoleType);
                sec.getUserProperties().put("username", username);
                sec.getUserProperties().put("bizRoleTypeNo", bizRoleTypeNo);
                sec.getUserProperties().put("tenantId", tenantId);
                sec.getUserProperties().put("authenticated", true);
            } else {
                // 未认证的连接，可以选择拒绝连接或标记为未认证
                sec.getUserProperties().put("authenticated", false);
                log.warn("WebSocket连接认证失败，token无效或为空");
            }
        } catch (Exception e) {
            log.error("WebSocket握手过程中发生异常", e);
            // 记录异常并设置认证失败
            sec.getUserProperties().put("authenticated", false);
            sec.getUserProperties().put("authError", e.getMessage());
        }

        // 修复核心问题：安全处理 Sec-WebSocket-Protocol 头
        try {
            List<String> secProtocols = request.getHeaders().get("Sec-WebSocket-Protocol");
            if (secProtocols != null && !secProtocols.isEmpty()) {
                // 只有当协议列表不为空时才设置响应头
                response.getHeaders().put("Sec-WebSocket-Protocol", secProtocols);
                log.debug("设置WebSocket协议: {}", secProtocols);
            } else {
                log.debug("客户端未指定WebSocket子协议");
            }
        } catch (Exception e) {
            log.error("处理WebSocket协议头时发生异常", e);
        }

        // 继续默认处理
        super.modifyHandshake(sec, request, response);
    }

    /**
     * 从请求中提取token
     * 修复空指针异常问题
     */
    private String extractTokenInfo(HandshakeRequest request) {
        try {
            // 方法1：尝试从Sec-WebSocket-Protocol头获取token
            List<String> secProtocols = request.getHeaders().get("Sec-WebSocket-Protocol");
            if (secProtocols != null && !secProtocols.isEmpty()) {
                String authorization = secProtocols.get(0);
                if (StringUtils.isNotEmpty(authorization)) {
                    log.debug("从Sec-WebSocket-Protocol获取到token");
                    return authorization;
                }
            }

            // 方法2：尝试从Authorization头获取token
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                if (StringUtils.isNotEmpty(authHeader)) {
                    // 移除 "Bearer " 前缀（如果存在）
                    if (authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
                    }
                    log.debug("从Authorization头获取到token");
                    return authHeader;
                }
            }

            // 方法3：尝试从查询参数中获取token
            Map<String, List<String>> parameters = request.getParameterMap();
            if (parameters != null) {
                List<String> tokenParams = parameters.get("token");
                if (tokenParams != null && !tokenParams.isEmpty()) {
                    String token = tokenParams.get(0);
                    if (StringUtils.isNotEmpty(token)) {
                        log.debug("从查询参数获取到token");
                        return token;
                    }
                }
            }

            log.debug("未找到有效的token");
            return null;

        } catch (Exception e) {
            log.error("提取token时发生异常", e);
            return null;
        }
    }
}