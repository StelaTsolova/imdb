package com.imdb.domain.service.impl;

import com.imdb.domain.model.entity.Genre;
import com.imdb.domain.repository.GenreRepository;
import com.imdb.domain.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre getGenre(final String name) {
        final Optional<Genre> genre = genreRepository.findByName(name);

        if(genre.isEmpty()){
            Genre newGenre = new Genre();
            newGenre.setName(name);

            return genreRepository.save(newGenre);
        }

        return genre.get();
    }
}
