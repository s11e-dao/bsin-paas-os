package me.flyray.bsin.dubbo.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import lombok.extern.slf4j.Slf4j;
import me.flyray.bsin.sensitive.handler.SensitiveHandler;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.Optional;

import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE;
import static org.apache.dubbo.common.constants.CommonConstants.$INVOKE_ASYNC;

/**
 * <p>
 * 针对泛化调用的响应结果进行json序列化
 * 使@JsonFormat、@JsonIgnore生效
 * ps: json序列化采用的是FastJson2，兼容Jackson大部分注解，建议还是使用FastJson2自带的注解为主
 * </p>
 *
 * @author HLW
 **/
@Slf4j
@Activate(group = CommonConstants.PROVIDER, order = -20001)
public class GenericSerializerFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        //判断是否是泛化调用
        boolean isGeneric = (invocation.getMethodName().equals($INVOKE)
            || invocation.getMethodName().equals($INVOKE_ASYNC))
            && invocation.getArguments() != null
            && invocation.getArguments().length == 3
            && !GenericService.class.isAssignableFrom(invoker.getInterface());
        Result result = invoker.invoke(invocation);
        Optional<Class<?>> clazz = Optional.ofNullable(result.getValue())
            .map(Object::getClass);
        //如果是泛化调用且响应结果不是基本数据类型则进行json序列化
        if (isGeneric && clazz.isPresent() && !ReflectUtils.isPrimitives(clazz.get())) {
            Object convert = JSON.parse(JSON.toJSONString(result.getValue(),new SensitiveHandler(), JSONWriter.Feature.WriteNulls));
            //如果是jsonObject类型则说明响应结果是对象则将类名存放，网关层需要针对某些类做特殊处理比如IPage<?>
            if (convert instanceof JSONObject) {
                ((JSONObject) convert).put("@type", clazz.get().getName());
            }
            result.setValue(convert);
        }
        return result;
    }
}
