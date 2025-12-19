package com.elearning.elearning_platform.user.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;
import com.elearning.elearning_platform.shared.domain.valueobject.Email;

/**
 * Exception thrown when attempting to create or update a user
 * with an email that already exists in the system.
 *
 * This domain exception represents a violation of the business
 * rule that enforces email uniqueness within the User aggregate.
 *
 * It is typically raised during user registration or profile
 * update operations when the provided {@link Email} is already
 * associated with another user.
 *
 * This exception extends {@link DomainException} to keep all
 * domain errors consistent and centralized.
 */
public class DuplicateEmailException extends DomainException {

    /**
     * Creates a DuplicateEmailException for an already registered email.
     *
     * @param email the email value object that caused the conflict
     */
    public DuplicateEmailException(Email email) {
        super("This email already exists: " + email.getValue());
    }
}

