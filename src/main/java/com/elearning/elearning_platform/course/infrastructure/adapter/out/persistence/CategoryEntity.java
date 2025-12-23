package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * JPA entity representing the Category table.
 *
 * This entity is the persistence model for course categories and is used
 * exclusively by the infrastructure layer. It maps directly to the
 * {@code categories} table in the database.
 */
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    /**
     * Primary key of the category.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Unique name of the category.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Optional description of the category.
     */
    @Column
    private String description;

    /**
     * URL-friendly unique slug generated from the category name.
     */
    @Column(nullable = false, unique = true)
    private String slug;

    /**
     * Indicates whether the category is active.
     *
     * Defaults to {@code true}.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Default no-args constructor required by JPA.
     */
    protected CategoryEntity() {
    }

    /**
     * Constructs a CategoryEntity with the provided values.
     *
     * @param name the category name
     * @param description the category description
     * @param slug the URL-friendly slug
     * @param active whether the category is active
     */
    public CategoryEntity(
            String name,
            String description,
            String slug,
            Boolean active
    ) {
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.active = active;
    }

    public UUID getId() {
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
}
