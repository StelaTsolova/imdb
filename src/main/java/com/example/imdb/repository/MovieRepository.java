package com.example.imdb.repository;

import com.example.imdb.model.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<List<Movie>> findByOwner_Email(String email);

//    @Query(value = "SELECT m FROM Movie m WHERE m = :columName")
//    Page<Movie> findMoviesByNeshto(Pageable pageable, @Param("columName") String columName);
}
