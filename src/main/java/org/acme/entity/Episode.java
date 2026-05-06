package org.acme.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Episode extends BaseEntity {
    private String serverName;
    private Boolean isAi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ElementCollection
    @CollectionTable(
            name = "episode_server_data",
            joinColumns = @JoinColumn(name = "episode_id")
    )
    private List<ServerData> serverData;
}
