package com.imdb.domain.rating.repository;

import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.rating.model.entity.Rating;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    @EntityGraph("get-by-movie-and-userEmail-graph")
    Optional<Rating> findByMovieAndUserEntity_Email(Movie movie, String userEmail);
}
