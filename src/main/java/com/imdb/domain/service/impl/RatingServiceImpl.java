package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RatingChangeDto;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Rating;
import com.imdb.domain.repository.RatingRepository;
import com.imdb.domain.service.RatingService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public double updateRating(Movie movie, double scour) {
        Rating rating = ratingRepository.findByMovie(movie)
                .orElseThrow(() -> new ObjectNotFoundException("Rating of movie " + movie + " is not found."));

        rating.increaseRating(scour);
        ratingRepository.save(rating);

        log.info("Rating with id {} is updated", rating.getId());
        return rating.getAverageRating();
    }

    @Override
    public Rating createRating(RatingChangeDto ratingChangeDto, Movie movie) {
        Rating rating = new Rating(ratingChangeDto.getCountScours(), ratingChangeDto.getScours(), movie);

        log.info("Created new rating");
        return ratingRepository.save(rating);
    }

    @Override
    public void removeRating(Rating rating) {
        ratingRepository.delete(rating);

        log.info("Deleted rating with id {}", rating.getId());
    }
}
