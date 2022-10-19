package com.imdb.domain.actor.repository;

import com.imdb.domain.actor.model.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    Optional<Actor> findByFirstNameAndLastName(String fistName, String lastName);
}
