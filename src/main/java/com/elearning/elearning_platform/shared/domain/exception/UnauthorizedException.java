package com.elearning.elearning_platform.shared.domain.exception;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 *
 * Maps to HTTP 401 Unauthorized status code.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Authentication required");
    }
}
