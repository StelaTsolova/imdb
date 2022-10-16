package com.imdb.domain.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.decimal4j.util.DoubleRounder;

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

    @OneToMany(mappedBy = "movie")
    private List<Rating> ratings;

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

    public double getAverageRating(){
        double scores =  ratings.stream().mapToDouble(Rating::getScore).sum();
        int countScores = ratings.size();

        return DoubleRounder.round(scores / countScores , 1);
    }

}
