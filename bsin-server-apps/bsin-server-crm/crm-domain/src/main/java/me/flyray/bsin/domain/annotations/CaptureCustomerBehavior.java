package me.flyray.bsin.domain.annotations;

/**
 * @author bolei
 * @date 2023/8/21
 * @desc
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import me.flyray.bsin.domain.enums.EventCode;
import me.flyray.bsin.domain.enums.CustomerType;

/**
 * 记录操作日志注解
 *
 * @author zqf
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptureCustomerBehavior {

    /**
     * 客户类型
     * @return
     */
    CustomerType customerType() default CustomerType.ENTERPRISE;

    /**
     * 行为编号
     * @return
     */
    EventCode behaviorCode() default EventCode.ISSUE;

}
