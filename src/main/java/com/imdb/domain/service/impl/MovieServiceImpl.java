package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Rating;
import com.imdb.domain.model.mapping.MovieMapper;
import com.imdb.domain.repository.MovieRepository;
import com.imdb.domain.controller.exception.ObjectNotFoundException;
import com.imdb.domain.service.*;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
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
    public Page<MovieDto> getAllMovies(final Integer pageNo, final Integer pageSize, final String sortBy) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        return movieRepository.findAll(pageable)
                .map(movieMapper::mapMovieToMovieDto);
    }

    @Override
    public Long createMovie(final MovieCreateDto movieCreateDto) {
        final Movie movie = movieMapper.mapMovieCreateDtoToMovie(movieCreateDto);
        if (movie.getActors() == null) {
            movie.setActors(new ArrayList<>());
        } else {
            movie.getActors().clear();
        }

        movieRepository.save(movie);

        addGenreToMovie(movie, movieCreateDto.getGenre());
        addActorsToMovie(movie, movieCreateDto.getActors());
        addRatingToMovie(movie, movieCreateDto.getRating());
        addRemainingFieldsToMovie(movie, movieCreateDto.getName(), movieCreateDto.getTrailerUrl(), movieCreateDto.getYear());

        return movieRepository.save(movie).getId();
    }

    @Override
    public void updateMovie(final Long id, final MovieUpdateDto movieUpdateDto) {
        final Movie movie = getMovieById(id);

        addGenreToMovie(movie, movieUpdateDto.getGenre());
        addActorsToMovie(movie, movieUpdateDto.getActors());
        addRatingToMovie(movie, movieUpdateDto.getRating());
        addRemainingFieldsToMovie(movie, movieUpdateDto.getName(), movieUpdateDto.getTrailerUrl(), movieUpdateDto.getYear());

        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(final Long id) {
        final Movie movie = getMovieById(id);

        movieRepository.delete(movie);
        pictureService.deletePictureByUrl(movie.getPicture().getUrl());
    }

    @Override
    public List<MovieDto> getMoviesByUserEmail(final String email) {
        final List<Movie> movies = movieRepository.findByOwner_Email(email)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with owner's email " + email + " is not found."));

        return movies.stream()
                .map(movieMapper::mapMovieToMovieDto)
                .collect(Collectors.toList());
    }

    @Override
    public void addPictureToMovie(final Long movieId, final MultipartFile image) {
        if (image == null) {
            return;
        }

        final Movie movie = getMovieById(movieId);
        movie.setPicture(pictureService.savePicture(image, movie));

        movieRepository.save(movie);
    }

    @Override
    public Movie getMovieById(final Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with id " + id + " is not found."));
    }

    @Override
    public double updateRating(final Long movieId, final RatingDto ratingDto, final Principal principal) {
        Movie movie = getMovieById(movieId);

        ratingService.updateRating(getMovieById(movieId), ratingDto.getScore(), principal.getName());

        return movie.getAverageRating();
    }

    @Override
    public List<MovieDto> searchMovie(final String keyword, final SearchDto searchDto) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        final Root<Movie> movieRoot = criteriaQuery.from(Movie.class);

        final List<String> columns = searchDto.getColumns();
        final List<Predicate> predicates = new ArrayList<>();
        for (String column : columns) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like
                    (movieRoot.get(String.valueOf(column))
                            .as(String.class), "%" + keyword + "%")));
        }
        criteriaQuery.select(movieRoot).where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0])));

        final List<Movie> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        return resultList.stream().map(movieMapper::mapMovieToMovieDto).collect(Collectors.toList());
    }

    private void addGenreToMovie(final Movie movie, final String genre) {
        if (genre != null) {
            movie.setGenre(genreService.getGenre(genre));
        } else {
            movie.setGenre(null);
        }
    }

    private void addActorsToMovie(final Movie movie, final List<ActorDto> actors) {
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            movie.setActors(new ArrayList<>());
        } else {
            movie.getActors().forEach(actor -> actorService.removeMovieFromActor(movie, actor));
            movie.getActors().clear();
        }

        addActors(movie, actors);
    }

    private void addActors(final Movie movie, final List<ActorDto> actors) {
        if (actors != null) {
            actors.stream().map(actorService::getActor).forEach(actor -> {
                movie.getActors().add(actor);
                actorService.addMovieToActor(movie, actor);
            });
        }
    }

    private void addRatingToMovie(final Movie movie, final List<RatingChangeDto> ratings) {
        if (movie.getRatings() == null || movie.getRatings().isEmpty()) {
            movie.setRatings(new ArrayList<>());
        } else {
            movie.getRatings().forEach(ratingService::removeRating);
            movie.getRatings().clear();
        }

        if (ratings != null) {
            ratings.stream().map(ratingChangeDto -> ratingService.createRating(ratingChangeDto, movie))
                    .forEach(rating -> movie.getRatings().add(rating));
        }
    }

    private void addRemainingFieldsToMovie(final Movie movie, final String name, final String trailerUrl, final Integer year) {
        movie.setName(name);
        movie.setTrailerUrl(trailerUrl);
        movie.setYear(year);
    }
}
