package com.imdb.domain.service;

import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface MovieService {

    Page<MovieDto> getAllMovies(Integer pageNo, Integer pageSize, String sortBy);

    void updateMovie(Long id, MovieUpdateDto movieUpdateDto);

    Long createMovie(MovieCreateDto movieCreateDto);

    void deleteMovie(Long id);

    List<MovieDto> getMoviesByUserEmail(String email);

    void addPictureToMovie(Long id, MultipartFile multipartFile);

    Movie getMovieById(Long id);

    double updateRating(Long movieId, RatingDto ratingDto, Principal principal);

    List<MovieDto> searchMovie(String keyword, SearchDto searchDto);
}
