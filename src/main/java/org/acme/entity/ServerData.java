package org.acme.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerData {
    private String name;
    private String slug;
    private String filename;
    private String linkEmbed;
    private String linkM3u8;
}
