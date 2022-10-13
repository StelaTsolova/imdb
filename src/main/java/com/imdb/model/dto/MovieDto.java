package com.imdb.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieDto {

    private String name;
    private Integer year;
    private double rating;
    private List<ActorDto> actors;
    private String genre;
    private String imgUrl;
    private String trailerUrl;
}
