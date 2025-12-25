package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link CategoryEntity}.
 *
 * This repository provides database access operations for category persistence,
 * leveraging Spring Data JPA to automatically generate queries based on
 * method naming conventions.
 *
 * It is used internally by the {@code JpaCategoryRepositoryAdapter} to fulfill
 * the {@code CategoryRepositoryPort} contract defined in the domain layer.
 *
 */
public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, UUID> {

    /**
     * Finds a category by its unique slug.
     *
     * @param slug the URL-friendly unique identifier of the category
     * @return an {@link Optional} containing the category if found, or empty otherwise
     */
    Optional<CategoryEntity> findBySlug(String slug);

    /**
     * Retrieves all categories filtered by their active status.
     *
     * @param active {@code true} to fetch only active categories,
     *               {@code false} to fetch inactive categories
     * @return a list of categories matching the given active state
     */
    List<CategoryEntity> findByActive(Boolean active);
}

