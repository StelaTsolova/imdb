package com.example.imdb.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RatingChangeDto {

    @NotNull
    @Min(1)
    private Integer countScours;

    @NotNull
    @Min(1)
    private Double scours;

}
