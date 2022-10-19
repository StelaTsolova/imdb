package com.imdb.domain.rating.model.entity;

import com.imdb.domain.movie.model.entity.Movie;
import com.imdb.domain.user.model.entity.User;
import lombok.*;

import javax.persistence.*;

@NamedEntityGraph(
        name = "get-by-movie-and-userEmail-graph",
        attributeNodes = {
                @NamedAttributeNode("movie"),
                @NamedAttributeNode("userEntity")
        }
)
@Entity
@Table(name = "ratings")
@NoArgsConstructor
@Getter
@Setter
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(nullable = false)
    private double score;

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    private User userEntity;

    public Rating(double score, Movie movie, User userEntity) {
        setScore(score);
        setMovie(movie);
        setUserEntity(userEntity);
    }
}
