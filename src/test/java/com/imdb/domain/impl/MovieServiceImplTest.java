package com.imdb.domain.impl;

import com.example.imdb.model.dto.*;
import com.example.imdb.model.entity.*;
import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.*;
import com.imdb.domain.model.mapping.MovieMapper;
import com.imdb.domain.repository.MovieRepository;
import com.example.imdb.service.*;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.*;
import com.imdb.domain.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.imdb.domain.impl.ActorServiceImplTest.ACTOR_FIRST_NAME;
import static com.imdb.domain.impl.RatingServiceImplTest.RATING_COUNT_SCORES;
import static com.imdb.domain.impl.RatingServiceImplTest.RATING_SCORES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {
    public static final String MOVIE_NAME = "Superman";
    public static final String MOVIE_NEW_NAME = "Batman";

    private static final Long ID = 1L;
    private static final String EMAIL = "mail.com";

    private MovieService movieServiceTest;
    private Movie movieTest;
    private MovieDto movieDtoTest;
    private MovieChangeDto movieChangeDtoTest;

    @Mock
    private MovieRepository movieRepositoryMock;
    @Mock
    private ActorService actorServiceMock;
    @Mock
    private GenreService genreServiceMock;
    @Mock
    private PictureService pictureServiceMock;
    @Mock
    private RatingService ratingServiceMock;
    @Mock
    private MovieMapper movieMapperMock;
    @Mock
    private EntityManager entityManagerMock;

    @BeforeEach
    public void init() {
        movieServiceTest = new MovieServiceImpl(movieRepositoryMock, actorServiceMock, genreServiceMock, pictureServiceMock,
                ratingServiceMock, movieMapperMock, entityManagerMock);

        movieTest = new Movie();
        movieTest.setName(MOVIE_NAME);

        movieDtoTest = new MovieDto();
        movieDtoTest.setName(movieTest.getName());

        movieChangeDtoTest = new MovieChangeDto();
        movieChangeDtoTest.setName(MOVIE_NEW_NAME);
    }

    @Test
    public void getAllMovies() {
        Pageable pageable = PageRequest.of(2, 2, Sort.by("id"));

        Mockito.when(movieRepositoryMock.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(movieTest)));
        Mockito.when(movieMapperMock.mapMovieToMovieDto(movieTest))
                .thenReturn(movieDtoTest);

        Page<MovieDto> pageMovieDto = movieServiceTest.getAllMovies(2, 2, "id");

        Assertions.assertEquals(pageMovieDto.getSize(), 1);
    }

    @Test
    public void createMovieWhenMovieChangeDtoHaveGenre() {
        Genre genre = new Genre();
        genre.setName(GenreServiceImplTest.GENRE_NAME);

        movieChangeDtoTest.setGenre(GenreServiceImplTest.GENRE_NAME);

        assertNull(movieTest.getGenre());

        Mockito.when(movieMapperMock.mapMovieChangeDtoToMovie(movieChangeDtoTest)).thenReturn(movieTest);
        Mockito.when(genreServiceMock.getGenre(GenreServiceImplTest.GENRE_NAME)).thenReturn(genre);
        Mockito.when(movieRepositoryMock.save(movieTest)).thenReturn(movieTest);

        movieServiceTest.createMovie(movieChangeDtoTest);

        assertEquals(movieTest.getGenre().getName(), GenreServiceImplTest.GENRE_NAME);
    }

    @Test
    public void updateMovieWhenMovieHaveNotActors() {
        Actor actor = new Actor();
        actor.setFirstName(ACTOR_FIRST_NAME);

        movieChangeDtoTest.setActors(List.of(new ActorDto()));

        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        Mockito.when(actorServiceMock.getActor(Mockito.any())).thenReturn(actor);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NAME);
        assertNull(movieTest.getActors());

        movieServiceTest.updateMovie(ID, movieChangeDtoTest);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        Assertions.assertEquals(movieTest.getActors().get(0).getFirstName(), ACTOR_FIRST_NAME);
    }

    @Test
    public void updateMovieWhenMovieHaveActors() {
        Actor actor = new Actor();
        actor.setFirstName(ACTOR_FIRST_NAME);
        movieTest.setActors(new ArrayList<>());
        movieTest.getActors().add(actor);

        Actor newActor = new Actor();
        newActor.setFirstName("NewName");
        movieChangeDtoTest.setActors(List.of(new ActorDto()));

        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        Mockito.when(actorServiceMock.getActor(Mockito.any())).thenReturn(newActor);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NAME);
        Assertions.assertEquals(movieTest.getActors().get(0).getFirstName(), ACTOR_FIRST_NAME);

        movieServiceTest.updateMovie(ID, movieChangeDtoTest);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        Assertions.assertEquals(movieTest.getActors().get(0).getFirstName(), "NewName");
    }

    @Test
    public void updateMovieWhenMovieHaveNotRating() {
        Rating rating = new Rating();
        rating.setCountScours(RATING_COUNT_SCORES);
        rating.setScours(RATING_SCORES);

        movieChangeDtoTest.setRating(new RatingChangeDto());

        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        Mockito.when(ratingServiceMock.createRating(Mockito.any(), Mockito.any())).thenReturn(rating);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NAME);
        assertNull(movieTest.getRating());

        movieServiceTest.updateMovie(ID, movieChangeDtoTest);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        Assertions.assertEquals(movieTest.getRating().getCountScours(), RATING_COUNT_SCORES);
        Assertions.assertEquals(movieTest.getRating().getScours(), RATING_SCORES);
    }

    @Test
    public void updateMovieWhenMovieHaveRating() {
        Rating rating = new Rating();
        rating.setCountScours(RATING_COUNT_SCORES);
        rating.setScours(RATING_SCORES);
        movieTest.setRating(rating);

        Rating newRating = new Rating();
        newRating.setCountScours(RATING_COUNT_SCORES + 1);
        newRating.setScours(RATING_SCORES + 3);
        movieChangeDtoTest.setRating(new RatingChangeDto());

        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        Mockito.when(ratingServiceMock.createRating(Mockito.any(), Mockito.any())).thenReturn(newRating);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NAME);
        Assertions.assertEquals(movieTest.getRating().getCountScours(), RATING_COUNT_SCORES);
        Assertions.assertEquals(movieTest.getRating().getScours(), RATING_SCORES);

        movieServiceTest.updateMovie(ID, movieChangeDtoTest);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        Assertions.assertEquals(movieTest.getRating().getCountScours(), RATING_COUNT_SCORES + 1);
        Assertions.assertEquals(movieTest.getRating().getScours(), RATING_SCORES + 3);
    }

    @Test
    public void updateMovieWhenMovieHaveRatingAndRatingChangeDtoIsNull() {
        Rating rating = new Rating();
        rating.setCountScours(RATING_COUNT_SCORES);
        rating.setScours(RATING_SCORES);
        movieTest.setRating(rating);

        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));

        Assertions.assertEquals(movieTest.getName(), MOVIE_NAME);
        Assertions.assertEquals(movieTest.getRating().getCountScours(), RATING_COUNT_SCORES);
        Assertions.assertEquals(movieTest.getRating().getScours(), RATING_SCORES);

        movieServiceTest.updateMovie(ID, movieChangeDtoTest);

        Assertions.assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertNull(movieTest.getRating());
    }

    @Test
    public void getMovieByIdShouldReturnMovieWhenIdExist() {
        Mockito.when(movieRepositoryMock.findById(Mockito.any())).thenReturn(Optional.of(movieTest));

        Movie movie = movieServiceTest.getMovieById(ID);

        Assertions.assertEquals(movie.getName(), movieTest.getName());
    }

    @Test
    public void getMovieByIdShouldThrowWhenIdNotExist() {
        Mockito.when(movieRepositoryMock.findById(Mockito.any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class, () -> movieServiceTest.getMovieById(ID));
    }

    @Test
    public void deleteMovie() {
        movieTest.setPicture(new Picture());
        Mockito.when(movieRepositoryMock.findById(Mockito.any())).thenReturn(Optional.of(movieTest));

        movieServiceTest.deleteMovie(ID);

        Mockito.verify(movieRepositoryMock, times(1)).delete(movieTest);
    }

    @Test
    public void getMoviesByUserEmailShouldReturnListOfMovieDtoWhenUserWithEmailExist() {
        List<Movie> movies = List.of(movieTest);
        Mockito.when(movieRepositoryMock.findByOwner_Email(EMAIL)).thenReturn(Optional.of(movies));
        Mockito.when(movieMapperMock.mapMovieToMovieDto(movieTest)).thenReturn(movieDtoTest);

        List<MovieDto> movieDtos = movieServiceTest.getMoviesByUserEmail(EMAIL);

        Assertions.assertEquals(movies.get(0).getName(), movieDtos.get(0).getName());
    }

    @Test
    public void getMoviesByUserEmailShouldThrowWhenUserWithEmailNotExist() {
        Mockito.when(movieRepositoryMock.findByOwner_Email(EMAIL)).thenReturn(Optional.empty());

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> movieServiceTest.getMoviesByUserEmail(EMAIL));
    }

    @Test
    public void addPictureToMovieWhenImageNotNull() {
        Mockito.when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        Mockito.when(pictureServiceMock.savePicture(Mockito.any(), Mockito.any())).thenReturn(new Picture());

        assertNull(movieTest.getPicture());

        movieServiceTest.addPictureToMovie(ID, Mockito.mock(MultipartFile.class));

        assertNotNull(movieTest.getPicture());
    }

    @Test
    public void updateRating() {
        Mockito.when(movieRepositoryMock.findById(Mockito.any())).thenReturn(Optional.of(movieTest));
        Mockito.when(ratingServiceMock.updateRating(movieTest, 0.0)).thenReturn(5.0);

        Assertions.assertEquals(movieServiceTest.updateRating(Mockito.any(), new RatingDto()), 5.0);
    }
}