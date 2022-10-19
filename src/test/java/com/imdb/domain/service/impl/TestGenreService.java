package com.imdb.domain.service.impl;

import com.imdb.domain.genre.model.entity.Genre;
import com.imdb.domain.genre.repository.GenreRepository;
import com.imdb.domain.genre.service.GenreService;
import com.imdb.domain.genre.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestGenreService {
    public static final String GENRE_NAME = "novel";

    private GenreService genreServiceTest;
    private Genre genreTest;

    @Mock
    private GenreRepository genreRepositoryMock;

    @BeforeEach
    void init() {
        genreServiceTest = new GenreServiceImpl(genreRepositoryMock);

        genreTest = new Genre();
        genreTest.setName(GENRE_NAME);
    }

    @Test
    public void getGenreShouldCreateGenreWhenItNotExist() {
        when(genreRepositoryMock.findByName(any()))
                .thenReturn(Optional.empty());
        when(genreRepositoryMock.save(any())).thenReturn(genreTest);

        final Genre genre = genreServiceTest.getGenre("");

        assertEquals(genre.getName(), genreTest.getName());
    }

    @Test
    public void getGenreShouldReturnGenreWhenItExist() {
        when(genreRepositoryMock.findByName(any()))
                .thenReturn(Optional.of(genreTest));

        final Genre genre = genreServiceTest.getGenre("");

        assertEquals(genre.getName(), genreTest.getName());
    }
}