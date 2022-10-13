package com.imdb.domain.model.dto;

import com.imdb.domain.model.validator.UserEmailUnique;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserEntityRegisterDto {

    @Email(message = "Invalid email.")
    @NotBlank
    @UserEmailUnique(message = "Email already exist.")
    private String email;

    @NotBlank
    @Size(min = 6, max = 40, message = "Password should be between 6 and 40 symbols.")
    private String password;

    private String firstName;

    private String lastName;

}
