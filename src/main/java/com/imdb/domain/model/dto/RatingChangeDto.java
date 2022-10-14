package com.imdb.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RatingChangeDto {

    @NotNull
    @Min(1)
    private Integer countScores;

    @NotNull
    @Min(1)
    private Double scores;

    public String toString(){
        return "[countScores=" + countScores + " scores=" + scores + "]";
    }

}
