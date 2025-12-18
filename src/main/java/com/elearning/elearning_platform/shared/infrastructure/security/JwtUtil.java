package com.elearning.elearning_platform.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for JWT (JSON Web Token) operations.
 *
 * Provides methods to generate, validate, and extract information from JWT tokens.
 * Tokens are signed using HMAC-SHA algorithm with a secret key configured in
 * application properties.
 *
 * Each token contains:
 *
 *   Subject: user's email
 *   Custom claim "role": user's role (ADMIN, INSTRUCTOR, STUDENT)
 *   Issued at: token creation timestamp
 *   Expiration: token expiration timestamp
 *
 * @see io.jsonwebtoken.Jwts
 * @see SecretKey
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Constructs a JwtUtil instance and initializes the secret key.
     *
     * The secret key is converted from a UTF-8 string to a SecretKey
     * suitable for HMAC-SHA signing. The key must be at least 256 bits
     * (32 characters) long for HS256 algorithm.
     *
     * @param secret the secret key string from application properties
     * @param expiration the token expiration time in milliseconds
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        // Usar el secret directamente sin decodificar Base64
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expiration;
    }

    /**
     * Generates a JWT token for an authenticated user.
     *
     * The token includes the user's email as the subject and their role
     * as a custom claim. It is signed with the secret key and has an
     * expiration time configured in application properties.
     *
     * @param email the user's email (used as token subject)
     * @param role the user's role (ADMIN, INSTRUCTOR, STUDENT)
     * @return a signed JWT token as a String
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * Parses and verifies the token's signature using the secret key.
     * If the token is invalid or expired, this method will throw an exception.
     *
     * @param token the JWT token to parse
     * @return the claims (payload) contained in the token
     * @throws io.jsonwebtoken.JwtException if the token is invalid or expired
     */
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts the email (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the user's email stored in the token
     */
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     *
     * Retrieves the custom "role" claim stored during token generation.
     *
     * @param token the JWT token
     * @return the user's role (ADMIN, INSTRUCTOR, STUDENT)
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * Compares the token's expiration date with the current date.
     *
     * @param token the JWT token to check
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    /**
     * Validates a JWT token.
     *
     * Checks that:
     *
     *   The token's email matches the provided email
     *   The token has not expired
     *
     * @param token the JWT token to validate
     * @param email the expected email to match against the token's subject
     * @return true if the token is valid and not expired, false otherwise
     */
    public boolean validateToken(String token, String email) {
        return (extractEmail(token).equals(email) && !isTokenExpired(token));
    }
}











