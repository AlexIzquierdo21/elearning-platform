package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.model.CourseStatus;
import com.elearning.elearning_platform.course.domain.port.out.CourseRepositoryPort;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA adapter implementation of {@link CourseRepositoryPort}.
 *
 * <p>This adapter acts as a bridge between the domain layer and the
 * infrastructure persistence layer, translating domain operations into
 * JPA repository calls and converting between domain models and JPA entities.
 *
 * <p>Following hexagonal architecture principles, this adapter allows the
 * domain layer to remain independent of persistence implementation details.
 */
@Component
public class CourseRepositoryAdapter implements CourseRepositoryPort {

    private final JpaCourseRepository jpaCourseRepository;

    /**
     * Constructs a new CourseRepositoryAdapter.
     *
     * @param jpaCourseRepository the Spring Data JPA repository for course persistence
     */
    public CourseRepositoryAdapter(JpaCourseRepository jpaCourseRepository) {
        this.jpaCourseRepository = jpaCourseRepository;
    }

    /**
     * Persists a course to the database.
     *
     * @param course the domain course to save
     * @return the saved course with updated persistence metadata
     */
    @Override
    public Course save(Course course) {
        CourseEntity entity = CourseMapper.toEntity(course);
        CourseEntity saved = jpaCourseRepository.save(entity);
        return CourseMapper.toDomain(saved);
    }

    /**
     * Finds a course by its unique identifier.
     *
     * @param id the course identifier
     * @return an Optional containing the course if found, empty otherwise
     */
    @Override
    public Optional<Course> findById(CourseId id) {
        return jpaCourseRepository.findById(id.value())
                .map(CourseMapper::toDomain);
    }

    /**
     * Finds all courses taught by a specific instructor.
     *
     * @param instructorId the instructor identifier
     * @return a list of courses taught by the instructor
     */
    @Override
    public List<Course> findByInstructorId(UserId instructorId) {
        return jpaCourseRepository.findByInstructorId(instructorId.value()).stream()
                .map(CourseMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Finds all courses with a specific status.
     *
     * @param status the course status to filter by
     * @return a list of courses with the specified status
     */
    @Override
    public List<Course> findByStatus(CourseStatus status) {
        return jpaCourseRepository.findByStatus(status).stream()
                .map(CourseMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Finds all courses belonging to a specific category.
     *
     * @param categoryId the category identifier
     * @return a list of courses in the specified category
     */
    @Override
    public List<Course> findByCategoryId(CategoryId categoryId) {
        return jpaCourseRepository.findByCategoryId(categoryId.value())
                .stream().map(CourseMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a course by its unique identifier.
     *
     * @param id the course identifier
     */
    @Override
    public void deleteById(CourseId id) {
        jpaCourseRepository.deleteById(id.value());
    }

    /**
     * Checks whether a course exists with the given identifier.
     *
     * @param id the course identifier to check
     * @return true if a course exists, false otherwise
     */
    @Override
    public boolean existsById(CourseId id) {
        return jpaCourseRepository.existsById(id.value());
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return a list of all courses
     */
    @Override
    public List<Course> findAll() {
        List<CourseEntity> courseEntities = jpaCourseRepository.findAll();
        return courseEntities.stream()
                .map(CourseMapper::toDomain)
                .collect(Collectors.toList());
    }
}

















