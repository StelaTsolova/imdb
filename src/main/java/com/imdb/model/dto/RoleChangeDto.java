package com.imdb.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class RoleChangeDto {

    @NotBlank
    @Email(message = "Invalid email.")
    private String userEmail;

    @NotBlank
    private String role;

}
