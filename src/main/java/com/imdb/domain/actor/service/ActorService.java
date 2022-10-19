package com.imdb.domain.actor.service;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.actor.model.entity.Actor;
import com.imdb.domain.movie.model.entity.Movie;

public interface ActorService {

    Actor getActor(ActorDTO actorDto);

    void addMovieToActor(Movie movie, Actor actor);

    void removeMovieFromActor(Movie movie, Actor actor);
}
