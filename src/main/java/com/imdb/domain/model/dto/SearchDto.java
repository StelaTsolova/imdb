package com.imdb.domain.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchDto {

    private List<String> columns;

}
