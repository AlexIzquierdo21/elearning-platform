package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.LoginCommand;
import com.elearning.elearning_platform.user.application.port.out.TokenGeneratorPort;
import com.elearning.elearning_platform.user.domain.exception.InvalidCredentialsException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LoginUseCase}.
 *
 * Covers main scenarios:
 * - Happy path (login successfully)
 * - User not found
 * - User inactive
 * - Incorrect password
 */
@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenGeneratorPort tokenGenerator;

    @InjectMocks
    private LoginUseCase loginUseCase;

    /**
     * Helper to create a User instance for tests.
     */
    private User user(String email, String hashedPassword, boolean active) {
        return User.fromRepository(
                UUID.randomUUID(),
                Email.of(email),
                hashedPassword,
                "Alex",
                "Izquierdo",
                Role.STUDENT,
                active
        );
    }

    /**
     * Happy path: login successfully and JWT token generated.
     */
    @Test
    @DisplayName("should login successfully")
    void shouldLoginSuccessfully() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "Password1");

        User user = user(command.email(), "$2a$12$hashedPassword", true);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPassword())).thenReturn(true);
        when(tokenGenerator.generateToken(user.getEmail().getValue(), user.getRole().name()))
                .thenReturn("jwt-token");

        String token = loginUseCase.execute(command);

        assertEquals("jwt-token", token);
        verify(tokenGenerator).generateToken(user.getEmail().getValue(), user.getRole().name());
    }

    /**
     * Throws exception if user is not found.
     */
    @Test
    @DisplayName("should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        LoginCommand command = new LoginCommand("notfound@deepcode.com", "Password1");

        when(userRepository.findByEmail(Email.of(command.email()))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }

    /**
     * Throws exception if user is inactive.
     */
    @Test
    @DisplayName("should throw exception when user is inactive")
    void shouldThrowExceptionWhenUserIsInactive() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "Password1");

        User inactiveUser = user(command.email(), "$2a$12$hashedPassword", false);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(inactiveUser));

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }

    /**
     * Throws exception if password is incorrect.
     */
    @Test
    @DisplayName("should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "WrongPassword");

        User user = user(command.email(), "$2a$12$hashedPassword", true);

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }
}


