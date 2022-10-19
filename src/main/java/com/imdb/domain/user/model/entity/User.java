package com.imdb.domain.user.model.entity;

import com.imdb.domain.rating.model.entity.Rating;
import com.imdb.domain.user.enums.Role;
import com.imdb.domain.movie.model.entity.Movie;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @OneToMany(mappedBy = "owner")
    private Set<Movie> movies;

    @OneToMany(mappedBy = "userEntity")
    private Set<Rating> ratings;
}
