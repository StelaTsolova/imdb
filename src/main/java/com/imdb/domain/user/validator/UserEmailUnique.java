package com.imdb.domain.user.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserEmailUniqueValidator.class)
public @interface UserEmailUnique {

    String message() default "User with this email already exist.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
