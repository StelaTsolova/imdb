package com.imdb.domain.model.entity;

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

    @Column(name = "count_scores", nullable = false)
    private int countScores;

    @Column(nullable = false)
    private double scores;

    @OneToOne(mappedBy = "rating")
    private Movie movie;

    public Rating(int countScores, double scores, Movie movie) {
       setCountScores(countScores);
       setScores(scores);
       setMovie(movie);
    }

    public double getAverageRating(){
        return DoubleRounder.round(scores / countScores, 1);
    }

    public void increaseRating(double score){
        scores += score;
        countScores++;
    }
}
