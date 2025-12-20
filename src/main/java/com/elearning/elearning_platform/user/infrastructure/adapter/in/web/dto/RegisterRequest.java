package com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * HTTP request DTO used to register a new user.
 *
 * This class represents the data received from the client (e.g. REST API).
 * It belongs to the infrastructure layer and must NOT contain domain logic.
 *
 * Validation annotations ensure basic input correctness before
 * reaching the application layer (UseCases).
 */
public class RegisterRequest {

    /**
     * User email address.
     * Must be non-empty and follow a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Raw user password.
     * Must be at least 8 characters long.
     * Password strength rules are validated again in the domain layer.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * User first name.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * User last name.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * User role.
     * Allowed values are limited to INSTRUCTOR or STUDENT.
     *
     * This is kept as String because it comes from HTTP input.
     * Conversion to the domain Role enum is done inside the UseCase.
     */
    @NotBlank(message = "Role is required")
    @Pattern(
            regexp = "INSTRUCTOR|STUDENT",
            message = "Role must be INSTRUCTOR or STUDENT"
    )
    private String role;

    /**
     * Default constructor required by frameworks (Jackson).
     */
    public RegisterRequest() {}

    /**
     * Full constructor.
     *
     * @param email user email
     * @param password raw password
     * @param firstName first name
     * @param lastName last name
     * @param role user role as string
     */
    public RegisterRequest(String email, String password, String firstName, String lastName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getEmail() {
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

    public String getRole() {
        return role;
    }
}
