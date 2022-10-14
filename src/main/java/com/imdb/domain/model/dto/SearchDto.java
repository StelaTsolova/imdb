package com.imdb.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchDto {

    private List<String> columns;

    public String toString(){
        return "columns=[" + String.join(", ", columns) + "]";
    }
}
