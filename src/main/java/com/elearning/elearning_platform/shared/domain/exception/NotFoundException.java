package com.elearning.elearning_platform.shared.domain.exception;

/**
 * Domain exception thrown when a requested entity cannot be found.
 *
 * This exception belongs to the domain layer and should be used to signal
 * that an aggregate or entity identified by a given id does not exist.
 * It is framework-agnostic and can be translated to an appropriate
 * application or infrastructure-level response (e.g. HTTP 404).
 */
public class NotFoundException extends DomainException {
    public NotFoundException(String entity, Object id) {
        super(String.format("%s with id %s not found", entity, id));
    }
}
