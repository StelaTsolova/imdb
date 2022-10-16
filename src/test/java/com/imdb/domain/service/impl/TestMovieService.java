package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.*;
import com.imdb.domain.model.mapping.MovieMapper;
import com.imdb.domain.repository.MovieRepository;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.imdb.domain.service.impl.TestActorService.ACTOR_FIRST_NAME;
import static com.imdb.domain.service.impl.TestRatingService.RATING_SCORE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestMovieService {
    public static final String MOVIE_NAME = "Superman";
    public static final String MOVIE_NEW_NAME = "Batman";

    private static final Long ID = 1L;
    private static final String EMAIL = "mail.com";

    private MovieService movieServiceTest;
    private Movie movieTest;
    private MovieDto movieDtoTest;
    private MovieCreateDto movieCreateDtoTest;
    private MovieUpdateDto movieUpdateDtoTest;

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
    @Mock
    private Principal principalMock;

    @BeforeEach
    public void init() {
        movieServiceTest = new MovieServiceImpl(movieRepositoryMock, actorServiceMock, genreServiceMock, pictureServiceMock,
                ratingServiceMock, movieMapperMock, entityManagerMock);

        movieTest = new Movie();
        movieTest.setName(MOVIE_NAME);
        movieTest.setRatings(new ArrayList<>());
        movieTest.getRatings().add(new Rating(3, null, null));

        movieDtoTest = new MovieDto();
        movieDtoTest.setName(movieTest.getName());

        movieCreateDtoTest = new MovieCreateDto();
        movieCreateDtoTest.setName(MOVIE_NEW_NAME);

        movieUpdateDtoTest = new MovieUpdateDto();
        movieUpdateDtoTest.setName(MOVIE_NEW_NAME);
    }

    @Test
    public void getAllMovies() {
        final Pageable pageable = PageRequest.of(2, 2, Sort.by("id"));

        when(movieRepositoryMock.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(movieTest)));
        when(movieMapperMock.mapMovieToMovieDto(movieTest))
                .thenReturn(movieDtoTest);

        final Page<MovieDto> pageMovieDto = movieServiceTest.getAllMovies(2, 2, "id");

        assertEquals(pageMovieDto.getSize(), 1);
    }

    @Test
    public void createMovieWhenMovieChangeDtoHaveGenre() {
        final Genre genre = new Genre();
        genre.setName(TestGenreService.GENRE_NAME);

        movieCreateDtoTest.setGenre(TestGenreService.GENRE_NAME);

        assertNull(movieTest.getGenre());

        when(movieMapperMock.mapMovieCreateDtoToMovie(movieCreateDtoTest)).thenReturn(movieTest);
        when(genreServiceMock.getGenre(TestGenreService.GENRE_NAME)).thenReturn(genre);
        when(movieRepositoryMock.save(movieTest)).thenReturn(movieTest);

        movieServiceTest.createMovie(movieCreateDtoTest);

        assertEquals(movieTest.getGenre().getName(), TestGenreService.GENRE_NAME);
    }

    @Test
    public void updateMovieWhenMovieHaveNotActors() {
        final Actor actor = new Actor();
        actor.setFirstName(ACTOR_FIRST_NAME);

        movieUpdateDtoTest.setActors(List.of(new ActorDto()));

        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        when(actorServiceMock.getActor(any())).thenReturn(actor);

        assertEquals(movieTest.getName(), MOVIE_NAME);
        assertNull(movieTest.getActors());

        movieServiceTest.updateMovie(ID, movieUpdateDtoTest);

        assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertEquals(movieTest.getActors().get(0).getFirstName(), ACTOR_FIRST_NAME);
    }

    @Test
    public void updateMovieWhenMovieHaveActors() {
        final Actor actor = new Actor();
        actor.setFirstName(ACTOR_FIRST_NAME);
        movieTest.setActors(new ArrayList<>());
        movieTest.getActors().add(actor);

        final Actor newActor = new Actor();
        newActor.setFirstName("NewName");
        movieUpdateDtoTest.setActors(List.of(new ActorDto()));

        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        when(actorServiceMock.getActor(any())).thenReturn(newActor);

        assertEquals(movieTest.getName(), MOVIE_NAME);
        assertEquals(movieTest.getActors().get(0).getFirstName(), ACTOR_FIRST_NAME);

        movieServiceTest.updateMovie(ID, movieUpdateDtoTest);

        assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertEquals(movieTest.getActors().get(0).getFirstName(), "NewName");
    }

    @Test
    public void updateMovieWhenMovieHaveNotRating() {
        movieTest.setRatings(null);
        final Rating rating = new Rating();
        rating.setScore(RATING_SCORE);

        movieUpdateDtoTest.setRating(List.of(new RatingChangeDto()));

        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        when(ratingServiceMock.createRating(any(), any())).thenReturn(rating);

        assertEquals(movieTest.getName(), MOVIE_NAME);
        assertNull(movieTest.getRatings());

        movieServiceTest.updateMovie(ID, movieUpdateDtoTest);

        assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertEquals(movieTest.getRatings().get(0).getScore(), RATING_SCORE);
    }

    @Test
    public void updateMovieWhenMovieHaveRating() {
        final Rating rating = new Rating();
        rating.setScore(RATING_SCORE);
        movieUpdateDtoTest.setRating(List.of(new RatingChangeDto()));

        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        when(ratingServiceMock.createRating(any(), any())).thenReturn(rating);

        assertEquals(movieTest.getName(), MOVIE_NAME);
        assertEquals(movieTest.getRatings().get(0).getScore(), 3);

        movieServiceTest.updateMovie(ID, movieUpdateDtoTest);

        assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertEquals(movieTest.getRatings().get(0).getScore(), RATING_SCORE);
    }

    @Test
    public void updateMovieWhenMovieHaveRatingAndRatingChangeDtoIsNull() {
        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));

        assertEquals(movieTest.getName(), MOVIE_NAME);
        assertEquals(movieTest.getRatings().get(0).getScore(), 3);

        movieServiceTest.updateMovie(ID, movieUpdateDtoTest);

        assertEquals(movieTest.getName(), MOVIE_NEW_NAME);
        assertTrue(movieTest.getRatings().isEmpty());
    }

    @Test
    public void getMovieByIdShouldReturnMovieWhenIdExist() {
        when(movieRepositoryMock.findById(any())).thenReturn(Optional.of(movieTest));

        final Movie movie = movieServiceTest.getMovieById(ID);

        assertEquals(movie.getName(), movieTest.getName());
    }

    @Test
    public void getMovieByIdShouldThrowWhenIdNotExist() {
        when(movieRepositoryMock.findById(any())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> movieServiceTest.getMovieById(ID));
    }

    @Test
    public void deleteMovie() {
        movieTest.setPicture(new Picture());
        when(movieRepositoryMock.findById(any())).thenReturn(Optional.of(movieTest));

        movieServiceTest.deleteMovie(ID);

        verify(movieRepositoryMock, times(1)).delete(movieTest);
    }

    @Test
    public void getMoviesByUserEmailShouldReturnListOfMovieDtoWhenUserWithEmailExist() {
        final List<Movie> movies = List.of(movieTest);
        when(movieRepositoryMock.findByOwner_Email(EMAIL)).thenReturn(Optional.of(movies));
        when(movieMapperMock.mapMovieToMovieDto(movieTest)).thenReturn(movieDtoTest);

        final List<MovieDto> movieDtos = movieServiceTest.getMoviesByUserEmail(EMAIL);

        assertEquals(movies.get(0).getName(), movieDtos.get(0).getName());
    }

    @Test
    public void getMoviesByUserEmailShouldThrowWhenUserWithEmailNotExist() {
        when(movieRepositoryMock.findByOwner_Email(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> movieServiceTest.getMoviesByUserEmail(EMAIL));
    }

    @Test
    public void addPictureToMovieWhenImageNotNull() {
        when(movieRepositoryMock.findById(ID)).thenReturn(Optional.of(movieTest));
        when(pictureServiceMock.savePicture(any(), any())).thenReturn(new Picture());

        assertNull(movieTest.getPicture());

        movieServiceTest.addPictureToMovie(ID, Mockito.mock(MultipartFile.class));

        assertNotNull(movieTest.getPicture());
    }

    @Test
    public void updateRating() {
        when(movieRepositoryMock.findById(any())).thenReturn(Optional.of(movieTest));

        assertEquals(movieServiceTest.updateRating(any(), new RatingDto(), principalMock), 3);
    }
}