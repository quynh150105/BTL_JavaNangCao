package org.acme.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter
@Builder
@RegisterForReflection
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;

}
