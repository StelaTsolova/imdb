package com.imdb.domain.repository;

import com.imdb.domain.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<List<Movie>> findByOwner_Email(String email);

}
