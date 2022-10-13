package com.imdb.domain.model.validator;

import com.imdb.domain.service.UserEntityService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UserEmailUniqueValidator implements ConstraintValidator<UserEmailUnique, String> {

    private final UserEntityService userEntityService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }

        return this.userEntityService.isNotExistByEmail(email);
    }
}