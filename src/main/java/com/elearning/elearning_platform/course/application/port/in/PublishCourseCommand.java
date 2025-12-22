package com.elearning.elearning_platform.course.application.port.in;

import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;

/**
 * Command object for publishing a course.
 *
 * This record encapsulates the data necessary to request the publication
 * of a course within the system. The command is immutable and contains
 * the following fields:
 *
 * - courseId: The unique identifier of the course to be published, encapsulated in {@code CourseId}.
 * - requestingUserId: The unique identifier of the user requesting the publication,
 * encapsulated in {@code UserId}.
 */
public record PublishCourseCommand (

        CourseId courseId,
        UserId requestingUserId
){}
