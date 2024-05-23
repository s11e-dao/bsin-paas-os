package me.flyray.bsin.sensitive.handler;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson2.filter.ValueFilter;
import lombok.SneakyThrows;
import me.flyray.bsin.sensitive.annotation.Sensitive;
import me.flyray.bsin.sensitive.core.SensitiveStrategy;

import java.lang.reflect.Field;

/**
 * 数据脱敏json序列化工具
 */
public class SensitiveHandler implements ValueFilter {

    @Override
    @SneakyThrows
    public Object apply(Object object, String name, Object value) {
        if (!(value instanceof String)) {
            return value;
        }
        Field field = ReflectUtil.getField(object.getClass(), name);
        if (field == null) {
            return value;
        }
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null) {
            return value;
        }
        SensitiveStrategy strategy = sensitive.strategy();
        return strategy.desensitizer().apply((String) value);
    }
}
