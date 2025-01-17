package com.project.bibly_be.user.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private final boolean success;
    private final String message;
    private final int status;
    private final T data;//data;

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, 200, data);
    }

    public static <T> ApiResponseDTO<T> error(String message, int status) {
        return new ApiResponseDTO<>(false, message, status, null);
    }
}
