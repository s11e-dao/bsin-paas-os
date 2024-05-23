package me.flyray.bsin.infrastructure.util;

import com.alibaba.fastjson.JSON;
import me.flyray.bsin.context.BaseService;

import java.util.Map;

public class BsinServiceContext {
    public static <T> T getReqBodyDto(Class<T> dtoClassType,Map<String, Object> map) {
        T obj = null;
        try {
            BaseService.validate(dtoClassType,map);
            String jsonStr = JSON.toJSONString(map);
            obj = JSON.parseObject(jsonStr, dtoClassType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static <T> T getReqBodyDtoId(Class<T> dtoClassType, Map<String, Object> map) {
        T obj = null;
        try {
            //BaseService.validateBissId(dtoClassType,map);
            String jsonStr = JSON.toJSONString(map);
            obj = JSON.parseObject(jsonStr, dtoClassType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
}
