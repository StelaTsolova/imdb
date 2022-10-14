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
public class ActorServiceImpl implements ActorService {

    private final ActorRepository actorRepository;
    private final ActorMapper actorMapper;

    @Override
    public Actor getActor(final ActorDto actorDto) {
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
