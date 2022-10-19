package com.imdb.domain.movie.model.dto;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.rating.model.dto.RatingChangeDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Setter
public class MovieCreateDTO {

    @NotBlank
    private String name;

    @Positive
    private Integer year;

    @Valid
    private List<RatingChangeDTO> rating;

    private List<ActorDTO> actors;

    private String genre;

    private String trailerUrl;

}
