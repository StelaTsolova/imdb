package com.imdb.domain.movie.model.entity;

import com.imdb.domain.actor.model.entity.Actor;
import com.imdb.domain.genre.model.entity.Genre;
import com.imdb.domain.picture.model.entity.Picture;
import com.imdb.domain.rating.model.entity.Rating;
import com.imdb.domain.user.model.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.decimal4j.util.DoubleRounder;

import javax.persistence.*;
import java.util.Set;

@NamedEntityGraph(
        name = "get-movie-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "ratings", subgraph = "ratings-subgraph"),
                @NamedAttributeNode(value = "actors", subgraph = "actors-subgraph"),
                @NamedAttributeNode("genre"),
                @NamedAttributeNode("picture"),
        },
        subgraphs = {
                @NamedSubgraph(name = "ratings-subgraph",
                        attributeNodes = {@NamedAttributeNode("score")}),
                @NamedSubgraph(
                        name = "actors-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("firstName"),
                                @NamedAttributeNode("lastName"),
                        }
                )
        }
)
@NamedEntityGraph(
        name = "get-movie-with-owner-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "ratings", subgraph = "ratings-subgraph"),
                @NamedAttributeNode(value = "actors", subgraph = "actors-subgraph"),
                @NamedAttributeNode("genre"),
                @NamedAttributeNode("owner"),
                @NamedAttributeNode("picture"),
        },
        subgraphs = {
                @NamedSubgraph(name = "ratings-subgraph",
                        attributeNodes = {@NamedAttributeNode("score")}),
                @NamedSubgraph(
                        name = "actors-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("id"),
                                @NamedAttributeNode("firstName"),
                                @NamedAttributeNode("lastName"),
                        }
                )
        }
)
@Entity
@Table(name = "movies")
@NoArgsConstructor
@Getter
@Setter
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(nullable = false)
    private String name;

    private Integer year;

    @OneToMany(mappedBy = "movie")
    private Set<Rating> ratings;

    @ManyToMany
    private Set<Actor> actors;

    @ManyToOne(fetch = FetchType.LAZY)
    private Genre genre;

    @Column
    private String trailerUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @OneToOne(fetch = FetchType.LAZY)
    private Picture picture;

    public double getAverageRating() {
        if (ratings.isEmpty()) {
            return 0;
        }

        double scores = ratings.stream().mapToDouble(Rating::getScore).sum();
        int countScores = ratings.size();

        return DoubleRounder.round(scores / countScores, 1);
    }

}
