package com.example.imdb.service.impl;

import com.example.imdb.model.dto.RatingChangeDto;
import com.example.imdb.model.entity.Rating;
import com.example.imdb.repository.RatingRepository;
import com.example.imdb.service.RatingService;
import com.example.imdb.web.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RatingServiceImplTest {
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
        Mockito.when(ratingRepositoryMock.findByMovie(Mockito.any())).thenReturn(Optional.of(ratingTest));

        double averageScore = ratingServiceTest.updateRating(Mockito.any(), RATING_SCORES);

        Assertions.assertEquals(averageScore, RATING_SCORES);
    }

    @Test
    public void updateRatingShouldThrowWhenRatingWithMovieNotExist() {
        Mockito.when(ratingRepositoryMock.findByMovie(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> ratingServiceTest.updateRating(Mockito.any(), RATING_SCORES));
    }

    @Test
    public void createRating() {
        RatingChangeDto ratingChangeDto = new RatingChangeDto();
        ratingChangeDto.setCountScours(RATING_COUNT_SCORES);
        ratingChangeDto.setScours(RATING_SCORES);

        ratingTest.setCountScours(RATING_COUNT_SCORES);
        ratingTest.setScours(RATING_SCORES);
        Mockito.when(ratingRepositoryMock.save(Mockito.any())).thenReturn(ratingTest);

        Rating rating = ratingServiceTest.createRating(ratingChangeDto, null);

        Assertions.assertEquals(rating.getCountScours(), ratingTest.getCountScours());
        Assertions.assertEquals(rating.getScours(), rating.getScours());
    }

    @Test
    public void removeRating() {
        ratingServiceTest.removeRating(ratingTest);

        Mockito.verify(ratingRepositoryMock, times(1)).delete(ratingTest);
    }
}