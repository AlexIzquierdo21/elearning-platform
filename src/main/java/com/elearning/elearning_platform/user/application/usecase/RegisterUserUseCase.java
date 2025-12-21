package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.RegisterUserCommand;
import com.elearning.elearning_platform.user.domain.exception.DuplicateEmailException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import com.elearning.elearning_platform.user.domain.valueobject.UserId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Use case for registering a new user in the system.
 *
 * This use case orchestrates the following operations:
 *
 *   Validates the email format and checks for duplicates
 *   Validates the password strength according to business rules
 *   Hashes the password using BCrypt
 *   Creates and persists the new user
 *
 * This is part of the application layer in Hexagonal Architecture,
 * coordinating domain logic and infrastructure services.
 *
 * @see RegisterUserCommand
 * @see User
 * @see UserRepositoryPort
 */
@Service
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the use case with required dependencies.
     *
     * @param userRepository repository for user persistence operations
     * @param passwordEncoder encoder for hashing passwords (BCrypt)
     */
    public RegisterUserUseCase(UserRepositoryPort userRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes the user registration process.
     *
     * Validates input, checks for duplicate emails, hashes the password,
     * and persists the new user to the repository.
     *
     * @param command the registration command containing user data
     * @return the created and persisted {@link User} with assigned ID
     * @throws IllegalArgumentException if email format is invalid, password is weak, or role is invalid
     * @throws DuplicateEmailException if a user with the given email already exists
     */
    public UserId execute(RegisterUserCommand command) {
        /// String email converted to Email ValueObject
        Email email = Email.of(command.email());
        /// Email does not exist verification
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        /// role String converted to Role enum
        Role role;
        try {
            role = Role.valueOf(command.role().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid role: " + command.role());
        }
        /// Password validation
        User.validateRawPassword(command.password());

        /// Hashed Password
        String hashedPassword = passwordEncoder.encode(command.password());

        /// User Creation
        User user = User.create(email, hashedPassword, command.firstName(),
                command.lastName(), role);

        /// Saved user
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}















