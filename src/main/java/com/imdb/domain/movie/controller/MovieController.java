package com.imdb.domain.movie.controller;

import com.imdb.domain.actor.model.dto.ActorDTO;
import com.imdb.domain.movie.model.dto.MovieCreateDTO;
import com.imdb.domain.movie.model.dto.MovieDTO;
import com.imdb.domain.movie.model.dto.MovieSearchDTO;
import com.imdb.domain.movie.model.dto.MovieUpdateDTO;
import com.imdb.domain.movie.service.MovieService;
import com.imdb.domain.rating.model.dto.RatingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.imdb.common.Constants.*;
import static com.imdb.common.utils.ErrorUtils.getErrorMessages;
import static com.imdb.common.utils.ErrorUtils.getErrorMessagesToString;

@RestController
@RequestMapping(PATH_MOVIES)
@RequiredArgsConstructor
@Log4j2
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> getMovies(
            @RequestParam(name = "pageNo", defaultValue = "0") final int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "15") final int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") final String sortBy
    ) {
        log.info("GET movies request: pageNo={} pageSize={} sortBy={}", pageNo, pageSize, sortBy);
        final Page<MovieDTO> movies = movieService.getAllMovies(pageNo, pageSize, sortBy);

        log.info("GET movies response: moviesElements={} moviesPages={}", movies.getTotalElements(), movies.getTotalPages());
        return ResponseEntity.ok(movies);
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@RequestBody @Valid final MovieCreateDTO movieCreateDto,
                                         final BindingResult bindingResult,
                                         final Principal principal,
                                         final UriComponentsBuilder builder) {
        log.info("POST create movie request: name={}, year={}, rating={}, actors={}, genre={},trailerUrl={}",
                movieCreateDto.getName(), movieCreateDto.getYear(),
                movieCreateDto.getRating(), getActorsToString(movieCreateDto.getActors()),
                movieCreateDto.getGenre(), movieCreateDto.getTrailerUrl());
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("POST create movie response: errorMessages={}", getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        final long movieId = movieService.createMovie(movieCreateDto, principal);
        final URI location = builder.path(PATH_MOVIES + PATH_ID).buildAndExpand(movieId).toUri();

        log.info("POST create movie: status: status={}, location={}", "201", location.getPath());
        return ResponseEntity.created(location).build();
    }

    @PostMapping(PATH_ID + PATH_PICTURE)
    public ResponseEntity<?> savePicture(@PathVariable(name = "id") final long movieId,
                                         @RequestBody final MultipartFile multipartFile) {
        log.info("POST picture request: movieId={}, multipartFileName={}, multipartFileSize={}",
                movieId, multipartFile.getName(), multipartFile.getSize());
        movieService.addPictureToMovie(movieId, multipartFile);

        log.info("POST picture response: status={}", "200");
        return ResponseEntity.ok().build();
    }

    @PutMapping(PATH_ID)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> updateMovie(@PathVariable final Long id,
                                         @RequestBody @Valid final MovieUpdateDTO movieUpdateDto,
                                         final BindingResult bindingResult) {
        log.info("POST update movie: id={}, name={}, year={}, rating={}, actors={}, " +
                        "genre={}, trailerUrl={}", id, movieUpdateDto.getName(),
                movieUpdateDto.getYear(), movieUpdateDto.getRating(),
                getActorsToString(movieUpdateDto.getActors()),
                movieUpdateDto.getGenre(), movieUpdateDto.getTrailerUrl());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("POST update movie response: errorMessages={}",
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        movieService.updateMovie(id, movieUpdateDto);

        log.info("POST update movie response: status={}", "200");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(PATH_ID)
    public ResponseEntity<?> deleteMovie(@PathVariable final long id) {
        log.info("DELETE movie request: movieId={}", id);
        movieService.deleteMovie(id);

        log.info("DELETE movie response: status={}", "204");
        return ResponseEntity.noContent().build();
    }

    @GetMapping(PATH_MY)
    public ResponseEntity<List<MovieDTO>> getMoviesByUser(final Principal principal) {
        final List<MovieDTO> movies = movieService.getMoviesByUserEmail(principal.getName());

        log.info("GET my movies response: status={}, {}", "200", getListMovieDtoToString(movies));
        return ResponseEntity.ok(movies);
    }

    @PatchMapping(PATH_MOVIE_ID)
    public ResponseEntity<?> changeRating(@PathVariable(name = "movieId") final long movieId,
                                          @RequestBody @Valid final RatingDTO ratingDto,
                                          BindingResult bindingResult,
                                          Principal principal) {
        log.info("POST rating request: movieId={}, score={}", movieId, ratingDto.getScore());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("POST rating response: errorMessages={}", getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        final double averageRating = movieService.updateRating(movieId, ratingDto, principal);

        log.info("POST rating response: status={}, rating={}", "200", averageRating);
        return ResponseEntity.ok().body(Map.of("rating", averageRating));
    }

    @GetMapping(PATH_SEARCH)
    public ResponseEntity<List<MovieDTO>> searchMovie(@RequestParam(name = "keyword") final String keyword,
                                                      @RequestBody @Valid final MovieSearchDTO movieSearchDTO) {
        log.info("GET search movies request: keyword={}, score={}", keyword, movieSearchDTO.toString());

        final List<MovieDTO> movies = movieService.searchMovie(keyword, movieSearchDTO);

        log.info("GET search movies response: status={}, {}", "200", getListMovieDtoToString(movies));
        return ResponseEntity.ok(movies);
    }

    private String getActorsToString(final List<ActorDTO> actorDtos) {
        if (actorDtos == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder().append("[");
        String result = actorDtos.stream().map(ActorDTO::toString).collect(Collectors.joining(", "));
        stringBuilder.append(result);
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    private String getListMovieDtoToString(final List<MovieDTO> movies) {
        StringBuilder stringBuilder = new StringBuilder().append("{");
        String result = movies.stream().map(MovieDTO::toString).collect(Collectors.joining(", "));
        stringBuilder.append(result);
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
