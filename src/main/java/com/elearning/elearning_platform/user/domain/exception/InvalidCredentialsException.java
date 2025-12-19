package com.elearning.elearning_platform.user.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;

/**
 * Exception thrown when user authentication fails due to invalid credentials.
 *
 * This domain exception represents a business rule violation where
 * the provided authentication data does not match any valid user.
 *
 * It is intentionally generic to avoid leaking sensitive information
 * about which part of the credentials was incorrect.
 *
 * This exception extends {@link DomainException} to remain consistent
 * with the application's domain exception hierarchy.
 */
public class InvalidCredentialsException extends DomainException {

    /**
     * Creates an InvalidCredentialsException with a default error message.
     */
    public InvalidCredentialsException() {
        super("Invalid credentials for user");
    }
}

