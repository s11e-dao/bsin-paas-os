package me.flyray.bsin.security.authentication;


import cn.hutool.core.bean.BeanUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.security.domain.LoginUser;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
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
    public static String createToken(LoginUser loginUser, String secreteKey,int minutes) {
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


}
