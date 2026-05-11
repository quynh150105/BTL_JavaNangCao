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
public class ExternalEpisodeDTO {

    @JsonProperty("server_name")
    private String serverName;

    @JsonProperty("is_ai")
    private Boolean isAi;

    @JsonProperty("items")
    private List<ExternalServerDataDTO> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalServerDataDTO {
        private String name;
        private String slug;
        private String filename;

        @JsonProperty("link_embed")
        private String linkEmbed;

        @JsonProperty("link_m3u8")
        private String linkM3u8;
    }
}

