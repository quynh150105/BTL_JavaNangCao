package org.acme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeResponse {
    private Long id;
    private String serverName;
    private Boolean isAi;
    private List<ServerDataResponse> serverData;
}
