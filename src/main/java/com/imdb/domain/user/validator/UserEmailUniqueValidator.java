package com.imdb.domain.user.validator;

import com.imdb.domain.user.service.UserEntityService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UserEmailUniqueValidator implements ConstraintValidator<UserEmailUnique, String> {

    private final UserEntityService userEntityService;

    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        if (email == null) {
            return true;
        }

        return this.userEntityService.doesNotExistByEmail(email);
    }
}
