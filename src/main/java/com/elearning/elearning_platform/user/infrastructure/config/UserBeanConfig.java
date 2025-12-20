package com.elearning.elearning_platform.user.infrastructure.config;

import com.elearning.elearning_platform.user.application.usecase.LoginUseCase;
import com.elearning.elearning_platform.user.application.usecase.RegisterUserUseCase;
import com.elearning.elearning_platform.user.application.port.out.TokenGeneratorPort;
import com.elearning.elearning_platform.user.domain.port.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring configuration class responsible for declaring
 * User-related application layer beans.
 *
 * This class explicitly wires use cases with their required
 * dependencies, keeping the application layer free from
 * framework-specific concerns.
 *
 * Although use cases are annotated with {@code @Service},
 * this configuration ensures clear and explicit dependency
 * resolution following hexagonal architecture principles.
 */
@Configuration
public class UserBeanConfig {

    /**
     * Creates the {@link RegisterUserUseCase} bean.
     *
     * Dependencies are automatically resolved by Spring:
     * - {@link UserRepositoryPort} is implemented by {@code JpaUserRepositoryAdapter}
     * - {@link PasswordEncoder} is provided by the security configuration
     *
     * @param userRepository repository port for user persistence
     * @param passwordEncoder encoder used to hash user passwords
     * @return configured {@link RegisterUserUseCase} instance
     */
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return new RegisterUserUseCase(userRepository, passwordEncoder);
    }

    /**
     * Creates the {@link LoginUseCase} bean.
     *
     * Dependencies are automatically resolved by Spring:
     * - {@link UserRepositoryPort} is implemented by {@code JpaUserRepositoryAdapter}
     * - {@link PasswordEncoder} is provided by the security configuration
     * - {@link TokenGeneratorPort} is implemented by {@code JwtTokenGeneratorAdapter}
     *
     * @param userRepository repository port for user persistence
     * @param passwordEncoder encoder used to verify passwords
     * @param tokenGenerator port used to generate authentication tokens
     * @return configured {@link LoginUseCase} instance
     */
    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            TokenGeneratorPort tokenGenerator
    ) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenGenerator);
    }
}

