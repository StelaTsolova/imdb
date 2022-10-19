package com.imdb.domain.rating.service;

import com.imdb.domain.rating.model.dto.RatingChangeDTO;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.rating.model.entity.Rating;

public interface RatingService {

    void updateRating(Movie movieById, double scour, String userEmail);

    Rating createRating(RatingChangeDTO ratingChangeDto, Movie movie);

    void removeRating(Rating rating);

}
