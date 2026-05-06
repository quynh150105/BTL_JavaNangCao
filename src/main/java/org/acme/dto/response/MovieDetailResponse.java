package org.acme.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetailResponse {
    private Long id;
    private String name;
    private String originName;
    private String slug;
    private String content;

    private String thumbUrl;
    private String posterUrl;

    private String year;
    private String quality;
    private String lang;

    private String episodeTotal;
    private String status;
    private String type;

    private List<CategoryDTO> categories;
    private List<CountryDTO> countries;

    private List<EpisodeResponse> episodes;
}
