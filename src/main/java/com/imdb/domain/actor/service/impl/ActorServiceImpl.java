package com.imdb.domain.actor.service.impl;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.actor.model.entity.Actor;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.actor.mapping.ActorMapper;
import com.imdb.domain.actor.repository.ActorRepository;
import com.imdb.domain.actor.service.ActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    @Override
    public Actor getActor(final ActorDTO actorDto) {
        final Optional<Actor> actor = actorRepository
                .findByFirstNameAndLastName(actorDto.getFirstName(), actorDto.getLastName());

        if (actor.isEmpty()) {
            return actorRepository.save(actorMapper.mapActorDtoToActor(actorDto));
        }

        return actor.get();
    }

    @Override
    public void addMovieToActor(final Movie movie, final Actor actor) {
        actor.getMovies().add(movie);

        actorRepository.save(actor);
    }

    @Override
    public void removeMovieFromActor(final Movie movie, final Actor actor) {
        actor.getMovies().remove(movie);

        actorRepository.save(actor);
    }
}
