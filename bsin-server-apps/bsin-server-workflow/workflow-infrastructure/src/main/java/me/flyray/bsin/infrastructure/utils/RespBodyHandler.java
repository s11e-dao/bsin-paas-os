package me.flyray.bsin.infrastructure.utils;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RespBodyHandler {


    public static Map<String, Object> RespBodyDto(){
        Map<String, Object> map = new HashMap<>();
        map.put("data","");
        return map;
    }

    public static Map<String, Object> setRespBodyDto(String jsonString) {
        Map<String, Object> map = new HashMap<>();
        if (null == jsonString) {
            return new HashMap(16);
        } else {
            map.put("data", JSONObject.parse(jsonString));
            return map;
        }
    }

    public static Map<String, Object> setRespBodyDto(Object object) {
        Map<String, Object> map = new HashMap<>();

        if (null == object) {
            map.put("data","");
            return map;
        } else {
            Map<String, Object> objectMap = new LinkedHashMap<String, Object>();
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = null;
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value == null){
                    value = "";
                }
                objectMap.put(fieldName, value);
            }
//             Map objectMap = BeanUtil.beanToMap(object, new HashMap<>(), CopyOptions.create().
//                    setIgnoreNullValue(true)
//                     .setFieldValueEditor((fieldName,fieldValue) -> {
//                 if (fieldValue == null){
//                     fieldValue = "";
//                 }
//                 return fieldValue;
//             }));
            map.put("data",objectMap);
            return map;
        }
    }


    public static Map<String, Object> setRespBodyListDto(List<?> list) {
        Map<String, Object> map = new HashMap<>();
        if(list.size() == 0){
            map.put("data","");
            return map;
        }
        map.put("data",list);
        return map;
    }



}
