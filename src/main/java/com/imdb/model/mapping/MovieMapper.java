package com.imdb.model.mapping;

import com.imdb.model.dto.MovieChangeDto;
import com.imdb.model.dto.MovieDto;
import com.imdb.model.entity.Movie;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ActorMapper.class})
public interface MovieMapper {

    @Mapping(ignore = true, target = "genre")
    Movie mapMovieChangeDtoToMovie(MovieChangeDto movieChangeDto);

    @Mapping(ignore = true, target = "rating")
    @Mapping(source = "genre.name", target = "genre")
    @Mapping(source = "picture.url", target = "imgUrl")
    MovieDto mapMovieToMovieDto(Movie movie);

    @AfterMapping
    default void updateResult(@MappingTarget MovieDto movieDto, Movie movie) {
        if (movie.getRating() != null) {
            movieDto.setRating(movie.getRating().getAverageRating());
        }
    }
}
