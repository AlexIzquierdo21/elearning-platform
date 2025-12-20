package com.elearning.elearning_platform.shared.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


/**
 * JWT authentication filter that intercepts each HTTP request,
 * extracts the JWT token, validates it, and loads user information
 * into Spring Security's context.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructor for the filter.
     *
     * @param jwtUtil Utility class for handling JWT operations (validation, claim extraction, etc.)
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Main filter method executed for each incoming request.
     *
     * Flow:
     * - Extracts the token from the Authorization header.
     * - If it exists and is valid, extracts email and role.
     * - Creates an Authentication object and sets it in the SecurityContext.
     * - Continues with the filter chain.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Spring Security filter chain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractTokenFromRequest(request);

        if (token != null) {
            String email = jwtUtil.extractEmail(token);
            if (email != null && jwtUtil.validateToken(token, email)); {
                String role = jwtUtil.extractRole(token);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email, null, authorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request HTTP request
     * @return JWT token without the "Bearer " prefix, or null if not present
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}






















