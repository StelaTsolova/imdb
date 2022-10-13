package com.imdb.service.impl;

import com.imdb.model.entity.Genre;
import com.imdb.repository.GenreRepository;
import com.imdb.service.GenreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre getGenre(String name) {
        Optional<Genre> genre = genreRepository.findByName(name);

        if(genre.isEmpty()){
            Genre newGenre = new Genre();
            newGenre.setName(name);

            log.info("Created new genre");
            return genreRepository.save(newGenre);
        }

        return genre.get();
    }
}
