package com.elearning.elearning_platform.course.domain.valueobject;

import java.util.UUID;

/**
 * Value object representing a Category's unique identifier.
 */
public record CategoryId(UUID value) {

    /**
     * Generates a new random CategoryId.
     *
     * @return new CategoryId with a random UUID
     */
    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

    /**
     * Creates a CategoryId from an existing UUID.
     *
     * @param value UUID value
     * @return CategoryId wrapping the UUID
     */
    public static CategoryId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CategoryId value cannot be null");
        }
        return new CategoryId(value);
    }

    /**
     * Creates a CategoryId from a string representation.
     *
     * @param value UUID string
     * @return CategoryId
     * @throws IllegalArgumentException if string is not a valid UUID
     */
    public static CategoryId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CategoryId value cannot be null or blank");
        }
        return new CategoryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
