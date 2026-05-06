package org.acme.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name="User", description = "User representation")
@Entity
@Table(name = "users")
@Builder
public class User extends BaseEntity {


    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    private String avatarUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<Favorites> favorites;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private java.util.List<WatchHistory> histories;

}
