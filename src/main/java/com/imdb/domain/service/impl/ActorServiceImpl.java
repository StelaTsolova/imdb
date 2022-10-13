package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.ActorDto;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.mapping.ActorMapper;
import com.imdb.domain.repository.ActorRepository;
import com.imdb.domain.service.ActorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    @Override
    public Actor getActor(ActorDto actorDto) {
        Optional<Actor> actor = actorRepository
                .findByFirstNameAndLastName(actorDto.getFirstName(), actorDto.getLastName());

        if (actor.isEmpty()) {
            log.info("Created new actor");
            return this.actorRepository.save(actorMapper.mapActorDtoToActor(actorDto));
        }

        return actor.get();
    }

    @Override
    public void addMovieToActor(Movie movie, Actor actor) {
        actor.getMovies().add(movie);

        actorRepository.save(actor);
        log.info("Added movie with id {} to actor with id {}", movie.getId(), actor.getId());
    }

    @Override
    public void removeMovieFromActor(Movie movie, Actor actor) {
        actor.getMovies().remove(movie);

        actorRepository.save(actor);
        log.info("Deleted movie with id {} from actor with id {}", movie.getId(), actor.getId());
    }
}
