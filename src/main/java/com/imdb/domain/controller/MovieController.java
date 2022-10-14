package com.imdb.domain.controller;

import com.imdb.domain.model.dto.*;
import com.imdb.domain.service.MovieService;
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

import static com.imdb.util.UtilClass.*;

@RestController
@RequestMapping(PATH_MOVIES)
@RequiredArgsConstructor
@Log4j2
public class MovieController {

    private final MovieService movieService;

    @GetMapping()
    public ResponseEntity<Page<MovieDto>> getMovies(
            @RequestParam(name = "pageNo", defaultValue = "0") final Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "15") final Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") final String sortBy
    ) {
        log.info("Get request on path {}: pageNo={} pageSize={} sortBy={}", PATH_MOVIES, pageNo, pageSize, sortBy);
        final Page<MovieDto> movies = movieService.getAllMovies(pageNo, pageSize, sortBy);

        log.info("Response from get request on path {}: moviesElements={} moviesPages={}", PATH_MOVIES,
                movies.getTotalElements(), movies.getTotalPages());
        return ResponseEntity.ok(movies);
    }

    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody @Valid final MovieCreateDto movieCreateDto,
                                         final BindingResult bindingResult, final UriComponentsBuilder builder) {
        log.info("Post request on path {}: name={}, year={}, rating={}, actors={}, genre={}, " +
                        "trailerUrl={}", PATH_MOVIES, movieCreateDto.getName(), movieCreateDto.getYear(),
                movieCreateDto.getRating(), getActorsToString(movieCreateDto.getActors()),
                movieCreateDto.getGenre(), movieCreateDto.getTrailerUrl());
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("Response from post request on path {}: errorMessages={}", PATH_MOVIES,
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        final Long movieId = movieService.createMovie(movieCreateDto);
        final URI location = builder.path(PATH_MOVIES + PATH_ID).buildAndExpand(movieId).toUri();

        log.info("Response from post request on path {}: status:{}, location={}", PATH_MOVIES,
                "201", location.getPath());
        return ResponseEntity.created(location).build();
    }

    @PostMapping(PATH_ID + PATH_PICTURE)
    public ResponseEntity<?> savePicture(@PathVariable(name = "id") final Long movieId,
                                         @RequestBody final MultipartFile multipartFile) {
        log.info("Post request on path {}: movieId={}, multipartFileName={}, multipartFileSize={}",
                PATH_ID + PATH_PICTURE, movieId, multipartFile.getName(), multipartFile.getSize());
        movieService.addPictureToMovie(movieId, multipartFile);

        log.info("Response from post request on path {}: status={}", PATH_ID + PATH_PICTURE, "200");
        return ResponseEntity.ok().build();
    }

    @PutMapping(PATH_ID)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> updateMovie(@PathVariable final Long id, @RequestBody @Valid final MovieUpdateDto movieUpdateDto,
                                         final BindingResult bindingResult) {
        log.info("Post request on path {}: id={}, name={}, year={}, rating={}, actors={}, genre={}, " +
                        "trailerUrl={}", id, PATH_MOVIES, movieUpdateDto.getName(), movieUpdateDto.getYear(),
                movieUpdateDto.getRating(), getActorsToString(movieUpdateDto.getActors()),
                movieUpdateDto.getGenre(), movieUpdateDto.getTrailerUrl());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("Response from put request on path {}: errorMessages={}", PATH_ID,
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        movieService.updateMovie(id, movieUpdateDto);

        log.info("Response from put request on path {}: status={}", PATH_ID, "200");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(PATH_ID)
    public ResponseEntity<?> deleteMovie(@PathVariable final Long id) {
        log.info("Delete request on path {}: movieId={}", PATH_ID, id);
        movieService.deleteMovie(id);

        log.info("Response from delete request on path {}: status={}", PATH_ID, "204");
        return ResponseEntity.noContent().build();
    }

    @GetMapping(PATH_MY)
    public ResponseEntity<List<MovieDto>> getMoviesByUser(final Principal principal) {
        final List<MovieDto> movies = movieService.getMoviesByUserEmail(principal.getName());

        log.info("Response from get request on path {}: status={}, {}", PATH_MY, "200", getListMovieDtoToString(movies));
        return ResponseEntity.ok(movies);
    }

    @PostMapping(PATH_ID)
    public ResponseEntity<?> changeRating(@PathVariable(name = "id") final Long movieId,
                                          @RequestBody final RatingDto ratingDto,
                                          BindingResult bindingResult) {
        log.info("Post request on path {}: movieId={}, score={}", PATH_ID, movieId, ratingDto.getScore());
        if (bindingResult.hasErrors()) {
            final Map<String, String> errorMessages = getErrorMessages(bindingResult.getAllErrors());
            log.info("Response from post request on path {}: errorMessages={}", PATH_ID,
                    getErrorMessagesToString(errorMessages));

            return ResponseEntity.badRequest().body(errorMessages);
        }

        final double averageRating = movieService.updateRating(movieId, ratingDto);

        log.info("Response from post request on path {}: status={}, rating={}", PATH_MY,
                "200", averageRating);
        return ResponseEntity.ok().body(Map.of("rating", averageRating));
    }

    @GetMapping(PATH_SEARCH)
    public ResponseEntity<List<MovieDto>> searchMovie(@RequestParam(name = "keyword") final String keyword,
                                                      @RequestBody final SearchDto searchDto) {
        log.info("Get request on path {}: keyword={}, score={}", PATH_SEARCH, keyword, searchDto.toString());

        final List<MovieDto> movies = movieService.searchMovie(keyword, searchDto);

        log.info("Response from get request on path {}: status={}, {}", PATH_SEARCH, "200", getListMovieDtoToString(movies));
        return ResponseEntity.ok(movies);
    }

    private String getActorsToString(final List<ActorDto> actorDtos) {
        if (actorDtos == null) {
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder().append("[");
        String result = actorDtos.stream().map(ActorDto::toString).collect(Collectors.joining(", "));
        stringBuilder.append(result);
        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    private String getListMovieDtoToString(final List<MovieDto> movies) {
        StringBuilder stringBuilder = new StringBuilder().append("{");
        String result = movies.stream().map(MovieDto::toString).collect(Collectors.joining(", "));
        stringBuilder.append(result);
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
