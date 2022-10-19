package com.imdb.domain.actor.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ActorDTO {

    private long id;

    @NotBlank
    private String firstName;

    private String lastName;

}
