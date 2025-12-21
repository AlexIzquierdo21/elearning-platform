package com.elearning.elearning_platform.user.domain.model;

import com.elearning.elearning_platform.shared.domain.exception.ValidationException;
import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;

import java.util.Objects;

/**
 * Domain entity that represents a User within the system.
 *
 * This class is a pure domain model (POJO) and contains no persistence
 * or framework-specific annotations.
 *
 * The User entity encapsulates core business rules related to user
 * creation and identity, enforcing invariants such as password
 * validity and email uniqueness.
 */
public class User {

    private final UserId id;
    private final Email email;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final Role role;
    private final Boolean active;

    /**
     * Full private constructor.
     *
     * Used internally and for rehydrating existing users
     * from persistence storage.
     */
    private User(UserId id,
                Email email,
                String password,
                String firstName,
                String lastName,
                Role role,
                Boolean active
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.active = active;
    }

    /**
     * Factory method for creating new users.
     *
     * Applies all business validations required for user creation
     * and returns a new User instance without an assigned id.
     */
    public static User create(
            Email email,
            String password,
            String firstName,
            String lastName,
            Role role
    ) {
        if (email == null) {
            throw new ValidationException("Email must not be null");
        }
        if (password == null) {
            throw new ValidationException("Password must not be null");
        }
        if (firstName == null) {
            throw new ValidationException("First name must not be null");
        }
        if (lastName == null) {
            throw new ValidationException("Last name must not be null");
        }
        if (role == null) {
            throw new ValidationException("Role must not be null");
        }

        validateRawPassword(password);

        return new User(
                UserId.generate(),
                email,
                password,
                firstName,
                lastName,
                role,
                true
        );
    }

    /**
     * Validates password strength according to business rules.
     */
    public static void validateRawPassword(String password) {
        if (password.length() < 8) {
            throw new ValidationException(
                    "Password must be at least 8 characters long"
            );
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            throw new ValidationException(
                    "Password must contain at least one uppercase letter"
            );
        }
        if (password.chars().noneMatch(Character::isLowerCase)) {
            throw new ValidationException(
                    "Password must contain at least one lowercase letter"
            );
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            throw new ValidationException(
                    "Password must contain at least one digit"
            );
        }
    }

    // Getters

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }

    /**
     * Users are compared by their business identifier (email).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    /**
     * Factory method for loading existing users from persistence.
     * Used when retrieving users from the database.
     *
     * @param id the user's database ID
     * @param email the user's email
     * @param password the user's hashed password
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param role the user's role
     * @param active whether the user is active
     * @return a User instance loaded from persistence
     */
    public static User fromRepository(UserId id, Email email, String password,
                                      String firstName, String lastName,
                                      Role role, boolean active) {
        return new User(id, email, password, firstName, lastName, role, active);
    }
}
