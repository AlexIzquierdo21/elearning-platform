package com.elearning.elearning_platform.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

/**
 * Spring Security configuration for JWT-based authentication and authorization.
 *
 * <p>This configuration establishes a stateless REST API security model with:
 * <ul>
 *   <li>JWT token authentication via custom filter</li>
 *   <li>Role-based access control (RBAC) for protected endpoints</li>
 *   <li>Public access to authentication and development endpoints</li>
 *   <li>Proper HTTP status codes for authentication/authorization failures</li>
 * </ul>
 *
 * <p>Security is enforced through a custom {@link JwtAuthenticationFilter} that
 * validates JWT tokens and loads user authorities into the security context.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Constructs the security configuration with required dependencies.
     *
     * @param jwtUtil utility for JWT token operations
     */
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configures the Spring Security filter chain with JWT authentication
     * and role-based authorization.
     *
     * <p><strong>Route Authorization:</strong>
     * <ul>
     *   <li>{@code /auth/**} - Public (registration, login)</li>
     *   <li>{@code /h2-console/**} - Public (development only)</li>
     *   <li>{@code /admin/**} - Requires ADMIN role</li>
     *   <li>{@code /instructor/**} - Requires INSTRUCTOR role</li>
     *   <li>{@code /student/**} - Requires STUDENT role</li>
     *   <li>All other routes - Requires authentication</li>
     * </ul>
     *
     * <p><strong>Security Features:</strong>
     * <ul>
     *   <li>CSRF disabled (stateless API)</li>
     *   <li>Frame options disabled (H2 console support)</li>
     *   <li>Stateless session management</li>
     *   <li>Custom authentication/authorization error handlers</li>
     *   <li>JWT filter integrated into security chain</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")
                        )
                )
                .securityContext(context -> context
                        .requireExplicitSave(false)
                )
                .addFilterAfter(
                        new JwtAuthenticationFilter(jwtUtil),
                        SecurityContextHolderFilter.class
                );

        return http.build();
    }

    /**
     * Provides a BCrypt password encoder for secure password hashing.
     *
     * <p>BCrypt is a deliberately slow hashing algorithm designed to resist
     * brute-force attacks. The configured strength of 12 represents 2^12
     * (4096) hashing rounds, providing a balance between security and
     * performance for typical authentication workloads.
     *
     * @return BCrypt password encoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
