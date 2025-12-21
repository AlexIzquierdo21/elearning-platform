package com.elearning.elearning_platform.shared.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter that intercepts HTTP requests to validate
 * JWT tokens and establish security context.
 *
 * <p>This filter extracts the JWT token from the Authorization header,
 * validates it, and loads the authenticated user's information into
 * Spring Security's SecurityContext for downstream authorization checks.
 *
 * <p>The filter is executed once per request and runs before the standard
 * Spring Security authentication filters in the filter chain.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * Constructs a new JwtAuthenticationFilter.
     *
     * @param jwtUtil utility for JWT token operations (validation, extraction)
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Performs JWT authentication for each incoming request.
     *
     * <p>Authentication flow:
     * <ol>
     *   <li>Extracts JWT token from Authorization header</li>
     *   <li>Validates token and extracts user email</li>
     *   <li>Verifies no existing authentication in SecurityContext</li>
     *   <li>Extracts user role and creates authorities</li>
     *   <li>Creates authentication token and sets it in SecurityContext</li>
     *   <li>Continues filter chain execution</li>
     * </ol>
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Spring Security filter chain
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
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

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null &&
                    jwtUtil.validateToken(token, email)) {

                String role = jwtUtil.extractRole(token);

                System.out.println("🔍 JWT Filter - Email: " + email + ", Role: " + role);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // ✅ LOGS AÑADIDOS
                System.out.println("✅ Authentication guardada: " + SecurityContextHolder.getContext().getAuthentication());
                System.out.println("✅ Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
        }

        // ✅ LOG ANTES DE CONTINUAR
        System.out.println("🚀 Antes de continuar filter chain, auth = " + SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from the Authorization header.
     *
     * <p>Expected header format: {@code Authorization: Bearer <token>}
     *
     * @param request HTTP request containing Authorization header
     * @return JWT token without "Bearer " prefix, or null if header is absent or malformed
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}




















