package com.imdb.repository;

import com.imdb.model.entity.Movie;
import com.imdb.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByMovie(Movie movie);
}
