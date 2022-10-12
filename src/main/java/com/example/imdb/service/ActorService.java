package com.example.imdb.service;

import com.example.imdb.model.dto.ActorDto;
import com.example.imdb.model.entity.Actor;
import com.example.imdb.model.entity.Movie;

public interface ActorService {

    Actor getActor(ActorDto actorDto);

    void addMovieToActor(Movie movie, Actor actor);

    void removeMovieFromActor(Movie movie, Actor actor);
}
