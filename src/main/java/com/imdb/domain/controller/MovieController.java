package com.imdb.domain.controller;

import com.imdb.domain.model.dto.MovieDto;
import com.imdb.domain.model.dto.MovieChangeDto;
import com.imdb.domain.model.dto.RatingDto;
import com.imdb.domain.model.dto.SearchDto;
import com.imdb.domain.service.MovieService;
import com.imdb.util.UtilClass;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping(UtilClass.PATH_MOVIES)
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping()
    public ResponseEntity<Page<MovieDto>> getMovies(
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(movieService.getAllMovies(pageNo, pageSize, sortBy));
    }

    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody @Valid MovieChangeDto movieChangeDto,
                                         BindingResult bindingResult, UriComponentsBuilder builder) {
        if (bindingResult.hasErrors()) {
            System.out.println("binding");
            return ResponseEntity.badRequest().body(UtilClass.getErrorMessages(bindingResult.getAllErrors()));
        }

        Long movieId = movieService.createMovie(movieChangeDto);
        URI location = builder.path("/movie/{id}").buildAndExpand(movieId).toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping(UtilClass.PATH_ID + UtilClass.PATH_PICTURE)
    public ResponseEntity<?> savePicture(@PathVariable(name = "id") Long movieId,
                                         @RequestBody MultipartFile multipartFile) {
        movieService.addPictureToMovie(movieId, multipartFile);

        return ResponseEntity.ok().build();
    }

    @PutMapping(UtilClass.PATH_ID)
    @PreAuthorize(value = "ADMIN")
    public ResponseEntity<?> editMovie(@PathVariable Long id, @RequestBody @Valid MovieChangeDto movieChangeDto,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(UtilClass.getErrorMessages(bindingResult.getAllErrors()));
        }

        this.movieService.updateMovie(id, movieChangeDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(UtilClass.PATH_ID)
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        this.movieService.deleteMovie(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(UtilClass.PATH_MY)
    public ResponseEntity<List<MovieDto>> getMoviesByUser(Principal principal) {
        return ResponseEntity.ok(this.movieService.getMoviesByUserEmail(principal.getName()));
    }

    @PostMapping(UtilClass.PATH_ID)
    public ResponseEntity<?> changeRating(final @PathVariable(name = "id") Long movieId,
                                          final @RequestBody RatingDto ratingDto,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(UtilClass.getErrorMessages(bindingResult.getAllErrors()));
        }

        double averageRating = movieService.updateRating(movieId, ratingDto);

        return ResponseEntity.ok().body(Map.of("rating", averageRating));
    }

    @GetMapping(UtilClass.PATH_SEARCH)
    public ResponseEntity<List<MovieDto>> searchMovie(@RequestParam(name = "keyword") String keyword,
                                                      @RequestBody SearchDto searchDto) {
        return ResponseEntity.ok(this.movieService.searchMovie(keyword, searchDto));
    }
}
