package com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used to receive login requests
 * from the HTTP layer.
 *
 * This class represents the raw input provided by the client
 * when attempting to authenticate. It belongs to the web
 * (inbound adapter) layer and must not contain any business logic.
 *
 * Validation annotations ensure that invalid data is rejected
 * as early as possible, before reaching the application layer.
 */
public class LoginRequest {

    /**
     * User email address.
     *
     * Must be present and follow a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * User password in plain text.
     *
     * Must be present and have a minimum length.
     * The password will be validated and compared
     * in the application/domain layers.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    /**
     * Default constructor required by frameworks
     * such as Spring for object deserialization.
     */
    public LoginRequest() {
    }

    /**
     * Convenience constructor for manual instantiation,
     * mainly useful in tests.
     *
     * @param email    user email
     * @param password user password
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Returns the email provided in the login request.
     *
     * @return user email as a String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password provided in the login request.
     *
     * @return user password as a String
     */
    public String getPassword() {
        return password;
    }
}
