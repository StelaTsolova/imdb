package com.example.imdb.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
public class MovieChangeDto {

    @NotBlank
    private String name;

    @Positive
    private Integer year;

    @Valid
    private RatingChangeDto rating;

    private List<ActorDto> actors;

    private String genre;

    private String trailerUrl;

}
