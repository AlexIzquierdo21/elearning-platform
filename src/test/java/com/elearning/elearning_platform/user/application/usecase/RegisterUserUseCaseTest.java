package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.RegisterUserCommand;
import com.elearning.elearning_platform.user.domain.exception.DuplicateEmailException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RegisterUserUseCase}.
 *
 * Covers all main scenarios:
 * - Happy path (user registered successfully)
 * - Duplicate email
 * - Invalid role
 * - Weak password
 */
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    /**
     * Helper to build RegisterUserCommand instances
     */
    private RegisterUserCommand command(String email, String password, String role) {
        return new RegisterUserCommand(
                email,
                password,
                "Alex",
                "Izquierdo",
                role
        );
    }

    /**
     * Happy path: registers a user successfully.
     */
    @Test
    @DisplayName("should register user successfully")
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = command("test@deepcode.com", "Password1", "STUDENT");

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("$2a$12$hashedPassword");

        // Capture the User passed to save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        registerUserUseCase.execute(command);

        // Then - verify captured User
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals("Alex", savedUser.getFirstName());
        assertEquals("Izquierdo", savedUser.getLastName());
        assertEquals(Role.STUDENT, savedUser.getRole());
        assertEquals("$2a$12$hashedPassword", savedUser.getPassword());

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode(command.password());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Throws exception when email already exists.
     */
    @Test
    @DisplayName("should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserCommand command = command("test@deepcode.com", "Password1", "STUDENT");

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Throws exception when role is invalid.
     */
    @Test
    @DisplayName("should throw exception when role is invalid")
    void shouldThrowExceptionWhenRoleIsInvalid() {
        RegisterUserCommand command = command("test@deepcode.com", "Password1", "INVALID_ROLE");

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Throws exception when password is weak.
     */
    @Test
    @DisplayName("should throw exception when password is weak")
    void shouldThrowExceptionWhenPasswordIsWeak() {
        RegisterUserCommand command = command("test@deepcode.com", "weak", "STUDENT");

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }
}

