package com.virtuallibrary.validations.validators;

import com.virtuallibrary.validations.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (!StringUtils.hasText(value) || value.contains(" ") || value.length() < 6 || value.length() > 16) {
            return false;
        }

        return true;
    }
}
