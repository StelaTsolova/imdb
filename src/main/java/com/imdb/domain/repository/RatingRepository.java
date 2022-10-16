package com.imdb.domain.repository;

import com.imdb.domain.model.entity.Movie;
import com.imdb.domain.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByMovieAndUserEntity_Email(Movie movie, String userEmail);
}
