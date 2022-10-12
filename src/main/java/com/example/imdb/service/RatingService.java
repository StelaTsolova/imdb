package com.example.imdb.service;

import com.example.imdb.model.dto.RatingChangeDto;
import com.example.imdb.model.dto.RatingDto;
import com.example.imdb.model.entity.Movie;
import com.example.imdb.model.entity.Rating;

public interface RatingService {

    double updateRating(Movie movieById, double scour);

    Rating createRating(RatingChangeDto ratingChangeDto, Movie movie);

    void removeRating(Rating rating);
}
