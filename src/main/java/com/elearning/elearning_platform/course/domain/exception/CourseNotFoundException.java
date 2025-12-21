package com.elearning.elearning_platform.course.domain.exception;

import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.exception.NotFoundException;

/**
 * Exception thrown when a specified course cannot be found in the system.
 *
 * This exception is a specialization of {@link NotFoundException} and is used
 * specifically for scenarios where an operation fails due to a course not being
 * available or identifiable by the given criteria.
 */
public class CourseNotFoundException extends NotFoundException {

    /**
     * Constructs a new CourseNotFoundException with the specified course ID.
     *
     * This exception is thrown when a course identified by the given course ID
     * cannot be located in the system.
     *
     * @param courseId the unique identifier of the course that could not be found
     */
    public CourseNotFoundException(CourseId courseId) {
        super("Course", courseId.toString());
    }

    /**
     * Constructs a new CourseNotFoundException with a detailed message.
     *
     * This exception indicates that a specific course could not be found,
     * providing additional context through a descriptive message.
     *
     * @param message a detailed message explaining the reason for the exception
     */
    public CourseNotFoundException(String message) {
        super("Course", message);
    }
}

