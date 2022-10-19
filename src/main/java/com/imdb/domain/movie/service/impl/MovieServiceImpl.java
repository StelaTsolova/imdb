package com.imdb.domain.movie.service.impl;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.actor.service.ActorService;
import com.imdb.domain.genre.service.GenreService;
import com.imdb.domain.movie.model.dto.MovieCreateDTO;
import com.imdb.domain.movie.model.dto.MovieDTO;
import com.imdb.domain.movie.model.dto.MovieSearchDTO;
import com.imdb.domain.movie.model.dto.MovieUpdateDTO;
import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.movie.mapping.MovieMapper;
import com.imdb.domain.movie.repository.MovieRepository;
import com.imdb.domain.picture.service.PictureService;
import com.imdb.domain.rating.model.dto.RatingChangeDTO;
import com.imdb.domain.rating.model.dto.RatingDTO;
import com.imdb.domain.rating.service.RatingService;
import com.imdb.exception.ObjectNotFoundException;
import com.imdb.domain.movie.service.MovieService;
import com.imdb.domain.user.service.UserEntityService;
import lombok.RequiredArgsConstructor;
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
import java.util.HashSet;
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
    private final UserEntityService userEntityService;
    private final MovieMapper movieMapper;
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Page<MovieDTO> getAllMovies(final int pageNo, final int pageSize, final String sortBy) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        return movieRepository.findAll(pageable)
                .map(movie -> {
                    MovieDTO movieDTO = movieMapper.mapMovieToMovieDto(movie);
                    movieDTO.setRating(movie.getAverageRating());

                    return movieDTO;
                });
    }

    @Override
    public long createMovie(final MovieCreateDTO movieCreateDto, Principal principal) {
        final Movie movie = movieMapper.mapMovieCreateDtoToMovie(movieCreateDto);
        movie.setOwner(userEntityService.getUserEntityByEmail(principal.getName()));
        if (movie.getActors() == null) {
            movie.setActors(new HashSet<>());
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
    public void updateMovie(final long id, final MovieUpdateDTO movieUpdateDto) {
        final Movie movie = getMovieById(id);

        addGenreToMovie(movie, movieUpdateDto.getGenre());
        addActorsToMovie(movie, movieUpdateDto.getActors());
        addRatingToMovie(movie, movieUpdateDto.getRating());
        addRemainingFieldsToMovie(movie, movieUpdateDto.getName(), movieUpdateDto.getTrailerUrl(), movieUpdateDto.getYear());

        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(final long id) {
        final Movie movie = getMovieById(id);

        movieRepository.delete(movie);
        pictureService.deletePictureByUrl(movie.getPicture().getUrl());
    }

    @Override
    public List<MovieDTO> getMoviesByUserEmail(final String email) {
        final List<Movie> movies = movieRepository.findByOwner_Email(email)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with owner's email " + email + " is not found."));

        return movies.stream()
                .map(movie -> {
                    MovieDTO movieDTO = movieMapper.mapMovieToMovieDto(movie);
                    movieDTO.setRating(movie.getAverageRating());

                    return movieDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addPictureToMovie(final long movieId, final MultipartFile image) {
        if (image == null) {
            return;
        }

        final Movie movie = getMovieById(movieId);
        movie.setPicture(pictureService.savePicture(image, movie));

        movieRepository.save(movie);
    }

    @Override
    public Movie getMovieById(final long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Movie with id " + id + " is not found."));
    }

    @Override
    public double updateRating(final long movieId, final RatingDTO ratingDto, final Principal principal) {
        Movie movie = getMovieById(movieId);

        ratingService.updateRating(getMovieById(movieId), ratingDto.getScore(), principal.getName());

        return movie.getAverageRating();
    }

    @Override
    public List<MovieDTO> searchMovie(final String keyword, final MovieSearchDTO movieSearchDTO) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Movie> criteriaQuery = criteriaBuilder.createQuery(Movie.class);
        final Root<Movie> movieRoot = criteriaQuery.from(Movie.class);

        final List<String> columns = movieSearchDTO.getColumns();
        final List<Predicate> predicates = new ArrayList<>();
        for (String column : columns) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like
                    (movieRoot.get(String.valueOf(column))
                            .as(String.class), "%" + keyword + "%")));
        }
        criteriaQuery.select(movieRoot).where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0])));

        final List<Movie> resultList = entityManager.createQuery(criteriaQuery).getResultList();

        return resultList.stream().map(movie -> {
            MovieDTO movieDTO = movieMapper.mapMovieToMovieDto(movie);
            movieDTO.setRating(movie.getAverageRating());

            return movieDTO;
        }).collect(Collectors.toList());
    }

    private void addGenreToMovie(final Movie movie, final String genre) {
        if (genre != null) {
            movie.setGenre(genreService.getGenre(genre));
        } else {
            movie.setGenre(null);
        }
    }

    private void addActorsToMovie(final Movie movie, final List<ActorDTO> actors) {
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            movie.setActors(new HashSet<>());
        } else {
            movie.getActors().forEach(actor -> actorService.removeMovieFromActor(movie, actor));
            movie.getActors().clear();
        }

        addActors(movie, actors);
    }

    private void addActors(final Movie movie, final List<ActorDTO> actors) {
        if (actors != null) {
            actors.stream().map(actorService::getActor).forEach(actor -> {
                movie.getActors().add(actor);
                actorService.addMovieToActor(movie, actor);
            });
        }
    }

    private void addRatingToMovie(final Movie movie, final List<RatingChangeDTO> ratings) {
        if (movie.getRatings() == null || movie.getRatings().isEmpty()) {
            movie.setRatings(new HashSet<>());
        } else {
            movie.getRatings().forEach(ratingService::removeRating);
            movie.getRatings().clear();
        }

        if (ratings != null) {
            ratings.stream().map(ratingChangeDto -> ratingService.createRating(ratingChangeDto, movie))
                    .forEach(rating -> movie.getRatings().add(rating));
        }
    }

    private void addRemainingFieldsToMovie(final Movie movie,
                                           final String name,
                                           final String trailerUrl,
                                           final Integer year) {
        movie.setName(name);
        movie.setTrailerUrl(trailerUrl);
        movie.setYear(year);
    }
}
