package com.elearning.elearning_platform.course.domain.model;

import com.elearning.elearning_platform.course.domain.exception.InvalidCourseStateException;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import org.testng.annotations.IFactoryAnnotation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public class Course {

    /**
     * The unique identifier for the course.
     *
     * This field holds a {@link CourseId}, which serves as the primary identifier
     * for an instance of {@code Course}. It ensures global uniqueness across all
     * courses within the system. The CourseId encapsulates a UUID value and
     * provides utilities for generating or validating identifiers.
     *
     * This field is immutable and mandatory, ensuring that every course instance
     * is uniquely distinguishable from others.
     */
    private final CourseId id;
    private String title;
    private String description;
    private Money price;
    private CourseStatus status;
    private final UserId instructorId;
    private CategoryId categoryId;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    /**
     * Constructs a new {@code Course} instance with the provided parameters.
     *
     * @param id the unique identifier of the course, must not be null
     * @param title the title of the course, must not be null or blank
     * @param description a detailed description of the course, must not be null
     * @param price the price of the course, represented as a {@code Money} object, must not be null
     * @param status the current publication status of the course, represented as {@code CourseStatus}, must not be null
     * @param instructorId the unique identifier of the course instructor, must not be null
     * @param categoryId the unique identifier of the category to which this course belongs, must not be null
     * @param createdAt the date and time when this course was created, must not be null
     * @param updatedAt the date and time when this course was last updated, must not be null
     * @param publishedAt the date and time when this course was published, nullable for unpublished courses
     */
    public Course(CourseId id,
                  String title,
                  String description,
                  Money price,
                  CourseStatus status,
                  UserId instructorId,
                  CategoryId categoryId,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt,
                  LocalDateTime publishedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
    }

    /**
     * Creates a new {@code Course} instance with the specified parameters.
     *
     * This method validates the input values to ensure that they meet the
     * required domain rules before creating the course object.
     *
     * @param title the title of the course, must not be null, blank, or exceed 200 characters
     * @param description the description of the course, must not be null and must have at least 50 characters
     * @param price the price of the course, represented as a {@code Money} object, must not be null and must be at least $1.00
     * @param instructorId the unique identifier of the instructor for the course, must not be null
     * @param categoryId the unique identifier of the category to which the course belongs, must not be null
     * @return a new {@code Course} instance in draft status with the specified values
     * @throws ValidationException if any of the validation rules are violated
     */
    public static Course create(String title,
                                String description,
                                Money price,
                                UserId instructorId,
                                CategoryId categoryId
    ) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Course title cannot be empty");
        }
        if (title.length() > 200) {
            throw new ValidationException("Course title cannot exceed 200 characters");
        }
        if (description.length() < 50) {
            throw new ValidationException("Course description must be at least 50 characters long");
        }
        if (price == null) {
            throw new ValidationException("Course price cannot be null");
        }
        if (price.getAmount().compareTo(new BigDecimal("1.00")) < 0) {
            throw new ValidationException("Course price must be at least $1.00");
        }
        if (instructorId == null) {
            throw new ValidationException("Instructor ID cannot be null");
        }
        if (categoryId == null) {
            throw new ValidationException("Category ID cannot be null");
        }
        return new Course(
                CourseId.generate(),
                title.trim(),
                description.trim(),
                price,
                CourseStatus.DRAFT,
                instructorId,
                categoryId,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    /**
     * Publishes the course by transitioning its status to {@code PUBLISHED}.
     *
     * This method performs a validation check to ensure the current status
     * allows a transition to {@code PUBLISHED}. If the transition is invalid,
     * an {@link InvalidCourseStateException} is thrown with a detailed error message.
     *
     * Upon successful publication:
     * - The course status is updated to {@code PUBLISHED}.
     * - The {@code publishedAt} timestamp is set to the current date and time.
     * - The {@code updatedAt} timestamp is updated to the current date and time.
     *
     * @throws InvalidCourseStateException if the current course status does not allow transitioning to {@code PUBLISHED}
     */
    public void publish() {
        if (!status.canTransitionTo(CourseStatus.PUBLISHED)) {
            throw new InvalidCourseStateException(
                    status.getTransitionErrorMessage(CourseStatus.PUBLISHED)
            );
        }

        this.status = CourseStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Archives the course by transitioning its status to {@code ARCHIVED}.
     *
     * This method verifies whether the current course status allows
     * a transition to the {@code ARCHIVED} state. If the transition
     * is invalid, an {@link InvalidCourseStateException} is thrown
     * with a descriptive error message.
     *
     * Upon successful archiving:
     * - The course status is updated to {@code ARCHIVED}.
     * - The {@code updatedAt} timestamp is set to the current date and time.
     *
     * @throws InvalidCourseStateException if the current course status does not
     *         allow transitioning to {@code ARCHIVED}
     */
    public  void archive() {
        if (!status.canTransitionTo(CourseStatus.ARCHIVED)) {
            throw new InvalidCourseStateException(
                    status.getTransitionErrorMessage(CourseStatus.ARCHIVED)
            );
        }

            this.status = CourseStatus.ARCHIVED;
            this.updatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the course is owned by the given instructor.
     *
     * This method compares the unique identifier of the course's instructor
     * with the provided instructor identifier to determine ownership.
     *
     * @param instructorId the unique identifier of the instructor to check against
     * @return true if the course is owned by the given instructor, false otherwise
     */
    public boolean isOwnedBy(UserId instructorId) {
        return this.instructorId.equals(instructorId);
    }

    // Getters

    public CourseId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Money getPrice() {
        return price;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public UserId getInstructorId() {
        return instructorId;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
}
















