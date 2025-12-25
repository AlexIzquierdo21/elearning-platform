package com.elearning.elearning_platform.course.infrastructure.adapter.out.persistence;

import com.elearning.elearning_platform.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * JPA entity representing a category in the persistence layer.
 *
 * This entity maps to the "categories" table in the database and serves
 * as the infrastructure representation of a Category domain model.
 *
 * The ID is not auto-generated; it is provided by the domain layer
 * through the CategoryId value object.
 */
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @Id
    private UUID id;  // ← SIN @GeneratedValue

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, unique = true, length = 150)
    private String slug;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Default no-args constructor required by JPA.
     */
    public CategoryEntity() {
    }

    /**
     * Full constructor for creating a CategoryEntity with all fields.
     *
     * @param id unique identifier
     * @param name category name
     * @param description category description
     * @param slug URL-friendly slug
     * @param active whether the category is active
     */
    public CategoryEntity(UUID id, String name,
                          String description, String slug,
                          Boolean active
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slug = slug;
        this.active = active;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}