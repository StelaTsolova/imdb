package com.imdb.domain.movie.mapping;

import com.imdb.domain.actor.mapping.ActorMapper;
import com.imdb.domain.movie.model.dto.MovieCreateDTO;
import com.imdb.domain.movie.model.dto.MovieDTO;
import com.imdb.domain.movie.model.entity.Movie;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ActorMapper.class})
public interface MovieMapper {

    @Mapping(ignore = true, target = "genre")
    Movie mapMovieCreateDtoToMovie(MovieCreateDTO movieCreateDto);

    @Mapping(source = "genre.name", target = "genre")
    @Mapping(source = "picture.url", target = "imgUrl")
    MovieDTO mapMovieToMovieDto(Movie movie);


}
