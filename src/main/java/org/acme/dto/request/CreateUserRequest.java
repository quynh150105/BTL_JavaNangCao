package org.acme.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank(message="username không được để trống")
    @Size(min = 3, max = 50, message ="Username phải có từ 3-50 ký tự")
    @Schema(example = "user", description = "example username")
    private String username;

    @NotBlank(message="password không được để trống")
    @Size(min = 6, max = 50, message ="password phải có từ 6-50 ký tự")
    @Schema(example = "password", description = "example password")
    private String password;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Schema(example = "example@gmail.com", description = "example password")
    private String email;
}
