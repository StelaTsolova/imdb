package com.imdb.domain.model.entity;

import lombok.*;
import org.decimal4j.util.DoubleRounder;
import org.springframework.security.core.userdetails.User;

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

//    @Column(name = "count_scores", nullable = false)
//    private int countScores;

    @Column(nullable = false)
    private double score;

//    @Transient
//    private double averageRating;

    @ManyToOne
    private Movie movie;

    @ManyToOne
    private UserEntity userEntity;

    public Rating(double score, Movie movie, UserEntity userEntity) {
        setScore(score);
        setMovie(movie);
        setUserEntity(userEntity);
    }

//    public void increaseRating(double score) {
//        scores += score;
//        countScores++;
//
//        averageRating = DoubleRounder.round(scores / countScores, 1);
//    }
}
