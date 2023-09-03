package org.youcancook.gobong.global.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.youcancook.gobong.global.validation.annotation.UserNickname;

import java.util.regex.Pattern;

public class UserNicknameValidator implements ConstraintValidator<UserNickname, String> {

    private String pattern;

    @Override
    public void initialize(UserNickname constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Pattern.matches(pattern, value);
    }
}