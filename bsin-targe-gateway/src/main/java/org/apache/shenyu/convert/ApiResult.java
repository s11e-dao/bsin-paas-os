package org.apache.shenyu.convert;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.flyray.bsin.utils.BsinResultEntity;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.shenyu.plugin.api.result.DefaultShenyuEntity;
import org.apache.shenyu.plugin.api.result.ShenyuResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author HLW
 **/
@Configuration
public class ApiResult implements ShenyuResult<DefaultShenyuEntity> {

    private static final String EMPTY_RET = "has not return value!";

    private static final String PAGE_CLASS = Page.class.getName();

    private static final String CLASS_NAME_KEY = "@type";

    private static final JSONWriter.Feature[] FEATURES = new JSONWriter.Feature[]{
            JSONWriter.Feature.WriteNulls,
            JSONWriter.Feature.WriteMapNullValue,
            JSONWriter.Feature.BrowserCompatible
    };


    @Override
    public Object format(ServerWebExchange exchange, Object origin) {
        //如果返回值是byte[]类型
        if (origin instanceof byte[]) {
            byte[] bytes = (byte[]) origin;
            return JSON.isValid(bytes) ? JSON.toJSON(BsinResultEntity.ok(JSON.parse(new String(bytes)))) : origin;
        }
        if (ReflectUtils.isPrimitive(origin.getClass())) {
            boolean isVoid = origin instanceof String && origin.toString().contains(EMPTY_RET);
            origin = BsinResultEntity.ok(isVoid ? new JSONObject() : origin);
        } else if (origin instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) origin;
            String clazz = jsonObj.getString(CLASS_NAME_KEY);
            jsonObj.remove(CLASS_NAME_KEY);
            if (PAGE_CLASS.equals(clazz)) {
                origin =BsinResultEntity.ok(jsonObj.to(Page.class));
            }else {
                origin = BsinResultEntity.ok(origin);
            }
        }else if(!(origin instanceof DefaultShenyuEntity)){
            origin = BsinResultEntity.ok(origin);
        }
        // shenyu自身报文不需要BsinResultEntity封装处理
        return JSON.toJSONString(origin, FEATURES);
    }


    @Override
    public DefaultShenyuEntity error(int code, String message, Object object) {
        return BsinResultEntity.error(code, message, object);
    }

}
