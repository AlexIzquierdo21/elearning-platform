package com.elearning.elearning_platform.course.application.usecase;

import com.elearning.elearning_platform.course.application.port.in.CreateCourseCommand;
import com.elearning.elearning_platform.course.domain.exception.CategoryNotFoundException;
import com.elearning.elearning_platform.course.domain.model.Category;
import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.port.out.CategoryRepositoryPort;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.user.domain.exception.UserNotFoundException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final UserRepositoryPort userRepository;

    /**
     * Constructor for {@code CreateCourseUseCase}.
     *
     * Initializes the use case with the required repository ports for managing courses,
     * categories, and users. These repositories are essential to perform operations
     * regarding course creation while validating the associated category and user.
     *
     * @param courseRepository the repository responsible for managing courses, must not be null
     * @param categoryRepository the repository responsible for managing categories, must not be null
     * @param userRepository the repository responsible for managing users, must not be null
     */
    public CreateCourseUseCase(CourseRepositoryPort courseRepository,
                               CategoryRepositoryPort categoryRepository,
                               UserRepositoryPort userRepository
    ) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executes the creation of a new course by validating the provided command details.
     * This includes verifying the instructor, category, and other necessary parameters.
     * Throws appropriate exceptions if validations fail.
     *
     * @param command the command containing the data required for course creation, must not be null
     * @return the created Course instance
     * @throws UserNotFoundException if the instructor specified in the command does not exist
     * @throws ValidationException if the instructor is not active or does not have the required role of INSTRUCTOR
     * @throws CategoryNotFoundException if the specified category does not exist
     * @throws ValidationException if the specified category is not active
     */
    public Course execute(CreateCourseCommand command) {
        // Validation instructor exists
        Optional<User> instructorOptional = userRepository.findById(command.instructorId().value());
        if (instructorOptional.isEmpty()) {
            throw new UserNotFoundException(command.instructorId().value());
        }
        // Validation instructor is Active
        User instructor = instructorOptional.get();
        if (!instructor.getActive()) {
            throw new ValidationException("Instructor is not active");
        }
        // Validation user has an INSTRUCTOR role
        if (instructor.getRole() != Role.INSTRUCTOR) {
            throw new ValidationException("User must be an INSTRUCTOR to create a course");
        }
        // Validation category exists
        Optional<Category> categoryOptional = categoryRepository.findById(command.categoryId());
        if (categoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category with ID " + command.categoryId());
        }
        // Validation category is active
        Category category = categoryOptional.get();
        if (!category.isActive()) {
            throw new ValidationException("Category is not active");
        }
        // Create course
        Course course = Course.create(command.title(),
                command.description(),
                command.price(),
                command.instructorId(),
                command.categoryId());

        return courseRepository.save(course);
    }
}
