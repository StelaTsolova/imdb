package com.imdb.domain.service;

import com.imdb.domain.model.dto.ActorDto;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;

public interface ActorService {

    Actor getActor(ActorDto actorDto);

    void addMovieToActor(Movie movie, Actor actor);

    void removeMovieFromActor(Movie movie, Actor actor);
}
