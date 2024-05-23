package me.flyray.bsin.gateway;

import com.google.common.collect.Maps;


import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bolei
 * @date 2023/12/26
 * @desc
 */
public class SignTest {

    public static void main(String[] args) {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(3);
        //timestamp为毫秒数的字符串形式 String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli())
        Long timestamp = System.currentTimeMillis();
        System.out.println(timestamp);
        map.put("timestamp", "1703559521500");  //值应该为毫秒数的字符串形式
        map.put("path", "/sofa-scaffold/findById");
        map.put("version", "1.0.0");
        map.put("id", "123");
        map.put("name", "bolei");

        // D7111F60958F39EF764802FDAF134A91

        List<String> storedKeys = Arrays.stream(map.keySet()
                        .toArray(new String[]{}))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        final String sign = storedKeys.stream()
                .map(key -> String.join("", key, map.get(key)))
                .collect(Collectors.joining()).trim()
                .concat("D1E5A5031B2C490B9B1D5DB95648EF61");
        System.out.println(sign);

        System.out.println(DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase());

    }

}
