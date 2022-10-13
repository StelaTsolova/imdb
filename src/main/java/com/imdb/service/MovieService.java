package com.imdb.service;

import com.imdb.model.dto.MovieDto;
import com.imdb.model.dto.MovieChangeDto;
import com.imdb.model.dto.RatingDto;
import com.imdb.model.dto.SearchDto;
import com.imdb.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieService {

    Page<MovieDto> getAllMovies(Integer pageNo, Integer pageSize, String sortBy);

    void updateMovie(Long id, MovieChangeDto movieChangeDto);

    Long createMovie(MovieChangeDto movieChangeDto);

    void deleteMovie(Long id);

    List<MovieDto> getMoviesByUserEmail(String email);

    void addPictureToMovie(Long id, MultipartFile multipartFile);

    Movie getMovieById(Long id);

    double updateRating(Long movieId, RatingDto ratingDto);

    List<MovieDto> searchMovie(String keyword, SearchDto searchDto);
}
