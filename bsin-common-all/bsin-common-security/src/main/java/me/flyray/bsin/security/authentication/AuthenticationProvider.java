package me.flyray.bsin.security.authentication;


import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.domain.LoginUser;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bolei
 * @date 2023/9/22
 * @desc
 */
public class AuthenticationProvider {

    /**
     * 生成token
     *
     * @param secreteKey
     * @return
     */
    public static String createToken(LoginUser loginUser, String secreteKey, int minutes) {
        return createToken(BeanUtil.beanToMap(loginUser), secreteKey, Duration.ofMinutes(minutes));
    }

    /**
     * 生成token
     *
     * @param secreteKey
     * @return
     */
    public static String createToken(LoginUser loginUser, String secreteKey, Duration duration) {
        return createToken(BeanUtil.beanToMap(loginUser), secreteKey, duration);
    }

    public static String createToken(Map<String, Object> payload, String secreteKey, int minutes) {
        return createToken(payload, secreteKey, Duration.ofMinutes(minutes));
    }

    public static String createToken(Map<String, Object> payload, String secreteKey, Duration duration) {
        long now = System.currentTimeMillis();
        long exp = now + now + duration.toMillis();
        Date createdDate = new Date(now);
        return Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(createdDate)
                .setExpiration(new Date(exp))
                .signWith(Keys.hmacShaKeyFor(secreteKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 校验token
     */
    public static boolean validateToken(String token, String secretKey) {
        try {
            // 获取签名密钥
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 解析 Token，验证签名、过期等信息
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            // 如果没有抛出异常，说明 Token 校验通过
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 如果验证失败，Token 无效
            return false;
        }
    }

    /**
     * 解析token
     */
    // 解析 Token 获取 Claims（载荷）
    public static Claims parseToken(String token, String secretKey) {
        try {
            // 获取签名密钥
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 解析 Token，返回 Claims
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // 如果解析失败，抛出异常
            throw new JwtException("Token 无效或解析失败");
        }
    }

    public static void main(String[] args) {
        // 密钥
        String secretKey = "mySecretKeyForJWT";

        // 生成一个包含用户信息的 Payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 12345);
        payload.put("role", "admin");

        // 创建 Token，设置过期时间为 1 小时
        String token = createToken(payload, secretKey, Duration.ofHours(1));
        System.out.println("生成的 Token: " + token);

        // 校验 Token
        boolean isValid = validateToken(token, secretKey);
        System.out.println("Token 校验结果: " + isValid);

        // 解析 Token 获取 Claims
        if (isValid) {
            Claims claims = parseToken(token, secretKey);
            System.out.println("Token 中的 Payload: " + claims);
        } else {
            System.out.println("无效的 Token");
        }
    }

}
