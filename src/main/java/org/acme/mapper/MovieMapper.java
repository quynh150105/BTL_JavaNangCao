package org.acme.mapper;

import org.acme.dto.response.*;
import org.acme.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "cdi")
public interface MovieMapper {
    // ===== LIST =====
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "countries", source = "countries")
    MovieResponse toResponse(Movie movie);

    List<MovieResponse> toResponseList(List<Movie> movies);

    // ===== DETAIL =====
    MovieDetailResponse toDetail(Movie movie);

    List<CategoryDTO> toCategoryDTO(List<Category> categories);
    List<CountryDTO> toCountryDTO(List<Country> countries);

    EpisodeResponse toEpisodeResponse(Episode episode);
    List<EpisodeResponse> toEpisodeResponseList(List<Episode> episodes);

    ServerDataResponse toServerData(ServerData data);
    List<ServerDataResponse> toServerDataList(List<ServerData> data);

    CategoryDTO map(Category category);
    CountryDTO map(Country country);

    // ===== CUSTOM =====
    default List<String> mapCategoryNames(List<Category> list) {
        if (list == null) return List.of();
        return list.stream().map(Category::getName).collect(Collectors.toList());
    }

    default List<String> mapCountryNames(List<Country> list) {
        if (list == null) return List.of();
        return list.stream().map(Country::getName).collect(Collectors.toList());
    }
}
