package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository interface for {@link CourseEntity} persistence operations.
 *
 * This interface extends {@link JpaRepository} to provide standard CRUD operations
 * and defines custom query methods for course-specific retrieval patterns.
 *
 * Spring Data JPA automatically implements this interface at runtime, eliminating
 * the need for manual implementation code. Query methods are derived from method names
 * following Spring Data naming conventions.
 *
 * No {@code @Repository} annotation is required as Spring Data JPA handles
 * component registration automatically.
 */
public interface JpaCourseRepository extends JpaRepository<CourseEntity, UUID> {

    /**
     * Finds all courses taught by a specific instructor.
     *
     * This method retrieves courses where the instructor_id matches the provided UUID.
     * Useful for displaying all courses created by a particular instructor.
     *
     * @param instructorId the UUID of the instructor whose courses should be retrieved
     * @return a list of {@link CourseEntity} objects taught by the instructor,
     *         or an empty list if no courses are found
     */
    List<CourseEntity> findByInstructorId(UUID instructorId);

    /**
     * Finds all courses with a specific status.
     *
     * This method retrieves courses filtered by their publication status
     * (e.g., DRAFT, PUBLISHED, ARCHIVED). Useful for displaying courses
     * in different stages of their lifecycle.
     *
     * @param status the {@link CourseStatus} to filter by
     * @return a list of {@link CourseEntity} objects with the specified status,
     *         or an empty list if no matching courses are found
     */
    List<CourseEntity> findByStatus(CourseStatus status);

    /**
     * Finds all courses belonging to a specific category.
     *
     * This method retrieves courses where the category_id matches the provided UUID.
     * Useful for displaying courses grouped by subject area or topic.
     *
     * @param categoryId the UUID of the category whose courses should be retrieved
     * @return a list of {@link CourseEntity} objects in the specified category,
     *         or an empty list if no courses are found
     */
    List<CourseEntity> findByCategoryId(UUID categoryId);

    List<CourseEntity> id(UUID id);
}