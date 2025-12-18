package com.elearning.elearning_platform.shared.domain.exception;

import jakarta.validation.ValidationException;

/**
 * Base exception for all domain-related errors in the application.
 *
 * This exception represents violations of business rules or domain logic.
 * All domain exceptions should extend this class to maintain a consistent
 * exception hierarchy.
 *
 * Being a RuntimeException, it does not need to be explicitly caught,
 * allowing the application layer to handle it appropriately.
 *
 * @see NotFoundException
 * @see ValidationException
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
