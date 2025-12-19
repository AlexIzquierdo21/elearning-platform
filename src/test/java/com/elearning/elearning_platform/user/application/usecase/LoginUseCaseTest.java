package com.elearning.elearning_platform.user.application.usecase;

import com.elearning.elearning_platform.shared.domain.valueobject.Email;
import com.elearning.elearning_platform.user.application.port.in.LoginCommand;
import com.elearning.elearning_platform.user.application.port.out.TokenGeneratorPort;
import com.elearning.elearning_platform.user.domain.exception.InvalidCredentialsException;
import com.elearning.elearning_platform.user.domain.model.Role;
import com.elearning.elearning_platform.user.domain.model.User;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
     * Happy path: login exitoso y token generado
     */
    @Test
    void shouldLoginSuccessfully() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "Password1");

        // Usa fromRepository en lugar de create
        User user = User.fromRepository(
                1L,
                Email.of(command.email()),
                "$2a$12$hashedPassword",  // Password ya hasheado
                "Alex",
                "Izquierdo",
                Role.STUDENT,
                true
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPassword())).thenReturn(true);
        when(tokenGenerator.generateToken(user.getEmail().getValue(), user.getRole().name()))
                .thenReturn("jwt-token");

        String token = loginUseCase.execute(command);

        assertEquals("jwt-token", token);
    }

    /**
     * Lanza excepción si el usuario no existe
     */
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        LoginCommand command = new LoginCommand("notfound@deepcode.com", "Password1");

        when(userRepository.findByEmail(Email.of(command.email()))).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }

    /**
     * Lanza excepción si el usuario está desactivado
     */
    @Test
    void shouldThrowExceptionWhenUserIsInactive() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "Password1");

        // Directamente crear usuario inactivo
        User inactiveUser = User.fromRepository(
                1L,
                Email.of(command.email()),
                "$2a$12$hashedPassword",
                "Alex",
                "Izquierdo",
                Role.STUDENT,
                false  // inactive
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(inactiveUser));

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }

    /**
     * Lanza excepción si la contraseña es incorrecta
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        LoginCommand command = new LoginCommand("test@deepcode.com", "WrongPassword");

        User user = User.fromRepository(
                1L,
                Email.of("test@deepcode.com"),
                "$2a$12$hashedPassword",
                "Alex",
                "Izquierdo",
                Role.STUDENT,
                true
        );

        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(command.password(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> loginUseCase.execute(command));
    }
}

