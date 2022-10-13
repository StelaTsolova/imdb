package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.ActorDto;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.mapping.ActorMapper;
import com.imdb.domain.repository.ActorRepository;
import com.imdb.domain.service.ActorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ActorServiceImplTest {
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
        movieTest.setName(MovieServiceImplTest.MOVIE_NAME);
    }

    @Test
    public void getActorShouldCreateActorWhenItNotExist() {
        Mockito.when(actorRepositoryMock.findByFirstNameAndLastName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(actorRepositoryMock.save(Mockito.any())).thenReturn(actorTest);

        final Actor actor = actorServiceTest.getActor(new ActorDto());

        Assertions.assertEquals(actor.getFirstName(), actorTest.getFirstName());
        Assertions.assertEquals(actor.getLastName(), actorTest.getLastName());
    }

    @Test
    public void getActorShouldReturnActorWhenItExist() {
        Mockito.when(actorRepositoryMock.findByFirstNameAndLastName(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(actorTest));

        final Actor actor = actorServiceTest.getActor(new ActorDto());

        Assertions.assertEquals(actor.getFirstName(), actorTest.getFirstName());
        Assertions.assertEquals(actor.getLastName(), actorTest.getLastName());
    }

    @Test
    public void addMovieToActor() {
        Mockito.when(actorRepositoryMock.save(actorTest)).thenReturn(actorTest);

        Assertions.assertEquals(actorTest.getMovies().size(), 0);

        actorServiceTest.addMovieToActor(movieTest, actorTest);

        Assertions.assertEquals(actorTest.getMovies().size(), 1);
        Assertions.assertEquals(actorTest.getMovies().get(0).getName(), movieTest.getName());
    }

    @Test
    public void removeMovieFromActor() {
        Mockito.when(actorRepositoryMock.save(actorTest)).thenReturn(actorTest);
        actorTest.getMovies().add(movieTest);

        Assertions.assertEquals(actorTest.getMovies().size(), 1);

        actorServiceTest.removeMovieFromActor(movieTest, actorTest);

        Assertions.assertEquals(actorTest.getMovies().size(), 0);
    }
}