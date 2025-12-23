package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Category;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;

public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryEntity toEntity(Category category) {
        return new CategoryEntity(
                category.getId().value(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getActive()
        );
    }

    public static Category toDomain (CategoryEntity entity) {
        return Category.fromRepository(
                CategoryId.of(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getSlug(),
                entity.getActive(),
                entity.getCreatedAt()
        );
    }
}
