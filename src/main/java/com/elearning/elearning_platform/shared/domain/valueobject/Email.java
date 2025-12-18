package com.elearning.elearning_platform.shared.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a valid email address.
 *
 * This is an immutable object that guarantees the email format is valid
 * according to a predefined pattern. Emails are automatically normalized
 * to lowercase for consistency.
 *
 * Instances must be created using the static factory method {@link #of(String)}.
 *
 * @throws IllegalArgumentException if the email format is invalid or empty
 */
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    /**
     * Private constructor that validates and creates an Email instance.
     *
     * Performs two validations:
     * 1. Ensures the value is not null or blank
     * 2. Validates the email format against the EMAIL_PATTERN regex
     *
     * The email is normalized to lowercase for consistency.
     *
     * @param value the raw email string to validate
     * @throws IllegalArgumentException if validation fails
     */
    private Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }
        this.value = value.toLowerCase();
    }

    /**
     * Creates a new Email value object from the given string.
     *
     * The email is validated and normalized to lowercase.
     *
     * @param value the email address as a string
     * @return a new Email instance
     * @throws IllegalArgumentException if the email is null, empty, or has an invalid format
     */
     public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Returns the email address as a string.
     *
     * @return the email value in lowercase
     */
    public String getValue() {
        return value;
    }

    /**
     * Compares this Email with another object for equality.
     *
     * Two Email objects are considered equal if they have the same
     * normalized email value (case-insensitive).
     *
     * @param o the object to compare with
     * @return true if both emails have the same value, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if( !(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
    }

    /**
     * Returns the hash code of this Email.
     *
     * Uses the hash code of the underlying email value string.
     * Consistent with equals() implementation.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode(){
        return Objects.hash(value);
    }

    /**
     * Returns the string representation of this Email.
     *
     * Returns the normalized email value directly (lowercase).
     *
     * @return the email address as a string
     */
    @Override
    public String toString() {
        return value;
    }
}


















