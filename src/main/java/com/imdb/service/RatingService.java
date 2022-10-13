package com.imdb.service;

import com.imdb.model.dto.RatingChangeDto;
import com.imdb.model.entity.Movie;
import com.imdb.model.entity.Rating;

public interface RatingService {

    double updateRating(Movie movieById, double scour);

    Rating createRating(RatingChangeDto ratingChangeDto, Movie movie);

    void removeRating(Rating rating);
}
