package com.elearning.elearning_platform.user.domain.valueobject;

import java.util.UUID;

/**
 * Value object representing a User's unique identifier.
 *
 * Encapsulates a UUID to provide type safety and prevent mixing
 * different entity identifiers throughout the application.
 */
public record UserId(UUID value) {

    /**
     * Generates a new random UserId.
     *
     * @return new UserId with random UUID
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Creates a UserId from an existing UUID.
     *
     * @param value UUID value
     * @return UserId wrapping the UUID
     * @throws IllegalArgumentException if value is null
     */
    public static UserId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId value cannot be null");
        }
        return new UserId(value);
    }

    /**
     * Creates a UserId from a string representation.
     *
     * @param value UUID string
     * @return UserId
     * @throws IllegalArgumentException if string is not a valid UUID
     */
    public static UserId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId value cannot be null or blank");
        }
        return new UserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
