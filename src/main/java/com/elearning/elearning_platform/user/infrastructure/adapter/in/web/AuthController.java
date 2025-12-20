package com.elearning.elearning_platform.user.infrastructure.adapter.in.web;

import com.elearning.elearning_platform.user.application.port.in.LoginCommand;
import com.elearning.elearning_platform.user.application.port.in.RegisterUserCommand;
import com.elearning.elearning_platform.user.application.usecase.LoginUseCase;
import com.elearning.elearning_platform.user.application.usecase.RegisterUserUseCase;
import com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto.LoginRequest;
import com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto.LoginResponse;
import com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for authentication-related operations.
 *
 * This controller acts as an inbound adapter in the hexagonal architecture,
 * translating HTTP requests into application commands and delegating
 * execution to the corresponding use cases.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final Long jwtExpiration;


    /**
     * Creates a new AuthController.
     *
     * @param registerUserUseCase use case responsible for user registration
     * @param loginUseCase        use case responsible for user authentication
     * @param jwtExpiration       JWT expiration time in milliseconds (from configuration)
     */
    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase,
                          @Value("${jwt.expiration}") Long jwtExpiration) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Registers a new user in the system.
     *
     * Receives a {@link RegisterRequest} from the client, converts it
     * into a {@link RegisterUserCommand}, and delegates the execution
     * to the application layer.
     *
     * @param request validated registration request
     * @return HTTP 201 Created if the user is successfully registered
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole()
        );

        registerUserUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * Receives a {@link LoginRequest}, converts it into a {@link LoginCommand},
     * and delegates authentication to the application layer.
     *
     * @param request validated login request
     * @return HTTP 200 OK with a {@link LoginResponse} containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginCommand command = new LoginCommand(
                request.getEmail(),
                request.getPassword()
        );

        String token = loginUseCase.execute(command);

        LoginResponse response = new LoginResponse(
                token,
                "Bearer",
                jwtExpiration / 1000
        );

        return ResponseEntity.ok(response);
    }
}
