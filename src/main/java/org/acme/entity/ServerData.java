package org.acme.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ServerData {
    private String name;       // "Tập 1"
    private String slug;       // "tap-1"
    private String filename;
    private String linkEmbed;
    private String linkM3u8;
}
