package com.elearning.elearning_platform.shared.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Basic security configuration for the application.
 * <p>
 * This is a temporary configuration for development that disables
 * security to allow unrestricted access. It will be replaced in
 * Sprint 3 with proper role-based authentication and JWT filtering.
 * </p>
 * <p>
 * Provides:
 * <ul>
 *   <li>Disabled CSRF protection (required for REST APIs)</li>
 *   <li>Permit all requests without authentication</li>
 *   <li>BCrypt password encoder for future authentication</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     *
     * Currently configured to:
     *
     *   Disable CSRF protection (REST API doesn't need it)
     *   Allow all HTTP requests without authentication
     *
     * This will be updated in Sprint 3 to add JWT authentication
     * and role-based authorization.
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    /**
     * Provides a password encoder bean using BCrypt algorithm.
     *
     * BCrypt is a strong password hashing function designed to be slow,
     * making brute-force attacks more difficult. The strength of 12
     * represents 2^12 iterations (4096 rounds).
     *
     * @return BCryptPasswordEncoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
