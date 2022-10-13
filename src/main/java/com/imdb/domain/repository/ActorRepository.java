package com.imdb.domain.repository;

import com.imdb.domain.model.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    Optional<Actor> findByFirstNameAndLastName(String fistName, String lastName);
}
