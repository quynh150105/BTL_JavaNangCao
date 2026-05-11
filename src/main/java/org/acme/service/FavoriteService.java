package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.Exception.NotFoundException;
import org.acme.dto.response.MovieDetailResponse;
import org.acme.mapper.MovieMapper;
import org.acme.entity.*;
import org.acme.mapper.ExternalMovieMapper;
import org.acme.repository.FavoriteRepository;
import org.acme.repository.MovieRepository;
import org.acme.repository.UserRepository;
import org.acme.service.external.ExternalMovieService;
import org.acme.service.external.TaxonomyService;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class FavoriteService {

    private static final Logger LOG = Logger.getLogger(FavoriteService.class);

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private FavoriteRepository favoriteRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ExternalMovieService externalMovieService;

    @Inject
    private ExternalMovieMapper externalMovieMapper;

    @Inject
    private MovieMapper movieMapper;

    @Inject
    private TaxonomyService taxonomyService;


    @Transactional
    public void addFavorite(Long userId, String movieSlug) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        Movie movie = movieRepository.find("slug", movieSlug).firstResult();

        if (movie == null) {
            try {
                var externalDto = externalMovieService.getMovieDetail(movieSlug);

                if (externalDto == null) {
                    throw new NotFoundException("Movie not found: " + movieSlug);
                }

                List<Category> categories = taxonomyService.resolveCategories(
                        externalDto.getCategories() != null ? externalDto.getCategories() : List.of()
                );
                List<Country> countries = taxonomyService.resolveCountries(
                        externalDto.getCountries() != null ? externalDto.getCountries() : List.of()
                );

                movie = externalMovieMapper.ToEntity(externalDto, categories, countries);
                movieRepository.persist(movie);
                movieRepository.flush(); // Ensure movie is saved before creating favorite

                LOG.info("Movie created from external API: " + movieSlug + " with " + 
                        (movie.getEpisodes() != null ? movie.getEpisodes().size() : 0) + " episodes");
            } catch (Exception e) {
                LOG.error("Error fetching movie from external API: " + e.getMessage(), e);
                throw new RuntimeException("Cannot fetch movie from external API", e);
            }
        }

        boolean exists = favoriteRepository.find(
                "user = ?1 and movie = ?2", user, movie
        ).firstResultOptional().isPresent();

        if (exists) {
            throw new RuntimeException("Movie already in favorites");
        }

        Favorites favorite = Favorites.builder()
                .user(user)
                .movie(movie)
                .build();

        favoriteRepository.persist(favorite);
        LOG.info("Added movie to favorites - User: " + userId + ", Movie: " + movieSlug);
    }


    @Transactional
    public void removeFavorite(Long userId, String movieSlug) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        Movie movie = movieRepository.find("slug", movieSlug).firstResult();
        if (movie == null) {
            throw new NotFoundException("Movie not found: " + movieSlug);
        }

        Favorites favorite = favoriteRepository.find(
                "user = ?1 and movie = ?2", user, movie
        ).firstResult();

        if (favorite == null) {
            throw new NotFoundException("Favorite not found");
        }

        favoriteRepository.delete(favorite);
        LOG.info("Removed movie from favorites - User: " + userId + ", Movie: " + movieSlug);
    }


    @Transactional
    public List<MovieDetailResponse> getFavorites(Long userId, int page) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException("User not found with id: " + userId);
        }

        List<Favorites> favorites = favoriteRepository.find(
                "user = ?1", user
        ).page(page - 1, 20).list();

        return favorites.stream()
                .map(fav -> {
                    Movie movie = fav.getMovie();
                    if (movie.getCategories() != null) movie.getCategories().size();
                    if (movie.getCountries() != null) movie.getCountries().size();
                    if (movie.getEpisodes() != null) {
                        movie.getEpisodes().forEach(e -> {
                            if (e.getServerData() != null) e.getServerData().size();
                        });
                    }
                    return movieMapper.toDetail(movie);
                })
                .toList();
    }


    public boolean isFavorited(Long userId, String movieSlug) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return false;
        }

        Movie movie = movieRepository.find("slug", movieSlug).firstResult();
        if (movie == null) {
            return false;
        }

        return favoriteRepository.find(
                "user = ?1 and movie = ?2", user, movie
        ).firstResultOptional().isPresent();
    }
}
