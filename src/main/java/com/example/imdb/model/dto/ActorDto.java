package com.example.imdb.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ActorDto {

    @NotBlank
    private String firstName;

    private String lastName;

}
