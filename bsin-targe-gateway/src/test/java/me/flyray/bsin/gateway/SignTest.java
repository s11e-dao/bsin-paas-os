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
        // 通用参数
        map.put("timestamp", timestamp.toString());  //值应该为毫秒数的字符串形式
        map.put("path", "/appAgent/openApi/signTest");
        map.put("version", "1.0.0");
        // 业务参数 if your request body is:{"id":123,"name":"order"}
        map.put("id", "123");
        map.put("name", "bolei");

        // D7111F60958F39EF764802FDAF134A91
        // SK = 5526B0B06B9744A3B259EB4FB994BB65
        List<String> storedKeys = Arrays.stream(map.keySet()
                        .toArray(new String[]{}))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        final String todoSignStr = storedKeys.stream()
                .map(key -> String.join("", key, map.get(key)))
                .collect(Collectors.joining()).trim()
                // 拼接上SK
                .concat("5526B0B06B9744A3B259EB4FB994BB65");
        System.out.println(todoSignStr);
        String sign = DigestUtils.md5DigestAsHex(todoSignStr.getBytes()).toUpperCase();
        System.out.println(sign);

    }

}
