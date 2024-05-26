package me.flyray.bsin.server.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：bolei
 * @date ：Created in 2022/1/14 18:12
 * @description：返回结果处理
 * @modified By：
 */

public class RespBodyHandler {

    public static Map<String, Object> RespBodyDto(){
        Map<String, Object> map = new HashMap<>();
        map.put("data","{}");
        return map;
    }

    public static Map<String, Object> setRespBodyDto(String str) {
        Map<String, Object> map = new HashMap<>();
        if (null == str) {
            return new HashMap(16);
        } else {
            map.put("data", str);
            return map;
        }
    }

    public static Map<String, Object> setRespBodyDto(Object object) {
        Map<String, Object> map = new HashMap<>();

        if (null == object) {
            map.put("data","");
            return map;
        } else if (object instanceof HashMap) {
            map.put("data", object);
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
                objectMap.put(fieldName, value);
            }
            map.put("data",objectMap);
            return map;
        }
    }


    public static Map<String, Object> setRespPageInfoBodyDto(PageInfo pageInfo) {
        Pagination pagination = new Pagination();
        pagination.setPageNum(pageInfo.getPageNum());
        pagination.setPageSize(pageInfo.getPageSize());
        pagination.setTotalSize(pageInfo.getTotal());
        pagination.setTotalPages(pageInfo.getPages());
        Map<String, Object> map = new HashMap<>();
        map.put("data",pageInfo.getList());
        map.put("pagination",pagination);
        return map;
    }

    public static Map<String, Object> setRespPageInfoBodyDto(IPage<?> pageInfo) {
        Pagination pagination = new Pagination();
        pagination.setPageNum((int) pageInfo.getCurrent());
        pagination.setPageSize((int) pageInfo.getSize());
        pagination.setTotalSize(pageInfo.getTotal());
        Map<String, Object> map = new HashMap<>();
        map.put("data", pageInfo.getRecords());
        map.put("pagination", pagination);
        return map;
    }

    public static Map<String, Object> setRespBodyListDto(List<?> list) {
        Map<String, Object> map = new HashMap<>();
//        int j=1;
//        for (int i = 0; i < list.size(); i++) {
//            String jsonStr = JSON.toJSONString(list.get(i));
//            map.put("param"+(j++),JSONObject.parseObject(jsonStr,Map.class)) ;
//        }
        map.put("data",list);
        return map;
    }


    public static Map<String, Object> setRespBodySet(Set<?> set) {
        Map<String, Object> map = new HashMap<>();
//        int j=1;
//        for (int i = 0; i < list.size(); i++) {
//            String jsonStr = JSON.toJSONString(list.get(i));
//            map.put("param"+(j++),JSONObject.parseObject(jsonStr,Map.class)) ;
//        }
        map.put("data",set);
        return map;
    }

}
