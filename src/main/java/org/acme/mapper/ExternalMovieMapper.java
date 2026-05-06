package org.acme.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.dto.external.ExternalMovieDTO;
import org.acme.entity.Category;
import org.acme.entity.Country;
import org.acme.entity.Movie;

import java.util.List;

@ApplicationScoped
public class ExternalMovieMapper {

    public Movie ToEntity(ExternalMovieDTO dto, List<Category> categories, List<Country> countries){
        return Movie.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .originName(dto.getOriginName())
                .content(dto.getContent())
                .thumbUrl(dto.getThumbUrl())
                .posterUrl(dto.getPosterUrl())
                .year(dto.getYear())
                .quality(dto.getQuality())
                .lang(dto.getLang())
                .episodeCurrent(dto.getEpisodeCurrent())
                .episodeTotal(dto.getEpisodeTotal())
                .type(dto.getType())
                .status(dto.getStatus())
                .categories(categories)
                .countries(countries)
                .build();

    }
}
