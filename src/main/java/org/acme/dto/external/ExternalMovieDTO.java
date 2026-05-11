package org.acme.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalMovieDTO {
    private String name;
    private String slug;
    
    @JsonProperty("origin_name")
    private String originName;
    
    private String content;

    @JsonProperty("thumb_url")
    private String thumbUrl;
    
    @JsonProperty("poster_url")
    private String posterUrl;

    private String year;
    private String quality;
    private String lang;

    @JsonProperty("episode_current")
    private String episodeCurrent;
    
    @JsonProperty("episode_total")
    private String episodeTotal;

    private String type;
    private String status;

    private List<String> categories;
    private List<String> countries;
    
    @JsonProperty("episodes")
    private List<ExternalEpisodeDTO> episodes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalListResponse {
        private List<ExternalMovieDTO> data;
    }

}
