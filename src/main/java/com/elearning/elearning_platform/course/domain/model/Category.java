package com.elearning.elearning_platform.course.domain.model;

import com.elearning.elearning_platform.course.domain.valueobject.CategoryId;
import com.elearning.elearning_platform.shared.domain.exception.ValidationException;

import java.time.LocalDateTime;

public class Category {

    /**
     * The unique identifier for the category.
     *
     * This field holds a {@link CategoryId} representing the primary identifier
     * for a category instance. It is immutable and required for distinguishing
     * one category from another within the system.
     *
     * The {@link CategoryId} encapsulates a UUID to ensure global uniqueness.
     */
    private final CategoryId id;
    private String name;
    private String description;
    private String slug;
    private Boolean active;
    private final LocalDateTime createdAt;

    public Category(CategoryId id,
                    String name,
                    String description,
                    String slug,
                    boolean active,
                    LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.active = active;
        this.createdAt = createdAt;
    }

    /**
     * Creates a new Category instance based on the provided name and description.
     * Validates the input parameters to ensure they meet the required constraints:
     * - The name must not be null, blank, or exceed 100 characters.
     * - The description must not exceed 500 characters.
     *
     * @param name the name of the category; must be non-null, non-blank, and no more than 100 characters
     * @param description a brief description of the category; can be null but must not exceed 500 characters
     * @return a new Category instance initialized with a unique identifier, slug, and current timestamp
     * @throws ValidationException if the name is null, blank, exceeds 100 characters,
     *                             or if the description exceeds 500 characters
     */
    public static Category create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Category name cannot be empty");
        }
        if (name.length() > 100) {
            throw new ValidationException("Category name cannot exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new ValidationException("Category description cannot exceed 500 characters");
        }

        CategoryId id = CategoryId.generate();
        String slug = generateSlug(name);

        return new Category(id,
                name,
                description,
                slug,
                true,
                LocalDateTime.now()
        );
    }

    /**
     * Generates a URL-friendly slug from the category name.
     */
    private static String generateSlug(String name) {
        return name
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /// Getters
    public CategoryId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSlug() {
        return slug;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the category is currently active.
     *
     * @return true if the category is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activates the category, making it available for use.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivates the category, making it unavailable for use.
     */
    public void deactivate() {
        this.active = false;
    }

}

















