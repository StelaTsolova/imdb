package com.imdb.domain.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "movies")
@NoArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer year;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Rating rating;

    @ManyToMany
    private List<Actor> actors;

    @ManyToOne
    private Genre genre;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @ManyToOne
    private UserEntity owner;

    @OneToOne
    private Picture picture;

}
