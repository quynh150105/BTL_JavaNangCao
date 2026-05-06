package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entity.Favorites;

@ApplicationScoped
public class FavoriteRepository implements PanacheRepository<Favorites> {
}
