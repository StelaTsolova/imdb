package com.imdb.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RatingChangeDto {

    @NotNull
    @Min(1)
    private Double score;

    @NotBlank
    private String userEmail;

    public String toString(){
        return "[score=" + score + " userEmail=" + userEmail + "]";
    }

}
