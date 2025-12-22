package com.elearning.elearning_platform.course.application.port.in;

import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;

/**
 * Command object for creating a new course.
 *
 * This record encapsulates the necessary data required to create a course in the system.
 * The command is immutable and contains the following fields:
 *
 * - title: The title of the course.
 * - description: A brief description of the course.
 * - price: The monetary cost of the course, represented as a {@code Money} value object.
 * - categoryId: The ID representing the category to which the course belongs,
 * encapsulated in a {@code CategoryId}.
 * - instructorId: The ID of the instructor who is responsible for the course,
 * encapsulated in a {@code UserId}.
 */
public record CreateCourseCommand(

        String title,
        String description,
        Money price,
        CategoryId categoryId,
        UserId instructorId
) {}
