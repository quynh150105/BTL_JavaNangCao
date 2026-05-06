package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entity.Movie;

import java.util.Optional;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<Movie> {
    public Optional<Movie> findBySlug(String slug){
        return find("slug", slug).firstResultOptional();
    }
}
