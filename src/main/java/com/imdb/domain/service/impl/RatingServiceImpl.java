package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RatingChangeDto;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Rating;
import com.imdb.domain.model.entity.UserEntity;
import com.imdb.domain.repository.RatingRepository;
import com.imdb.domain.service.RatingService;
import com.imdb.domain.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserEntityService userEntityService;

    @Override
    public void updateRating(final Movie movie, final double score, final String userEmail) {
        Rating rating = ratingRepository.findByMovieAndUserEntity_Email(movie, userEmail).orElse(null);

        if (rating != null) {
            rating.setScore(score);
        } else {
            RatingChangeDto ratingChangeDto = new RatingChangeDto();
            ratingChangeDto.setScore(score);
            ratingChangeDto.setUserEmail(userEmail);
            rating = createRating(ratingChangeDto, movie);
        }

        ratingRepository.save(rating);
    }

    @Override
    public Rating createRating(final RatingChangeDto ratingChangeDto, final Movie movie) {
        UserEntity userEntity = userEntityService.getUserEntityByEmail(ratingChangeDto.getUserEmail());
        boolean hasRated = userEntityService.hasRated(userEntity, movie);
        if (hasRated) {
            throw new RuntimeException(String.format("User %s already rared movie %s.",
                    userEntity.getEmail(), movie.getName()));
        }

        final Rating rating = new Rating(ratingChangeDto.getScore(), movie, userEntity);

        return ratingRepository.save(rating);
    }

    @Override
    public void removeRating(final Rating rating) {
        if (rating == null) {
            return;
        }
        userEntityService.removeRating(rating.getUserEntity(), rating);

        ratingRepository.delete(rating);
    }
}
