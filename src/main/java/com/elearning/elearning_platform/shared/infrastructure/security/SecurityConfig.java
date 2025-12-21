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
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security configuration for the application.
 *
 * <p>This configuration enables JWT-based stateless authentication and
 * defines access rules for public and protected endpoints.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Disable session-based authentication</li>
 *     <li>Configure JWT authentication filter</li>
 *     <li>Define role-based access control</li>
 *     <li>Handle authentication and authorization errors</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    /**
     * Constructs the security configuration with required JWT utilities.
     *
     * @param jwtUtil utility class for JWT validation and parsing
     */
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Provides a {@link SecurityContextRepository} suitable for stateless applications.
     *
     * <p>This implementation stores the {@link org.springframework.security.core.context.SecurityContext}
     * as a request attribute, making it available during the filter chain execution
     * without using HTTP sessions.</p>
     *
     * @return a stateless security context repository
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new RequestAttributeSecurityContextRepository();
    }

    /**
     * CORS configuration for development and production.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the Spring Security filter chain.
     *
     * <p>Key configurations:</p>
     * <ul>
     *     <li>Disable CSRF protection (JWT-based stateless API)</li>
     *     <li>Allow access to public endpoints</li>
     *     <li>Restrict access based on user roles</li>
     *     <li>Register {@link JwtAuthenticationFilter} before username/password authentication</li>
     *     <li>Return proper HTTP status codes for security errors</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if a configuration error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF as the API is stateless and uses JWT
                .csrf(csrf -> csrf.disable())

                // Disable frame options to allow H2 console rendering
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )

                // Use a stateless SecurityContextRepository
                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository())
                )

                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/h2-console/**", "/courses").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )

                // Disable HTTP sessions completely
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register JWT authentication filter
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                )

                // Customize security exception handling
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((
                                request,
                                response,
                                authException) -> {
                            // Solo 401 si realmente no hay authentication
                            System.out.println("🚨 AuthenticationEntryPoint - No authentication found");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                        })
                        .accessDeniedHandler((request,
                                              response,
                                              accessDeniedException) -> {
                            // 403 cuando hay authentication pero falta autorización
                            System.out.println("🚨 AccessDeniedHandler - Insufficient privileges");
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient privileges");
                        })
                );



        return http.build();
    }

    /**
     * Password encoder bean used to hash user passwords.
     *
     * <p>Uses BCrypt with strength 12, providing a good balance
     * between security and performance.</p>
     *
     * @return a {@link PasswordEncoder} implementation
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
