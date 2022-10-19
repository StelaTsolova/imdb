package com.imdb.domain.rating.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class RatingChangeDTO {

    @NotNull
    @Min(1)
    private Double score;

    @NotBlank
    private String userEmail;

}
