package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.acme.entity.*;
import org.acme.mapper.ExternalMovieMapper;
import org.acme.repository.FavoriteRepository;
import org.acme.repository.MovieRepository;
import org.acme.repository.UserRepository;
import org.acme.service.external.ExternalMovieService;
import org.acme.service.external.TaxonomyService;

import java.util.List;

@ApplicationScoped
public class FavoriteService {
    @Inject
    private  MovieRepository movieRepository;

    @Inject
    private  FavoriteRepository favoriteRepository;

    @Inject
    private  UserRepository userRepository;

    @Inject
    private  ExternalMovieService externalMovieService;

    @Inject
    private  ExternalMovieMapper externalMovieMapper;

    @Inject
    private  TaxonomyService taxonomyService;


    @Transactional
    public void addFavorite(Long userId, String movieSlug){
        User user = userRepository.findById(userId);

        Movie movie = movieRepository.find("slug", movieSlug).firstResult();

        if(movie == null){
            var external = externalMovieService.getMovieDetail(movieSlug);

            List<Category> categories =
                    taxonomyService.resolveCategories(external.getCategories());
            List<Country> countries =
                    taxonomyService.resolveCountries(external.getCountries());

            movie = externalMovieMapper.ToEntity(external, categories, countries);

            movieRepository.persist(movie);
        }

        boolean exists = favoriteRepository.find(
                "user = ?1 and movie = ?2", user, movie
        ).firstResultOptional().isPresent();

        if(exists) return;

        Favorites favorite =  Favorites.builder()
                .user(user)
                .movie(movie)
                .build();
        favoriteRepository.persist(favorite);

    }

}
