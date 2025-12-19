package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.RegisterUserCommand;
import com.elearning.elearning_platform.user.domain.exception.DuplicateEmailException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    /**
     * Happy path: registra un usuario correctamente
     */
    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterUserCommand command = new RegisterUserCommand(
                "test@deepcode.com",
                "Password1",
                "Alex",
                "Izquierdo",
                "STUDENT"
        );

        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("$2a$12$hashedPassword");

        // Capturar el User que se pasa a save()
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        registerUserUseCase.execute(command);

        // Then - verificar el User capturado
        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser);
        assertEquals(command.firstName(), savedUser.getFirstName());
        assertEquals(command.lastName(), savedUser.getLastName());
        assertEquals(Role.STUDENT, savedUser.getRole());
        assertEquals("$2a$12$hashedPassword", savedUser.getPassword());

        verify(userRepository).existsByEmail(any(Email.class));
        verify(passwordEncoder).encode(command.password());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Lanza excepción cuando el email ya existe
     */
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterUserCommand command = new RegisterUserCommand(
                "test@deepcode.com",
                "Password1",
                "Alex",
                "Izquierdo",
                "STUDENT"
        );

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Lanza excepción cuando el rol no es válido
     */
    @Test
    void shouldThrowExceptionWhenRoleIsInvalid() {
        RegisterUserCommand command = new RegisterUserCommand(
                "test@deepcode.com",
                "Password1",
                "Alex",
                "Izquierdo",
                "INVALID_ROLE"
        );

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Lanza excepción cuando la contraseña es débil
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsWeak() {
        RegisterUserCommand command = new RegisterUserCommand(
                "test@deepcode.com",
                "weak",
                "Alex",
                "Izquierdo",
                "STUDENT"
        );

        when(userRepository.existsByEmail(Email.of(command.email()))).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> registerUserUseCase.execute(command));
        verify(userRepository, never()).save(any(User.class));
    }
}

