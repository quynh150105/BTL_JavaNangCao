package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.acme.dto.response.MovieDetailResponse;
import org.acme.dto.response.MovieResponse;
import org.acme.entity.Movie;
import org.acme.mapper.MovieMapper;
import org.acme.repository.MovieRepository;

import java.util.List;

@ApplicationScoped
public class MovieService {

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private MovieMapper movieMapper;


    public List<MovieResponse> getMovie(int page, int size) {
        List<Movie> movies = movieRepository.findAll().page(page, size).list();
        return movieMapper.toResponseList(movies);
    }

    public MovieDetailResponse getMovieDetail(String slug) {
        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return movieMapper.toDetail(movie);
    }
}
