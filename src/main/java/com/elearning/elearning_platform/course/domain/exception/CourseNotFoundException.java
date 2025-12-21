package com.elearning.elearning_platform.course.domain.exception;

import com.elearning.elearning_platform.shared.domain.exception.NotFoundException;

/**
 * Exception thrown when a course cannot be found.
 */
public class CourseNotFoundException extends NotFoundException {

    /**
     * Creates an exception using the given CourseId.
     *
     * @param courseId identifier of the course not found
     */
    public CourseNotFoundException(CourseId courseId) {
        super("Course with id " + courseId + " not found");
    }

    /**
     * Creates an exception with a custom message.
     *
     * @param message custom error message
     */
    public CourseNotFoundException(String message) {
        super(message);
    }
}

