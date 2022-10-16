package com.imdb.domain.service;

import com.imdb.domain.model.dto.RatingChangeDto;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Rating;

public interface RatingService {

    void updateRating(Movie movieById, double scour, String userEmail);

    Rating createRating(RatingChangeDto ratingChangeDto, Movie movie);

    void removeRating(Rating rating);

}
