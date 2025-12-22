package com.elearning.elearning_platform.course.application.usecase;

import com.elearning.elearning_platform.course.application.port.in.PublishCourseCommand;
import com.elearning.elearning_platform.course.domain.exception.CourseNotFoundException;
import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.user.domain.exception.UserNotFoundException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case for publishing a course in the e-learning platform.
 *
 * This service validates and executes the necessary business logic to publish a course.
 * The validations include checking if the course exists, ensuring the requesting user is
 * valid, active, and holds the correct role, and verifying ownership of the course by the user.
 *
 * The course is only published if all validations pass successfully.
 */
@Service
public class PublishCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final UserRepositoryPort userRepository;

    /**
     * Executes the process of publishing a course.
     *
     * This method performs validations including:
     * - Checking if the course with the provided ID exists.
     * - Ensuring the requesting user exists, is active, and has the correct role (INSTRUCTOR).
     * - Verifying that the requesting user is the owner of the course.
     *
     * If all validations pass, the course is published and saved.
     *
     * @param command The command containing the course ID to publish and the ID of the requesting user.
     * @return The published course after being saved to the repository.
     * @throws CourseNotFoundException if the course with the given ID does not exist.
     * @throws UserNotFoundException if the requesting user does not exist.
     * @throws ValidationException if the requesting user is inactive, does not have the INSTRUCTOR role, or is not the owner of the course.
     */
    public Course execute(PublishCourseCommand command) {
        // Find Course by ID and Validates course exists
        Optional<Course> courseOptional = courseRepository.findById(command.courseId());
        if (courseOptional.isEmpty()) {
            throw new CourseNotFoundException("Course with ID " + command.courseId() + " not found");
        }

        Course course = courseOptional.get();

        // Validates requesting user is active, has INSTRUCTOR role, and is owner of course
        Optional<User> requestingUserOptional = userRepository.findById(command.requestingUserId().value());
        if (requestingUserOptional.isEmpty()) {
            throw new UserNotFoundException(UUID.fromString("User with ID " + command.requestingUserId() + " not found"));
        }

        User requestingUser = requestingUserOptional.get();
        if (!requestingUser.getActive()) {
            throw new ValidationException("User is not active");
        }

        // Validates requesting user has INSTRUCTOR role
        if (requestingUser.getRole() != Role.INSTRUCTOR) {
            throw new ValidationException("User must be an INSTRUCTOR to publish a course");
        }

        // Validates Ownership of Course
        if (!course.isOwnedBy(command.requestingUserId())) {
            throw new ValidationException("User is not the owner of the course");
        }

        // Publish Course
        course.publish();

        // Save Course
        return courseRepository.save(course);
    }
}
