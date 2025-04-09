package me.flyray.bsin.context;

import com.alipay.common.tracer.core.context.trace.SofaTraceContext;

import java.lang.reflect.Field;
import java.util.Map;


import jakarta.validation.constraints.NotBlank;
import me.flyray.bsin.annotate.BsinId;
import me.flyray.bsin.exception.BusinessException;
import me.flyray.bsin.utils.EmptyChecker;

public class BaseService {
    /**
     * 获取并校验参数
     * @param object 校验bean对象
     * @param map 被校验的参数
     * @param <T>
     * @return
     * @throws Exception
     */
    public static  <T> Boolean validate(Class<T> object, Map<String, Object> map){
        //获取该类型声明的成员
        Field[] fields = object.getDeclaredFields();
        //遍历属性
        for (Field field : fields) {
            //对于private私有化的成员变量，通过setAccessible来修改器访问权限
            field.setAccessible(true);
            //判断加NotBlank注解的参数是否为空
            if(field.isAnnotationPresent(NotBlank.class)){
                //获取属性名称
                String name = field.getName();
                //判断map集合里是否有Key为name
                boolean b = map.containsKey(name);
                if (!b){
                    //throw new Exception("code:1111\t message:"+name+"不能为空！");
                    throw new BusinessException("1111",name+"不能为空！");
                }else {
                    Object o = map.get(name);
                    if (EmptyChecker.isEmpty(o)){
                        //throw new Exception("code:1111\tmessage:"+name+"不能为空！");
                        throw new BusinessException("1111",name+"不能为空！");
                    }
                }
            };
            //重新设置会私有权限
            field.setAccessible(false);
        }

        return true;
    }
    /**
     * 获取并校验参数
     */
    public static  <T> Boolean bsinId(Class<T> object, Map<String, Object> map){
        //获取该类型声明的成员
        Field[] fields = object.getDeclaredFields();
        //遍历属性
        for (Field field : fields) {
            //对于private私有化的成员变量，通过setAccessible来修改器访问权限
            field.setAccessible(true);
            //判断加NotBlank注解的参数是否为空
            if (field.isAnnotationPresent(BsinId.class)) {
                //获取属性名称
                String name = field.getName();
                //判断map集合里是否有Key为name
                boolean b = map.containsKey(name);
                if (!b) {
                    throw new BusinessException("1111",name+"不能为空！");
                } else {
                    Object o = map.get(name);
                    if (EmptyChecker.isEmpty(o)) {
                        throw new BusinessException("1111",name+"不能为空！");
                    }
                }
            };
            //重新设置会私有权限
            field.setAccessible(false);
        }
        return true;
    }
}
