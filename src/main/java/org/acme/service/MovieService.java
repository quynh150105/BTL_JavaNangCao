package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.Exception.NotFoundException;
import org.acme.dto.response.*;
import org.acme.entity.Category;
import org.acme.entity.Country;
import org.acme.entity.Movie;
import org.acme.mapper.ExternalMovieMapper;
import org.acme.repository.MovieRepository;
import org.acme.service.external.ExternalMovieService;
import org.acme.service.external.TaxonomyService;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class MovieService {

    private static final Logger LOG = Logger.getLogger(MovieService.class);

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private ExternalMovieService externalMovieService;

    @Inject
    private TaxonomyService taxonomyService;

    @Inject
    private ExternalMovieMapper externalMovieMapper;


    public List<MovieResponse> getMovies(int page) {
        List<Movie> movies = movieRepository.findAll().page(page - 1, 20).list();

        if (movies.isEmpty()) {
            return getMoviesFromExternal(page);
        }

        return movies.stream()
                .map(this::mapToMovieResponse)
                .toList();
    }


    @Transactional
    public List<MovieResponse> getMoviesFromExternal(int page) {
        try {
            var externalMovies = externalMovieService.getMovies(page);

            if (externalMovies == null || externalMovies.isEmpty()) {
                return List.of();
            }

            return externalMovies.stream()
                    .map(externalDto -> {
                        // Kiểm tra phim đã tồn tại trong DB không
                        var existingMovie = movieRepository.findBySlug(externalDto.getSlug()).orElse(null);

                        if (existingMovie != null) {
                            return mapToMovieResponse(existingMovie);
                        }

                        // Nếu chưa có, tạo mới
                        List<Category> categories = taxonomyService.resolveCategories(
                                externalDto.getCategories() != null ? externalDto.getCategories() : List.of()
                        );
                        List<Country> countries = taxonomyService.resolveCountries(
                                externalDto.getCountries() != null ? externalDto.getCountries() : List.of()
                        );

                        Movie movie = externalMovieMapper.ToEntity(externalDto, categories, countries);
                        movieRepository.persist(movie);

                        return mapToMovieResponse(movie);
                    })
                    .toList();
        } catch (Exception e) {
            LOG.error("Error fetching movies from external API: " + e.getMessage(), e);
            return List.of();
        }
    }


    @Transactional
    public MovieDetailResponse getMovieBySlug(String slug) {
        var movie = movieRepository.findBySlug(slug)
                .orElse(null);

        if (movie != null) {
            return mapToMovieDetailResponse(movie);
        }

        var externalDto = externalMovieService.getMovieDetail(slug);

        if (externalDto == null) {
            throw new NotFoundException("Movie not found: " + slug);
        }

        List<Category> categories = taxonomyService.resolveCategories(
                externalDto.getCategories() != null ? externalDto.getCategories() : List.of()
        );
        List<Country> countries = taxonomyService.resolveCountries(
                externalDto.getCountries() != null ? externalDto.getCountries() : List.of()
        );

        Movie newMovie = externalMovieMapper.ToEntity(externalDto, categories, countries);
        movieRepository.persist(newMovie);

        return mapToMovieDetailResponse(newMovie);
    }

    @Transactional
    public MovieDetailResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id);

        if (movie == null) {
            throw new NotFoundException("Movie not found with id: " + id);
        }

        return mapToMovieDetailResponse(movie);
    }

    private MovieResponse mapToMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .name(movie.getName())
                .slug(movie.getSlug())
                .thumbUrl(movie.getThumbUrl())
                .posterUrl(movie.getPosterUrl())
                .year(movie.getYear())
                .quality(movie.getQuality())
                .lang(movie.getLang())
                .episodeCurrent(movie.getEpisodeCurrent())
                .categories(movie.getCategories() != null ?
                        movie.getCategories().stream()
                                .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getSlug()))
                                .toList()
                        : List.of())
                .countries(movie.getCountries() != null ?
                        movie.getCountries().stream()
                                .map(c -> new CountryDTO(c.getId(), c.getName(), c.getSlug()))
                                .toList()
                        : List.of())
                .build();
    }

      private MovieDetailResponse mapToMovieDetailResponse(Movie movie) {
         return MovieDetailResponse.builder()
                 .id(movie.getId())
                 .name(movie.getName())
                 .originName(movie.getOriginName())
                 .slug(movie.getSlug())
                 .content(movie.getContent())
                 .thumbUrl(movie.getThumbUrl())
                 .posterUrl(movie.getPosterUrl())
                 .year(movie.getYear())
                 .quality(movie.getQuality())
                 .lang(movie.getLang())
                 .episodeTotal(movie.getEpisodeTotal())
                 .status(movie.getStatus())
                 .type(movie.getType())
                 .categories(movie.getCategories() != null ?
                         movie.getCategories().stream()
                                 .map(c -> new CategoryDTO(c.getId(), c.getName(), c.getSlug()))
                                 .toList()
                         : List.of())
                 .countries(movie.getCountries() != null ?
                         movie.getCountries().stream()
                                 .map(c -> new CountryDTO(c.getId(), c.getName(), c.getSlug()))
                                 .toList()
                         : List.of())
                 .episodes(movie.getEpisodes() != null ?
                         movie.getEpisodes().stream()
                                 .map(e -> {
                                     var response = new EpisodeResponse();
                                     response.setId(e.getId());
                                     response.setServerName(e.getServerName());
                                     response.setIsAi(e.getIsAi());
                                     response.setServerData(e.getServerData() != null ?
                                             e.getServerData().stream()
                                                     .map(sd -> new ServerDataResponse(
                                                             sd.getName(),
                                                             sd.getSlug(),
                                                             sd.getFilename(),
                                                             sd.getLinkEmbed(),
                                                             sd.getLinkM3u8()
                                                     ))
                                                     .toList()
                                             : List.of());
                                     return response;
                                 })
                                 .toList()
                         : List.of())
                 .build();
     }
}




