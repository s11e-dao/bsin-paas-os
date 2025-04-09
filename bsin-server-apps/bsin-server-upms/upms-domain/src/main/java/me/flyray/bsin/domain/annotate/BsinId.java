package me.flyray.bsin.domain.annotate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD}) //只允许用在类的字段上
@Retention(RetentionPolicy.RUNTIME) //注解保留在程序运行期间，此时可以通过反射获得定义在某个类上的所有注解
@Constraint(validatedBy = {})
public @interface BsinId {//标识为Id
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
