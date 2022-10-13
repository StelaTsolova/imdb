package com.imdb.service;

import com.imdb.model.dto.ActorDto;
import com.imdb.model.entity.Actor;
import com.imdb.model.entity.Movie;

public interface ActorService {

    Actor getActor(ActorDto actorDto);

    void addMovieToActor(Movie movie, Actor actor);

    void removeMovieFromActor(Movie movie, Actor actor);
}
