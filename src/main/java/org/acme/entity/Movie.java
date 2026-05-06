package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="movies")
public class Movie extends BaseEntity {

    @Column(unique = true)
    private String slug;

    private String name;
    private String originName;

    @Column(length = 2000)
    private String content;

    private String thumbUrl;
    private String posterUrl;

    private String year;
    private String quality;
    private String lang;

    private String episodeCurrent;
    private String episodeTotal;

    private String type;
    private String status;

    // 🔥 ManyToMany đúng chuẩn
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_category",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_country",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private List<Country> countries;

    // 🔥 1 movie → nhiều episode
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes;
}
