package com.example.imdb.service;

import com.example.imdb.model.entity.Genre;
import com.example.imdb.model.entity.Movie;

public interface GenreService {

    Genre getGenre(String name);

}
