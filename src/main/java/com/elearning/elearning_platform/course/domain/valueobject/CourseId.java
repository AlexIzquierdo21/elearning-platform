package com.elearning.elearning_platform.course.domain.valueobject;

import java.util.UUID;

/**
 * Value object representing a Course's unique identifier.
 *
 * The CourseId serves as a strongly-typed identifier for courses, ensuring
 * type safety and encapsulating the raw UUID value. It provides utility
 * methods for creating CourseId instances from UUIDs or strings, as well
 * as generating new random identifiers.
 */
public record CourseId(UUID value) {

    /**
     * Generates a new random CourseId.
     *
     * @return a new CourseId with a random UUID
     */
    public static CourseId generate() {
        return new CourseId(UUID.randomUUID());
    }

    /**
     * Creates a CourseId from an existing UUID.
     *
     * @param value the UUID value to be wrapped in a CourseId, must not be null
     * @return a new CourseId instance containing the provided UUID
     * @throws IllegalArgumentException if the provided UUID is null
     */
    public static CourseId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("CourseId value cannot be null");
        }
        return new CourseId(value);
    }

    /**
     * Creates a CourseId from a string representation of a UUID.
     *
     * @param value the string representation of the UUID, must not be null or blank
     * @return a new CourseId instance containing the provided UUID
     * @throws IllegalArgumentException if the provided string is null, blank, or not a valid UUID
     */
    public static CourseId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CourseId value cannot be null or blank");
        }
        return new CourseId(UUID.fromString(value));
    }

    /**
     * Returns the string representation of the encapsulated UUID value.
     *
     * @return the string representation of the UUID value
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
