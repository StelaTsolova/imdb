package com.imdb.domain.service.impl;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.actor.model.entity.Actor;
import com.imdb.domain.actor.service.impl.ActorServiceImpl;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.actor.mapping.ActorMapper;
import com.imdb.domain.actor.repository.ActorRepository;
import com.imdb.domain.actor.service.ActorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestActorService {
    public static final String ACTOR_FIRST_NAME = "Pesho";
    public static final String ACTOR_LAST_NAME = "Peshov";

    private ActorService actorServiceTest;
    private Actor actorTest;
    private Movie movieTest;

    @Mock
    private ActorRepository actorRepositoryMock;

    @Mock
    private ActorMapper actorMapperMock;

    @BeforeEach
    void init() {
        actorServiceTest = new ActorServiceImpl(actorRepositoryMock, actorMapperMock);

        actorTest = new Actor();
        actorTest.setFirstName(ACTOR_FIRST_NAME);
        actorTest.setLastName(ACTOR_LAST_NAME);
        actorTest.setMovies(new ArrayList<>());

        movieTest = new Movie();
        movieTest.setName(TestMovieService.MOVIE_NAME);
    }

    @Test
    public void getActorShouldCreateActorWhenItNotExist() {
        when(actorRepositoryMock.findByFirstNameAndLastName(any(), any()))
                .thenReturn(Optional.empty());
        when(actorRepositoryMock.save(any())).thenReturn(actorTest);

        final Actor actor = actorServiceTest.getActor(new ActorDTO());

        assertEquals(actor.getFirstName(), actorTest.getFirstName());
        assertEquals(actor.getLastName(), actorTest.getLastName());
    }

    @Test
    public void getActorShouldReturnActorWhenItExist() {
        when(actorRepositoryMock.findByFirstNameAndLastName(any(), any()))
                .thenReturn(Optional.of(actorTest));

        final Actor actor = actorServiceTest.getActor(new ActorDTO());

        assertEquals(actor.getFirstName(), actorTest.getFirstName());
        assertEquals(actor.getLastName(), actorTest.getLastName());
    }

    @Test
    public void addMovieToActor() {
        when(actorRepositoryMock.save(actorTest)).thenReturn(actorTest);

        assertEquals(actorTest.getMovies().size(), 0);

        actorServiceTest.addMovieToActor(movieTest, actorTest);

        assertEquals(actorTest.getMovies().size(), 1);
        assertEquals(actorTest.getMovies().get(0).getName(), movieTest.getName());
    }

    @Test
    public void removeMovieFromActor() {
        when(actorRepositoryMock.save(actorTest)).thenReturn(actorTest);
        actorTest.getMovies().add(movieTest);

        assertEquals(actorTest.getMovies().size(), 1);

        actorServiceTest.removeMovieFromActor(movieTest, actorTest);

        assertEquals(actorTest.getMovies().size(), 0);
    }
}