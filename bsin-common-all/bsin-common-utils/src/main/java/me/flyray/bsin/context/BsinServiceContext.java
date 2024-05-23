package me.flyray.bsin.context;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;
import me.flyray.bsin.utils.ValidatorUtils;

import java.util.Map;

@Log4j2
public class BsinServiceContext {
    public static <T> T getReqBodyDto(Class<T> dtoClassType, Map<String, Object> map,Class<?> ...groups) {
        T obj = BeanUtil.copyProperties(map,dtoClassType);
        ValidatorUtils.validate(obj,groups);
        return obj;
    }

    public static <T> T bisId(Class<T> dtoClassType, Map<String, Object> map) {
        BaseService.bsinId(dtoClassType, map);
        String jsonStr = JSON.toJSONString(map);
        T obj = JSON.parseObject(jsonStr, dtoClassType);
        return obj;
    }
}
