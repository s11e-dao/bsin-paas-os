package me.flyray.bsin.log.annotation;

import me.flyray.bsin.log.enums.OperateType;
import me.flyray.bsin.log.enums.OperateChannelType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author ruoyi
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    OperateType operateType() default OperateType.OTHER;

    /**
     * 操作人类别
     */
    OperateChannelType operatorType() default OperateChannelType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean isSaveResponseData() default true;


    /**
     * 排除指定的请求参数
     */
    String[] excludeParamNames() default {};

}
