package com.elearning.elearning_platform.user.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;
import com.elearning.elearning_platform.shared.domain.valueobject.Email;

/**
 * Exception thrown when a user cannot be found in the system.
 *
 * This domain exception represents the absence of a User aggregate
 * when queried by its identifier or by its email value object.
 *
 * It is intended to be raised from the domain or application layer
 * when a lookup operation yields no result, signaling a business-level
 * error rather than a technical one.
 *
 * This exception extends {@link DomainException} to ensure consistency
 * across all domain-related errors.
 */
public class UserNotFoundException extends DomainException {

    /**
     * Creates a UserNotFoundException for a missing user identified by id.
     *
     * @param userId the unique identifier of the user
     */
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }

    /**
     * Creates a UserNotFoundException for a missing user identified by email.
     *
     * @param email the email value object used to search for the user
     */
    public UserNotFoundException(Email email) {
        super("User not found with email: " + email.getValue());
    }
}
