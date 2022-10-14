package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.RatingChangeDto;
import com.imdb.domain.model.entity.Rating;
import com.imdb.domain.repository.RatingRepository;
import com.imdb.domain.service.RatingService;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
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
    public static final double RATING_SCORES = 5;
    public static final int RATING_COUNT_SCORES = 1;

    private RatingService ratingServiceTest;
    private Rating ratingTest;

    @Mock
    private RatingRepository ratingRepositoryMock;

    @BeforeEach
    void init() {
        ratingServiceTest = new RatingServiceImpl(ratingRepositoryMock);

        ratingTest = new Rating();
    }

    @Test
    public void updateRating() {
        when(ratingRepositoryMock.findByMovie(any())).thenReturn(Optional.of(ratingTest));

        final double averageScore = ratingServiceTest.updateRating(any(), RATING_SCORES);

        assertEquals(averageScore, RATING_SCORES);
    }

    @Test
    public void updateRatingShouldThrowWhenRatingWithMovieNotExist() {
        when(ratingRepositoryMock.findByMovie(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> ratingServiceTest.updateRating(any(), RATING_SCORES));
    }

    @Test
    public void createRating() {
        final RatingChangeDto ratingChangeDto = new RatingChangeDto();
        ratingChangeDto.setCountScores(RATING_COUNT_SCORES);
        ratingChangeDto.setScores(RATING_SCORES);

        ratingTest.setCountScores(RATING_COUNT_SCORES);
        ratingTest.setScores(RATING_SCORES);
        when(ratingRepositoryMock.save(any())).thenReturn(ratingTest);

        final Rating rating = ratingServiceTest.createRating(ratingChangeDto, null);

        assertEquals(rating.getCountScores(), ratingTest.getCountScores());
        assertEquals(rating.getScores(), rating.getScores());
    }

    @Test
    public void removeRating() {
        ratingServiceTest.removeRating(ratingTest);

        verify(ratingRepositoryMock, times(1)).delete(ratingTest);
    }
}