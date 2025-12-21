package com.elearning.elearning_platform.shared.domain.exception;

/**
 * Exception thrown when user is authenticated but lacks required permissions.
 *
 * Maps to HTTP 403 Forbidden status code.
 */
public class AccessDeniedException extends DomainException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException() {
        super("Insufficient privileges to access this resource");
    }
}