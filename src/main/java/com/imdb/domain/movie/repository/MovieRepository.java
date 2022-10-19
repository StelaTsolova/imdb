package com.imdb.domain.movie.repository;

import com.imdb.domain.movie.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @EntityGraph("get-movie-graph")
    Page<Movie> findAll(Pageable pageable);

    @EntityGraph("get-movie-with-owner-graph")
    Optional<List<Movie>> findByOwner_Email(String email);

}
