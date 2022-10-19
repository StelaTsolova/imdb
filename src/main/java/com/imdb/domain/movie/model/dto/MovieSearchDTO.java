package com.imdb.domain.movie.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class MovieSearchDTO {

    @NotNull
    private List<String> columns;

}
