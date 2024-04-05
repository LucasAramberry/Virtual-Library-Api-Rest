package com.virtuallibrary.validations.annotations;

import com.virtuallibrary.validations.validators.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ValidPassword {

    String message() default "Invalid password, cannot be null and must contain between 6 and 16 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
