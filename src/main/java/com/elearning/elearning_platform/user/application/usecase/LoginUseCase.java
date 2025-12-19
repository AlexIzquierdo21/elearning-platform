package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.LoginCommand;
import com.elearning.elearning_platform.user.application.port.out.TokenGeneratorPort;
import com.elearning.elearning_platform.user.domain.exception.InvalidCredentialsException;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Use case responsible for authenticating a user and generating a JWT token.
 *
 * This class belongs to the application layer and orchestrates the login process.
 * It depends only on interfaces (ports) and does not directly couple with infrastructure.
 */
@Service
public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;

    /**
     * Constructor with dependency injection.
     *
     * @param userRepository repository port to access user data
     * @param passwordEncoder encoder to verify passwords
     * @param tokenGenerator port to generate authentication tokens
     */
    public LoginUseCase(UserRepositoryPort userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenGeneratorPort tokenGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    /**
     * Executes the login process.
     *
     * Steps:
     * Convert email string from the command into the Email ValueObject.
     * Retrieve the user from the repository by email. If not found, throws InvalidCredentialsException.
     * Verify the user is active. If inactive, throws InvalidCredentialsException.
     * Validate the provided password against the stored hashed password.
     *    If it does not match, throws InvalidCredentialsException.
     * Generate a JWT token using the user's email and role.
     *
     * @param command DTO containing login credentials (email and password)
     * @return a JWT token as a String if login is successful
     * @throws InvalidCredentialsException if email/password are invalid or user is inactive
     */
    public String execute(LoginCommand command) {
        /// Convert email string to Email value object
        Email email = Email.of(command.email());

        /// Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        /// Verify user is active
        if (!user.getActive()) {
            throw new InvalidCredentialsException();
        }

        /// Validate password
        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        /// Generate JWT token using token generator port
        return tokenGenerator.generateToken(
                user.getEmail().getValue(),
                user.getRole().name()
        );
    }
}






















