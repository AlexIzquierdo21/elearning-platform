package com.elearning.elearning_platform.user.application.port.in;

import com.elearning.elearning_platform.user.application.usecase.LoginUseCase;

/**
 * Command object for logging in a user.
 *
 * This is a simple Data Transfer Object (DTO) that represents
 * the input required to authenticate a user. It typically comes
 * from the infrastructure layer, e.g., HTTP request body.
 *
 *
 * The conversion to domain types (if needed) and the verification
 * of credentials is performed inside the corresponding UseCase,
 * keeping the domain layer independent from infrastructure.
 *
 * Fields:
 *     {@code email}: the email address used to identify the user
 *     {@code password}: the raw password submitted for authentication
 *
 * @see LoginUseCase
 */
public record LoginCommand(
        String email,
        String password
) {}

