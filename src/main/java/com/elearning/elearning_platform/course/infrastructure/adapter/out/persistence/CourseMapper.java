package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Course;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.course.domain.valueobject.CourseId;
import com.elearning.elearning_platform.shared.domain.valueobject.Money;
import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;

/**
 * Mapper class for converting between {@link Course} domain objects
 * and {@link CourseEntity} JPA entities.
 *
 * This mapper provides bidirectional transformation to maintain a clear
 * separation between the domain layer and the infrastructure persistence layer,
 * following hexagonal architecture principles.
 *
 * Key transformations:
 *
 * - UUID ↔ Value Objects (CourseId, UserId, CategoryId)</li>
 * - Money value object ↔ Separate priceAmount and priceCurrency fields</li>
 *
 *
 * All methods are static utility methods, and the class cannot be instantiated.
 */
public class CourseMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private CourseMapper() {}

    /**
     * Converts a {@link CourseEntity} from the persistence layer
     * into a {@link Course} domain object.
     *
     * This method reconstructs the domain model from the database representation,
     * including:
     *
     * - Converting UUID identifiers into value objects
     * - Reconstructing the Money value object from amount and currency fields
     * - Preserving all timestamps and status information
     *
     *
     * @param entity the JPA entity to convert, must not be null
     * @return the corresponding {@link Course} domain object
     */
    public static Course toDomain(CourseEntity entity) {
        return Course.fromRepository(
                CourseId.of(entity.getId()),
                entity.getTitle(),
                entity.getDescription(),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.getStatus(),
                UserId.of(entity.getInstructorId()),
                CategoryId.of(entity.getCategoryId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getPublishedAt()
        );
    }

    /**
     * Converts a {@link Course} domain object into a {@link CourseEntity}
     * suitable for persistence.
     *
     * This method extracts the primitive values from the domain model,
     * including:
     *
     * - Unwrapping value objects to their UUID representations
     * - Decomposing the Money value object into separate amount and currency fields
     * - Preserving all domain state including status and timestamps
     *
     *
     * <p>Note: The {@code createdAt} timestamp is not included as it is managed
     * automatically by the {@link BaseEntity} superclass via JPA lifecycle callbacks.
     *
     * @param course the domain object to convert, must not be null
     * @return the corresponding {@link CourseEntity} JPA entity
     */
    public static CourseEntity toEntity(Course course) {
        return new CourseEntity(
                course.getId().value(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice().getAmount(),
                course.getPrice().getCurrency(),
                course.getStatus(),
                course.getInstructorId().value(),
                course.getCategoryId().value(),
                course.getUpdatedAt(),
                course.getPublishedAt()
        );
    }
}
