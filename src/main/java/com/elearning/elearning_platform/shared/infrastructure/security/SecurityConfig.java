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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Basic security configuration for the application.
 *
 * This is a temporary configuration for development that disables
 * security to allow unrestricted access. It will be replaced in
 * Sprint 3 with proper role-based authentication and JWT filtering.
 *
 * Provides:
 *
 *   Disabled CSRF protection (required for REST APIs)
 *   Permit all requests without authentication
 *   BCrypt password encoder for future authentication
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Configures the Spring Security filter chain for the application.
     *
     * Security configuration details:
     * - Disables CSRF protection (useful for stateless REST APIs).
     * - Disables frame options to allow H2 console access.
     * - Defines route authorization rules:
     *     - "/auth/**" and "/h2-console/**" are publicly accessible.
     *     - "/admin/**" requires "ADMIN" role.
     *     - "/instructor/**" requires "INSTRUCTOR" role.
     *     - "/student" requires "STUDENT" role.
     *     - All other requests require authentication.
     * - Configures stateless session management (no HTTP sessions).
     * - Adds JwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter
     *   to handle JWT validation and authentication.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while building the filter chain
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
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

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
