package com.elearning.elearning_platform.shared.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtUtil}.
 *
 * Verifies the correct generation, validation, and extraction of information
 * from JWT tokens. Uses Spring Boot test context to load the actual JwtUtil
 * bean with its configuration.
 *
 * @see JwtUtil
 */
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private static final String ROLE = "STUDENT";
    private static final String EMAIL = "test@deepcode.com";

    /**
     * Tests that a JWT token is successfully generated.
     *
     * Verifies that the generated token is not null and not blank.
     */
    @Test
    void testGenerateToken() {
        // When
        String token = jwtUtil.generateToken(EMAIL, ROLE);

        // Then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    /**
     * Tests that the role can be correctly extracted from a JWT token.
     *
     * Generates a token with a specific role and verifies that the
     * extracted role matches the original.
     */
    @Test
    void testExtractRole() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, ROLE);

        // When
        String extractedRole = jwtUtil.extractRole(token);

        // Then
        assertEquals(ROLE, extractedRole);
    }

    /**
     * Tests that a valid token is correctly validated.
     *
     * Generates a token and validates it with the same email used
     * during generation. The validation should return true.
     */
    @Test
    void testValidateToken_Valid() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, ROLE);

        // When
        boolean isValid = jwtUtil.validateToken(token, EMAIL);

        // Then
        assertTrue(isValid);
    }

    /**
     * Tests that token validation fails with a wrong email.
     *
     * Generates a token with one email and attempts to validate it
     * with a different email. The validation should return false.
     */
    @Test
    void testValidToken_WrongEmail() {
        // Given
        String token = jwtUtil.generateToken(EMAIL, ROLE);

        // When
        boolean isValid = jwtUtil.validateToken(token, "wrong@email.com");

        // Then
        assertFalse(isValid);
    }
}








