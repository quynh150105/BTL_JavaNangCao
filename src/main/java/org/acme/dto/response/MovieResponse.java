package org.acme.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponse {
    private Long id;
    private String name;
    private String slug;
    private String thumbUrl;
    private String posterUrl;
    private String year;
    private String quality;
    private String lang;
    private String episodeCurrent;

    private List<CategoryDTO> categories;
    private List<CountryDTO> countries;
}
