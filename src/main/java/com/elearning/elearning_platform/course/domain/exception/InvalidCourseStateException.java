package com.elearning.elearning_platform.course.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;

public class InvalidCourseStateException extends DomainException {
    public InvalidCourseStateException(String message) {
        super("Invalid course state: " + message + ".");
    }
}
