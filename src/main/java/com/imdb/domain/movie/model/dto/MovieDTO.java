package com.imdb.domain.movie.model.dto;

import com.imdb.domain.actor.model.dto.ActorDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MovieDTO {

    private long id;
    private String name;
    private Integer year;
    private double rating;
    private List<ActorDTO> actors;
    private String genre;
    private String imgUrl;
    private String trailerUrl;

}