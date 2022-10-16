package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RatingChangeDto;
import com.imdb.domain.model.entity.Rating;
import com.imdb.domain.repository.RatingRepository;
import com.imdb.domain.service.RatingService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRatingService {
    public static final double RATING_SCORE = 5;

    private RatingService ratingServiceTest;
    private Rating ratingTest;

    @Mock
    private RatingRepository ratingRepositoryMock;
    @Mock
    private UserEntityService userEntityServiceMock;

    @BeforeEach
    void init() {
        ratingServiceTest = new RatingServiceImpl(ratingRepositoryMock, userEntityServiceMock);

        ratingTest = new Rating();
    }

    @Test
    public void updateRating() {
        when(ratingRepositoryMock.findByMovieAndUserEntity_Email(any(), any()))
                .thenReturn(Optional.of(ratingTest));

        assertEquals(ratingTest.getScore(), 0.0);
        ratingServiceTest.updateRating(any(), RATING_SCORE, any());

        assertEquals(ratingTest.getScore(), RATING_SCORE);
    }

    @Test
    public void updateRatingShouldThrowWhenRatingWithMovieNotExist() {
        when(ratingRepositoryMock.findByMovieAndUserEntity_Email(any(), any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> ratingServiceTest.updateRating(any(), RATING_SCORE, any()));
    }

    @Test
    public void createRating() {
        final RatingChangeDto ratingChangeDto = new RatingChangeDto();
        ratingChangeDto.setScore(RATING_SCORE);

        ratingTest.setScore(RATING_SCORE);
        when(ratingRepositoryMock.save(any())).thenReturn(ratingTest);

        final Rating rating = ratingServiceTest.createRating(ratingChangeDto, null);

        assertEquals(rating.getScore(), rating.getScore());
    }

    @Test
    public void createRatingShouldThrowWhenUserAlreadyRated() {
        when(userEntityServiceMock.hasRated(any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> ratingServiceTest.createRating(new RatingChangeDto(), null));
    }

    @Test
    public void removeRating() {
        ratingServiceTest.removeRating(ratingTest);

        verify(ratingRepositoryMock, times(1)).delete(ratingTest);
    }
}