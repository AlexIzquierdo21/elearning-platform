package com.elearning.elearning_platform.shared.infrastructure.config;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for standardized error responses.
 *
 * Used by GlobalExceptionHandler to format exceptions into HTTP responses.
 *
 * @param timestamp when the error occurred
 * @param status HTTP status code
 * @param error HTTP status reason phrase
 * @param message detailed error message
 * @param path the request path that caused the error (optional)
 * @param errors list of validation errors (optional, for ValidationException)
 */
public record ErrorResponse(LocalDateTime timestamp,
                            int status,
                            String error,
                            String message,
                            String path,
                            List<String> errors) {

    /// Constructor para errores simples (sin path ni errors)
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, null, null);
    }

    /// Constructor para errores con path
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    /// Constructor para ValidationException con lista de errores
    public static ErrorResponse withErrors(int status, String error, String message, List<String> errors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, null, errors);
    }
}
