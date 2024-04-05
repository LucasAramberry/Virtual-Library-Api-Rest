package com.virtuallibrary.validations.validators;

import com.virtuallibrary.dto.UserDto;
import com.virtuallibrary.validations.annotations.ValidPasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<ValidPasswordMatches, Object> {

    @Override
    public void initialize(ValidPasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        UserDto userDto = (UserDto) object;

        if (userDto.getPassword() == null || userDto.getMatchingPassword() == null || !userDto.getPassword().equals(userDto.getMatchingPassword())) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("matchingPassword")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
