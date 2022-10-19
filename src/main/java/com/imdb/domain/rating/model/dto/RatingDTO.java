package com.imdb.domain.rating.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class RatingDTO {

    @Positive
    @NotNull
    @Min(1)
    @Max(10)
    private double score;

}
