package com.elearning.elearning_platform.user.domain.model;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link User}.
 *
 * These tests verify the business rules and invariants defined
 * in the User domain entity.
 *
 *
 * This is a pure unit test:
 *
 *   No Spring context
 *   No infrastructure dependencies
 *   Only domain logic is validated
 *
 * The goal of these tests is to ensure that the User entity
 * enforces its business rules consistently and safely.
 */
public class UserTest {

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        Email email = Email.of("test@deepcode.com");
        String password = "Password1";
        String firstName = "Alex";
        String lastName = "Izquierdo";
        Role role = Role.STUDENT;

        // When
        User user = User.create(email, password, firstName, lastName, role);

        // Then
        assertNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(role, user.getRole());
        assertTrue(user.getActive());
    }

    /**
     * Verifies that user creation fails when the email is null.
     *
     * Email is a mandatory business identifier and must
     * always be provided.
     */
    @Test
    void shouldFailWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                User.create(
                        null,
                        "Password1",
                        "Alex",
                        "Izquierdo",
                        Role.STUDENT
                )
        );
    }

    /**
     * Verifies that user creation fails when the password
     * does not meet the minimum length requirement.
     */
    @Test
    void shouldFailWhenPasswordIsTooShort() {
        Email email = Email.of("test@deepcode.com");

        assertThrows(IllegalArgumentException.class, () ->
                User.create(
                        email,
                        "Pass1",
                        "Alex",
                        "Izquierdo",
                        Role.STUDENT
                )
        );
    }

    /**
     * Verifies that user creation fails when the password
     * does not contain any uppercase letters.
     */
    @Test
    void shouldFailWhenPasswordHasNoUppercase() {
        Email email = Email.of("test@deepcode.com");

        assertThrows(IllegalArgumentException.class, () ->
                User.create(
                        email,
                        "password1",
                        "Alex",
                        "Izquierdo",
                        Role.STUDENT
                )
        );
    }

    /**
     * Verifies that user creation fails when the password
     * does not contain any numeric digits.
     */
    @Test
    void shouldFailWhenPasswordHasNoDigit() {
        Email email = Email.of("test@deepcode.com");

        assertThrows(IllegalArgumentException.class, () ->
                User.create(
                        email,
                        "Password",
                        "Alex",
                        "Izquierdo",
                        Role.STUDENT
                )
        );
    }

    /**
     * Verifies that two users with the same email
     * are considered equal.
     *
     * This confirms that equality is based on the
     * business identifier (email) rather than
     * technical identifiers like database ids.
     */
    @Test
    void usersWithSameEmailShouldBeEqual() {
        Email email = Email.of("same@deepcode.com");

        User user1 = User.create(
                email,
                "Password1",
                "Alex",
                "One",
                Role.STUDENT
        );

        User user2 = User.create(
                email,
                "Password1",
                "Alex",
                "Two",
                Role.INSTRUCTOR
        );

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    /**
     * Verifies that user creation fails when the password
     * does not contain any lowercase letters.
     *
     * Passwords composed only of uppercase characters are
     * not considered strong enough according to the
     * business security rules.
     */
    @Test
    void shouldFailWhenPasswordHasNoLowercase() {
        Email email = Email.of("test@deepcode.com");  // Usa Email.of()

        assertThrows(IllegalArgumentException.class, () ->
                User.create(
                        email,
                        "PASSWORD1",  // Sin minúsculas
                        "Alex",
                        "Izquierdo",
                        Role.STUDENT
                )
        );
    }
}
