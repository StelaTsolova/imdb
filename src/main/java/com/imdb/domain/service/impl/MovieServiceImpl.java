package com.imdb.domain.service.impl;

import com.imdb.domain.model.dto.*;
import com.imdb.domain.model.entity.Actor;
import com.imdb.domain.model.entity.Movie;
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
    public Page<MovieDto> getAllMovies(final Integer pageNo, final Integer pageSize, final String sortBy) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        return this.movieRepository.findAll(pageable)
                .map(movieMapper::mapMovieToMovieDto);
    }

    @Override
    public Long createMovie(final MovieChangeDto movieChangeDto) {
        final Movie movie = movieMapper.mapMovieChangeDtoToMovie(movieChangeDto);
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
    public void updateMovie(final Long id, final MovieChangeDto movieChangeDto) {
        final Movie movie = getMovieById(id);

        addGenreToMovie(movie, movieChangeDto.getGenre());
        addActorsToMovie(movie, movieChangeDto.getActors());
        addRatingToMovie(movie, movieChangeDto.getRating());
        addRemainingFieldsToMovie(movie, movieChangeDto);

        this.movieRepository.save(movie);

        log.info("Movie with id {} is updated", movie.getId());
    }

    @Override
    public void deleteMovie(final Long id) {
        final Movie movie = getMovieById(id);

        this.movieRepository.delete(movie);
        this.pictureService.deletePictureByUrl(movie.getPicture().getUrl());

        log.info("Deleted movie with id {}", id);
    }

    @Override
    public List<MovieDto> getMoviesByUserEmail(final String email) {
        final List<Movie> movies = this.movieRepository.findByOwner_Email(email)
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

        log.info("Added picture to movie with id {}", movieId);
    }

    @Override
    public Movie getMovieById(final Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with id " + id + " is not found."));
    }

    @Override
    public double updateRating(final Long movieId, final RatingDto ratingDto) {
        log.info("Movie with id {} updated its rating", movieId);

        return ratingService.updateRating(getMovieById(movieId), ratingDto.getScour());
    }

    @Override
    public List<MovieDto> searchMovie(final String keyword, final SearchDto searchDto) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        final Root<Movie> movieRoot = criteriaQuery.from(Movie.class);

        final List<String> columns = searchDto.getColumns();
        final  List<Predicate> predicates = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like
                    (movieRoot.get(String.valueOf(columns.get(i)))
                            .as(String.class), "%" + keyword + "%")));
        }
        criteriaQuery.select(movieRoot).where(
                criteriaBuilder.or(predicates.toArray(new Predicate[predicates.size()])));

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
            addActors(movie, actors);
        } else {
            movie.getActors().forEach(actor -> {
                actorService.removeMovieFromActor(movie, actor);
            });
            movie.getActors().clear();

            addActors(movie, actors);
        }
    }

    private void addActors(final Movie movie, final List<ActorDto> actors) {
        if (actors != null) {
            actors.forEach(actorDto -> {
                final Actor actor = actorService.getActor(actorDto);
                movie.getActors().add(actor);

                actorService.addMovieToActor(movie, actor);
            });
        }
    }

    private void addRatingToMovie(final Movie movie, final RatingChangeDto ratingChangeDto) {
        if (ratingChangeDto != null) {
            movie.setRating(ratingService.createRating(ratingChangeDto, movie));
        } else {
            ratingService.removeRating(movie.getRating());
            movie.setRating(null);
        }
    }

    private void addRemainingFieldsToMovie(final Movie movie, final MovieChangeDto movieEditDto) {
        movie.setName(movieEditDto.getName());
        movie.setTrailerUrl(movieEditDto.getTrailerUrl());
        movie.setYear(movieEditDto.getYear());
    }
}
