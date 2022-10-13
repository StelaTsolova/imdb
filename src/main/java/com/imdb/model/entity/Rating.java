package com.imdb.model.entity;

import lombok.*;
import org.decimal4j.util.DoubleRounder;

import javax.persistence.*;

@Entity
@Table(name = "ratings")
@NoArgsConstructor
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column(name = "count_scours", nullable = false)
    private int countScours;

    @Column(nullable = false)
    private double scours;

    @OneToOne(mappedBy = "rating")
    private Movie movie;

    public Rating(int countScours, double scours, Movie movie) {
       setCountScours(countScours);
       setScours(scours);
       setMovie(movie);
    }

    public double getAverageRating(){
        return DoubleRounder.round(scours / countScours, 1);
    }

    public void increaseRating(double scour){
        scours += scour;
        countScours++;
    }
}
