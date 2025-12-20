package com.elearning.elearning_platform.user.infrastructure.adapter.in.web.dto;

/**
 * Data Transfer Object (DTO) used to send authentication
 * information back to the client after a successful login.
 *
 * This response typically contains a JWT token along with
 * metadata required by the client to perform authenticated
 * requests against the API.
 */
public class LoginResponse {

    /**
     * JWT access token generated after successful authentication.
     */
    private String token;

    /**
     * Token type used in the Authorization header.
     * Usually "Bearer".
     */
    private String type;

    /**
     * Token expiration time expressed in seconds.
     */
    private Long expiresIn;

    /**
     * Creates a new LoginResponse.
     *
     * @param token     the generated JWT token
     * @param type      the token type (e.g. Bearer)
     * @param expiresIn token expiration time in seconds
     */
    public LoginResponse(String token, String type, Long expiresIn) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
    }

    /**
     * Returns the JWT access token.
     *
     * @return JWT token as String
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the token type.
     *
     * @return token type (e.g. Bearer)
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the token expiration time.
     *
     * @return expiration time in seconds
     */
    public Long getExpiresIn() {
        return expiresIn;
    }
}

