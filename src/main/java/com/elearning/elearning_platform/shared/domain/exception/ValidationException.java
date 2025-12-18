package com.elearning.elearning_platform.shared.domain.exception;

import java.util.List;

/**
 * Domain exception thrown when one or more validation rules are violated.
 *
 * It contains the list of validation errors produced during the validation
 * of a command, entity, or aggregate. This exception is domain-level and
 * should be handled or translated at higher layers.
 */
public class ValidationException extends DomainException {

    private final List<String> errors;

    public ValidationException(List<String> errors) {

        super("Validation failed: " + String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
