package com.project.bibly_be.sermon.exception;

import com.project.bibly_be.bible.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ApiResponseDTO.error(ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    // Handle Invalid Input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ApiResponseDTO.error(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    // Handle Other Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<String>> handleGlobalException(Exception ex) {
        ex.printStackTrace();  // Log stack trace for debugging
        String message = ex.getMessage() != null ? ex.getMessage() : "No detailed error message available";
        return new ResponseEntity<>(
                ApiResponseDTO.error("An unexpected error occurred: " + message),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
