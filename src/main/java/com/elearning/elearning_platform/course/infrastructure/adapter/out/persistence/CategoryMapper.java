package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Category;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;

/**
 * Mapper class for converting between {@link Category} domain objects
 * and {@link CategoryEntity} JPA entities.
 *
 * This mapper provides bidirectional transformation to maintain a clear
 * separation between the domain layer and the infrastructure persistence layer,
 * following hexagonal architecture principles.
 *
 * All methods are static utility methods, and the class cannot be instantiated.
 */
public class CategoryMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private CategoryMapper() {}

    /**
     * Converts a {@link CategoryEntity} from the persistence layer
     * into a {@link Category} domain object.
     *
     * This method reconstructs the domain model from the database representation,
     * including converting the UUID identifier into a {@link CategoryId} value object.
     *
     * @param entity the JPA entity to convert, must not be null
     * @return the corresponding {@link Category} domain object
     */
    public static Category toDomain(CategoryEntity entity) {
        return Category.fromRepository(
                CategoryId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                entity.getActive(),
                entity.getCreatedAt()
        );
    }

    /**
     * Converts a {@link Category} domain object into a {@link CategoryEntity}
     * suitable for persistence.
     *
     * This method extracts the primitive values from the domain model,
     * including unwrapping the {@link CategoryId} value object to its UUID representation.
     *
     * Note: The {@code createdAt} timestamp is not included as it is managed
     * automatically by the {@link BaseEntity} superclass via JPA lifecycle callbacks.
     *
     * @param category the domain object to convert, must not be null
     * @return the corresponding {@link CategoryEntity} JPA entity
     */
    public static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.getId().value(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getActive()
        );
    }
}
