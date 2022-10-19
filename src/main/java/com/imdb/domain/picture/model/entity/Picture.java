package com.imdb.domain.picture.model.entity;

import com.imdb.domain.movie.model.entity.Movie;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pictures")
@NoArgsConstructor
@Getter
@Setter
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String publicId;

    @OneToOne(mappedBy = "picture", fetch = FetchType.LAZY)
    private Movie movie;

    public Picture(String url, String publicId) {
        this.url = url;
        this.publicId = publicId;
    }

}
