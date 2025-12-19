package com.elearning.elearning_platform.user.application.port.in;

/**
 * Command object for registering a new user.
 *
 * This is a simple Data Transfer Object (DTO) that represents
 * the input required to create a user. It comes from the
 * infrastructure layer (e.g., HTTP request body).
 *
 * Important: The conversion from raw types (like {@link String} role)
 * to domain types (like {@link com.elearning.elearning_platform.user.domain.model.Role})
 * is performed inside the corresponding UseCase. This keeps the
 * domain layer independent and clean.
 *
 * Fields:
 *
 *     {@code email}: the user's email address
 *     {@code password}: the user's raw password
 *     {@code firstName}: the user's first name
 *     {@code lastName}: the user's last name
 *     {@code role}: the user's role represented as a string (converted in the UseCase)</li>
 *
 * @see RegisterUserUseCase
 */
public record RegisterUserCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String role
) {}


