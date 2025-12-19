package com.elearning.elearning_platform.user.infrastructure.security;

import com.elearning.elearning_platform.shared.infrastructure.security.JwtUtil;
import com.elearning.elearning_platform.user.application.port.out.TokenGeneratorPort;
import org.springframework.stereotype.Component;

/**
 * JWT-based implementation of the TokenGeneratorPort.
 *
 * This adapter lives in the infrastructure layer and uses JwtUtil
 * to generate JWT tokens. Spring will detect this component
 * and inject it wherever TokenGeneratorPort is required.
 */
@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private final JwtUtil jwtUtil;

    /**
     * Constructor-based dependency injection.
     *
     * @param jwtUtil utility class responsible for low-level JWT generation
     */
    public JwtTokenGeneratorAdapter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Generates a JWT token using the provided email and role.
     *
     * @param email user's email
     * @param role  user's role
     * @return JWT token
     */
    @Override
    public String generateToken(String email, String role) {
        return jwtUtil.generateToken(email, role);
    }
}

