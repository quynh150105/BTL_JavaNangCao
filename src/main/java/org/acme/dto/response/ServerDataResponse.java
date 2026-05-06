package org.acme.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerDataResponse {
    private String name;
    private String slug;
    private String filename;
    private String linkEmbed;
    private String linkM3u8;
}
