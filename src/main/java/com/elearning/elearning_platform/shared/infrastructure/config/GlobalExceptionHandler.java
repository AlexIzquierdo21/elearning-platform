package com.elearning.elearning_platform.shared.infrastructure.config;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;
import com.elearning.elearning_platform.shared.domain.exception.NotFoundException;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 *
 * Intercepts exceptions thrown from any layer and translates them into
 * standardized HTTP responses with appropriate status codes and error details.
 *
 * This handler is part of the infrastructure layer and knows about HTTP
 * and Spring MVC, while the domain exceptions remain framework-agnostic.
 *
 * @see ErrorResponse
 * @see DomainException
 * @see NotFoundException
 * @see ValidationException
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles general domain exceptions.
     *
     * Catches any {@link DomainException} that represents a business rule violation
     * and returns a 400 Bad Request response.
     *
     * @param ex the domain exception thrown
     * @return ResponseEntity with 400 status and error details
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(400, "Bad request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles not found exceptions.
     *
     * Catches {@link NotFoundException} when a requested entity does not exist
     * and returns a 404 Not Found response.
     *
     * @param ex the not found exception thrown
     * @return ResponseEntity with 404 status and error details
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(404, "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles validation exceptions.
     *
     * Catches {@link ValidationException} when multiple validation rules fail
     * and returns a 400 Bad Request response with the list of validation errors.
     *
     * @param ex the validation exception containing the list of errors
     * @return ResponseEntity with 400 status and detailed validation errors
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ErrorResponse errorResponse = ErrorResponse.withErrors(400, "Bad Request", "Validation failed", ex.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles any unexpected exception.
     *
     * Catches all exceptions not handled by other handlers and returns a
     * 500 Internal Server Error response. The actual exception details are
     * logged but not exposed to the client for security reasons.
     *
     * @param ex the unexpected exception
     * @return ResponseEntity with 500 status and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ErrorResponse errorResponse = ErrorResponse.of(500, "Internal Server Error", "An unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
