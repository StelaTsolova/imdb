package com.imdb.domain.impl;

import com.imdb.domain.model.entity.Genre;
import com.imdb.domain.repository.GenreRepository;
import com.imdb.domain.service.GenreService;
import com.imdb.domain.service.impl.GenreServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
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
        Mockito.when(genreRepositoryMock.findByName(Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(genreRepositoryMock.save(Mockito.any())).thenReturn(genreTest);

        Genre genre = genreServiceTest.getGenre("");

        Assertions.assertEquals(genre.getName(), genreTest.getName());
    }

    @Test
    public void getGenreShouldReturnGenreWhenItExist() {
        Mockito.when(genreRepositoryMock.findByName(Mockito.any()))
                .thenReturn(Optional.of(genreTest));

        Genre genre = genreServiceTest.getGenre("");

        Assertions.assertEquals(genre.getName(), genreTest.getName());
    }
}