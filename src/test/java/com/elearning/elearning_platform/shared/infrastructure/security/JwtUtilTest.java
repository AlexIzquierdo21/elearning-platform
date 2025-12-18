package com.elearning.elearning_platform.shared.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    /// Datos de prueba
    private static final String ROLE = "STUDENT";
    private static final String EMAIL = "test@deepcode.com";

    @Test
    void testGenerateToken() {
        /// When
        String token = jwtUtil.generateToken(EMAIL, ROLE);

        /// Then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

}
