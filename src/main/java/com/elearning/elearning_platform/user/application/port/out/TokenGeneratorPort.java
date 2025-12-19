package com.elearning.elearning_platform.user.application.port.out;

/**
 * Output port responsible for generating authentication tokens.
 *
 * This belongs to the application layer and defines the contract
 * for any token generation mechanism.
 *
 * The application layer depends only on this interface,
 * never on a concrete implementation like JWT.
 */
public interface TokenGeneratorPort {

    /**
     * Generates an authentication token for a user.
     *
     * @param email user's email (already validated and coming from the domain)
     * @param role  user's role (domain value)
     * @return generated token as a String
     */
    String generateToken(String email, String role);
}



