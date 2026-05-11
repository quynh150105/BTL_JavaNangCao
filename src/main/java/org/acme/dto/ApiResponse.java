package org.acme.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .status(200)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message){
        return ApiResponse.<T>builder()
                .status(code)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error( String message){
        return ApiResponse.<T>builder()
                .message(message)
                .build();
    }
}
