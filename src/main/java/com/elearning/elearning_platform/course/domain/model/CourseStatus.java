package com.elearning.elearning_platform.course.domain.model;

import com.elearning.elearning_platform.course.domain.exception.InvalidCourseStateException;

/**
 * Represents the publication status of a course in its lifecycle.
 *
 * Valid state transitions:
 *   DRAFT → PUBLISHED
 *   PUBLISHED → ARCHIVED
 *
 * Invalid transitions will throw {@link InvalidCourseStateException}
 * when attempting to change course status.
 */
public enum CourseStatus {

    /**
     * Course is being created/edited by instructor.
     * Not visible in public catalog.
     */
    DRAFT,

    /**
     * Course is published and visible in catalog.
     * Students can enroll.
     */
    PUBLISHED,

    /**
     * Course is no longer available for new enrollments.
     * Existing students may still have access.
     */
    ARCHIVED;

    /**
     * Validates if a transition to another status is allowed.
     *
     * Allowed transitions:
     *
     *   DRAFT → PUBLISHED
     *   PUBLISHED → ARCHIVED
     *
     * @param targetStatus the status to transition to
     * @return true if transition is valid, false otherwise
     */
    public boolean canTransitionTo(CourseStatus targetStatus) {
        return switch (this) {
            case DRAFT -> targetStatus == PUBLISHED;
            case PUBLISHED -> targetStatus == ARCHIVED;
            case ARCHIVED -> false; // No transitions allowed from ARCHIVED
        };
    }

    /**
     * Returns a human-readable description of why a transition is invalid.
     *
     * @param targetStatus the attempted target status
     * @return error message explaining why transition is not allowed
     */
    public String getTransitionErrorMessage(CourseStatus targetStatus) {
        if (this == targetStatus) {
            return String.format("Course is already %s", this.name());
        }

        return switch (this) {
            case DRAFT -> "Draft courses can only be published";
            case PUBLISHED -> "Published courses can only be archived";
            case ARCHIVED -> "Archived courses cannot be modified";
        };
    }
}