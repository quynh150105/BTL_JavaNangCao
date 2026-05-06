package org.acme.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor@Builder
public class UpdateUserRequest {
    @NotBlank(message="username không được để trống")
    @Size(min = 3, max = 50, message ="username phải có từ 3-50 ký tự")
    @Schema()
    private String username;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    private String avatarUrl;
}
