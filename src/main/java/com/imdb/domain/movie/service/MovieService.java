package com.imdb.domain.movie.service;

import com.imdb.domain.movie.model.dto.MovieCreateDTO;
import com.imdb.domain.movie.model.dto.MovieDTO;
import com.imdb.domain.movie.model.dto.MovieSearchDTO;
import com.imdb.domain.movie.model.dto.MovieUpdateDTO;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.rating.model.dto.RatingDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface MovieService {

    Page<MovieDTO> getAllMovies(int pageNo, int pageSize, String sortBy);

    void updateMovie(long id, MovieUpdateDTO movieUpdateDto);

    long createMovie(MovieCreateDTO movieCreateDto, Principal principal);

    void deleteMovie(long id);

    List<MovieDTO> getMoviesByUserEmail(String email);

    void addPictureToMovie(long id, MultipartFile multipartFile);

    Movie getMovieById(long id);

    double updateRating(long movieId, RatingDTO ratingDto, Principal principal);

    List<MovieDTO> searchMovie(String keyword, MovieSearchDTO movieSearchDTO);
}
