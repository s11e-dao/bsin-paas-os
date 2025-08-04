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
                // sec.getUserProperties().put("authenticated", false);
                // 如果要拒绝连接，可以在这里抛出异常
                // throw new RuntimeException("未授权的WebSocket连接请求");
            }
        } catch (Exception e) {
            // 记录异常并设置认证失败
            sec.getUserProperties().put("authenticated", false);
            sec.getUserProperties().put("authError", e.getMessage());
        }

        List<String> secProtocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        response.getHeaders().put("Sec-WebSocket-Protocol", secProtocols);

        // 继续默认处理
        // 自定义握手逻辑
        super.modifyHandshake(sec, request, response);
    }

    /**
     * 从请求中提取token
     */
    private String extractTokenInfo(HandshakeRequest request) {
        // 尝试从查询参数中获取token
        List<String> secProtocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        String authorization = secProtocols.get(0);
        Map<String, List<String>> parameters = request.getParameterMap();
        if (StringUtils.isNotEmpty(authorization)) {
            return authorization;
        }
        return null;
    }


}
