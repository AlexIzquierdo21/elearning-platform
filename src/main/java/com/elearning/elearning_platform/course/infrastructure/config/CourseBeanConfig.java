package com.elearning.elearning_platform.course.infrastructure.config;

import com.elearning.elearning_platform.course.application.usecase.CreateCourseUseCase;
import com.elearning.elearning_platform.course.application.usecase.PublishCourseUseCase;
import com.elearning.elearning_platform.course.domain.port.out.CategoryRepositoryPort;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for Course domain use cases.
 *
 * This configuration class defines beans for the application layer use cases
 * related to course management. It follows the hexagonal architecture pattern
 * by keeping the application layer free of Spring annotations and configuring
 * dependency injection externally.
 *
 * The beans defined here orchestrate course-related business operations
 * such as course creation and publication.
 */
@Configuration
public class CourseBeanConfig {

    /**
     * Creates a bean for the {@link CreateCourseUseCase}.
     *
     * This use case handles the creation of new courses in the system,
     * including validation of instructors and categories.
     *
     * @param courseRepositoryPort repository port for course persistence operations
     * @param categoryRepositoryPort repository port for category lookup operations
     * @param userRepositoryPort repository port for user validation operations
     * @return a configured instance of {@link CreateCourseUseCase}
     */
    @Bean
    public CreateCourseUseCase createCourseUseCase(
            CourseRepositoryPort courseRepositoryPort,
            CategoryRepositoryPort categoryRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        return new CreateCourseUseCase(courseRepositoryPort, categoryRepositoryPort, userRepositoryPort);
    }

    /**
     * Creates a bean for the {@link PublishCourseUseCase}.
     *
     * This use case handles the publication of courses, transitioning them
     * from DRAFT to PUBLISHED status after validating ownership and permissions.
     *
     * @param courseRepositoryPort repository port for course persistence and retrieval operations
     * @param userRepositoryPort repository port for user validation and ownership verification
     * @return a configured instance of {@link PublishCourseUseCase}
     */
    @Bean
    public PublishCourseUseCase publishCourseUseCase(
            CourseRepositoryPort courseRepositoryPort,
            UserRepositoryPort userRepositoryPort
    ) {
        return new PublishCourseUseCase(courseRepositoryPort, userRepositoryPort);
    }
}
