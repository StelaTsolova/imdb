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
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    @Override
    public double updateRating(final Movie movie, final double score) {
        final Rating rating = ratingRepository.findByMovie(movie)
                .orElseThrow(() -> new ObjectNotFoundException("Rating of movie " + movie + " is not found."));

        rating.increaseRating(score);
        ratingRepository.save(rating);

        return rating.getAverageRating();
    }

    @Override
    public Rating createRating(final RatingChangeDto ratingChangeDto, final Movie movie) {
        final Rating rating = new Rating(ratingChangeDto.getCountScores(), ratingChangeDto.getScores(), movie);

        return ratingRepository.save(rating);
    }

    @Override
    public void removeRating(final Rating rating) {
        if(rating == null){
            return;
        }

        ratingRepository.delete(rating);
    }
}
