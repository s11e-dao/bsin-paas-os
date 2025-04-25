package me.flyray.bsin.utils;


import cn.hutool.extra.spring.SpringUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validator 校验框架工具
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidatorUtils {

    private static final Validator VALID = SpringUtil.getBean(Validator.class);

    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> validate = VALID.validate(object, groups);
        if (!validate.isEmpty()) {
            ConstraintViolationException exception = new ConstraintViolationException(validate);
            String message = exception.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ValidationException(message);
        }
    }

}
