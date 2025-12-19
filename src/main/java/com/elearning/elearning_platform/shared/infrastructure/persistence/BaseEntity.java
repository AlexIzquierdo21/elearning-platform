package com.elearning.elearning_platform.shared.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Base entity class for JPA entities.
 *
 * Provides automatic auditing fields for creation and update timestamps.
 * All entities extending this class will inherit these columns.
 *
 * - createdAt: timestamp when the entity was first persisted
 * - updatedAt: timestamp when the entity was last updated
 *
 * Uses Hibernate annotations:
 * - {@link CreationTimestamp} to automatically set creation time
 * - {@link UpdateTimestamp} to automatically update on modifications
 */
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * Timestamp when the entity was created.
     * Not nullable, and cannot be updated once set.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated.
     * Automatically updated whenever the entity is modified.
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Returns the creation timestamp.
     *
     * @return the date and time when the entity was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the last update timestamp.
     *
     * @return the date and time when the entity was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
