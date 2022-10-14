package com.imdb.domain.model.dto;

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

    public String toString(){
        return new StringBuilder().append("[name=").append(name)
                .append(" year=").append(year)
                .append(" rating=").append(rating)
                .append(" actors={").append(actors)
                .append("} genre").append(genre)
                .append(" imgUrl").append(imgUrl)
                .append(" trailerUrl").append(trailerUrl).toString();
    }
}
