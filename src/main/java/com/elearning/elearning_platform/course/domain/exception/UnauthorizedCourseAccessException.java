package com.elearning.elearning_platform.course.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.DomainException;

public class UnauthorizedCourseAccessException extends DomainException {
    public UnauthorizedCourseAccessException(String message) {
        super("You don't have permission to access this course: " + message + ".");
    }
}
