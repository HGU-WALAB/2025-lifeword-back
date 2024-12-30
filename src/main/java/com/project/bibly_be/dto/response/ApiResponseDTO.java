package com.project.bibly_be.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T response_object;

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .response_object(data)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .response_object(null)
                .build();
    }
}