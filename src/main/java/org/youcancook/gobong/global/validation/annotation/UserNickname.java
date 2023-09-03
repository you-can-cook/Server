package org.youcancook.gobong.global.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.youcancook.gobong.global.validation.validator.UserNicknameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UserNicknameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserNickname {

    String message() default "닉네임은 한글, 영어, 숫자로 10자 이내만 가능합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String pattern() default "^[0-9a-zA-Z가-힣]{1,10}$";
}