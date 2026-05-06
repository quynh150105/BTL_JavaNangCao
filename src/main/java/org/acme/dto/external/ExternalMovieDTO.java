package org.acme.dto.external;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalMovieDTO {
    private String name;
    private String slug;
    private String originName;
    private String content;

    private String thumbUrl;
    private String posterUrl;

    private String year;
    private String quality;
    private String lang;

    private String episodeCurrent;
    private String episodeTotal;

    private String type;
    private String status;

    private List<String> categories;
    private List<String> countries;
}
