package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.course.domain.model.Category;
import com.elearning.elearning_platform.course.domain.port.out.CategoryRepositoryPort;
import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA adapter implementation of {@link CategoryRepositoryPort}.
 *
 * This adapter acts as a bridge between the domain layer and the
 * infrastructure persistence layer, translating domain operations into
 * JPA repository calls and converting between domain models and JPA entities.
 *
 * Following hexagonal architecture principles, this adapter allows the
 * domain layer to remain independent of persistence implementation details.
 */
@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final JpaCategoryRepository jpaCategoryRepository;

    /**
     * Constructs a new CategoryRepositoryAdapter.
     *
     * @param jpaCategoryRepository the Spring Data JPA repository for category persistence
     */
    public CategoryRepositoryAdapter(JpaCategoryRepository jpaCategoryRepository) {
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    /**
     * Persists a category to the database.
     *
     * @param category the domain category to save
     * @return the saved category with updated persistence metadata
     */
    @Override
    public Category save(Category category) {
        CategoryEntity entity = CategoryMapper.toEntity(category);
        CategoryEntity saved = jpaCategoryRepository.save(entity);
        return CategoryMapper.toDomain(saved);
    }

    /**
     * Finds a category by its unique identifier.
     *
     * @param id the category identifier
     * @return an Optional containing the category if found, empty otherwise
     */
    @Override
    public Optional<Category> findById(CategoryId id) {
        return jpaCategoryRepository.findById(id.value())
                .map(CategoryMapper::toDomain);
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of all categories
     */
    @Override
    public List<Category> findAll() {
        List<CategoryEntity> categoryEntities = jpaCategoryRepository.findAll();
        return categoryEntities.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Finds categories filtered by their active status.
     *
     * @param active true to find active categories, false for inactive
     * @return a list of categories matching the active status
     */
    @Override
    public List<Category> findByActive(boolean active) {
        List<CategoryEntity> categoryEntities = jpaCategoryRepository.findByActive(active);
        return categoryEntities.stream()
                .map(CategoryMapper::toDomain)
                .collect(Collectors.toList());  // ← AÑADIR ESTO
    }

    /**
     * Finds a category by its unique slug.
     *
     * @param slug the URL-friendly category slug
     * @return an Optional containing the category if found, empty otherwise
     */
    @Override
    public Optional<Category> findBySlug(String slug) {
        Optional<CategoryEntity> categoryEntityOptional = jpaCategoryRepository.findBySlug(slug);
        return categoryEntityOptional.map(CategoryMapper::toDomain);
    }

    /**
     * Checks whether a category exists with the given identifier.
     *
     * @param id the category identifier to check
     * @return true if a category exists, false otherwise
     */
    @Override
    public boolean existsById(CategoryId id) {
        return jpaCategoryRepository.existsById(id.value());
    }
}



















