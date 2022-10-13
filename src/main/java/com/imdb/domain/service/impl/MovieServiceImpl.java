package com.imdb.domain.service.impl;

import com.example.imdb.model.dto.*;
import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.mapping.MovieMapper;
import com.imdb.domain.repository.MovieRepository;
import com.example.imdb.service.*;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.*;
import com.imdb.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final ActorService actorService;
    private final GenreService genreService;
    private final PictureService pictureService;
    private final RatingService ratingService;
    private final MovieMapper movieMapper;
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<MovieDto> getAllMovies(Integer pageNo, Integer pageSize, String sortBy) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        return this.movieRepository.findAll(pageable)
                .map(movieMapper::mapMovieToMovieDto);
    }

    @Override
    public Long createMovie(MovieChangeDto movieChangeDto) {
        Movie movie = movieMapper.mapMovieChangeDtoToMovie(movieChangeDto);
        if (movie.getActors() == null) {
            movie.setActors(new ArrayList<>());
        } else {
            movie.getActors().clear();
        }

        movie.setRating(null);

        this.movieRepository.save(movie);

        addGenreToMovie(movie, movieChangeDto.getGenre());
        addActorsToMovie(movie, movieChangeDto.getActors());
        addRatingToMovie(movie, movieChangeDto.getRating());
        addRemainingFieldsToMovie(movie, movieChangeDto);

        log.info("Created new movie");
        return this.movieRepository.save(movie).getId();
    }

    @Override
    public void updateMovie(Long id, MovieChangeDto movieChangeDto) {
        Movie movie = getMovieById(id);

        addGenreToMovie(movie, movieChangeDto.getGenre());
        addActorsToMovie(movie, movieChangeDto.getActors());
        addRatingToMovie(movie, movieChangeDto.getRating());
        addRemainingFieldsToMovie(movie, movieChangeDto);

        this.movieRepository.save(movie);

        log.info("Movie with id {} is updated", movie.getId());
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = getMovieById(id);

        this.movieRepository.delete(movie);
        this.pictureService.deletePictureByUrl(movie.getPicture().getUrl());

        log.info("Deleted movie with id {}", id);
    }

    @Override
    public List<MovieDto> getMoviesByUserEmail(String email) {
        List<Movie> movies = this.movieRepository.findByOwner_Email(email)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with owner's email " + email + " is not found."));

        return movies.stream()
                .map(movieMapper::mapMovieToMovieDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addPictureToMovie(Long movieId, MultipartFile image) {
        if (image == null) {
            return;
        }

        Movie movie = getMovieById(movieId);
        movie.setPicture(pictureService.savePicture(image, movie));

        movieRepository.save(movie);

        log.info("Added picture to movie with id {}", movieId);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with id " + id + " is not found."));
    }

    @Override
    public double updateRating(Long movieId, RatingDto ratingDto) {
        log.info("Movie with id {} updated its rating");

        return ratingService.updateRating(getMovieById(movieId), ratingDto.getScour());
    }

    @Override
    public List<MovieDto> searchMovie(String keyword, SearchDto searchDto) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        Root<Movie> movieRoot = criteriaQuery.from(Movie.class);

        List<String> columns = searchDto.getColumns();
        List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like
                    (movieRoot.get(String.valueOf(columns.get(i)))
                            .as(String.class), "%" + keyword + "%")));
        }

        criteriaQuery.select(movieRoot).where(
                criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])));

        List<Movie> resultList = entityManager.createQuery(criteriaQuery).getResultList();


        return resultList.stream().map(movieMapper::mapMovieToMovieDto).collect(Collectors.toList());
    }

    private void addGenreToMovie(Movie movie, String genre) {
        if (genre != null) {
            movie.setGenre(genreService.getGenre(genre));
        } else {
            movie.setGenre(null);
        }
    }

    private void addActorsToMovie(Movie movie, List<ActorDto> actors) {
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            movie.setActors(new ArrayList<>());
            addActors(movie, actors);
        } else {
            movie.getActors().forEach(actor -> {
                actorService.removeMovieFromActor(movie, actor);
            });
            movie.getActors().clear();

            addActors(movie, actors);
        }
    }

    private void addActors(Movie movie, List<ActorDto> actors) {
        if (actors != null) {
            actors.forEach(actorDto -> {
                Actor actor = actorService.getActor(actorDto);
                movie.getActors().add(actor);

                actorService.addMovieToActor(movie, actor);
            });
        }
    }

    private void addRatingToMovie(Movie movie, RatingChangeDto ratingChangeDto) {
        if (ratingChangeDto != null) {
            movie.setRating(ratingService.createRating(ratingChangeDto, movie));
        } else {
            ratingService.removeRating(movie.getRating());
            movie.setRating(null);
        }
    }

    private void addRemainingFieldsToMovie(Movie movie, MovieChangeDto movieEditDto) {
        movie.setName(movieEditDto.getName());
        movie.setTrailerUrl(movieEditDto.getTrailerUrl());
        movie.setYear(movieEditDto.getYear());
    }
}
