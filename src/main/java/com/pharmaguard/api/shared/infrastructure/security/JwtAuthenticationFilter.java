package com.pharmaguard.api.shared.infrastructure.security;

import com.pharmaguard.api.auth.adapters.out.security.JwtTokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_ROLES = "roles";
    private static final String TOKEN_TYPE_ACCESS = "access";

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = extrairToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            autenticar(token, request);
        }

        chain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    private void autenticar(String token, HttpServletRequest request) {
        try {
            Map<String, Object> claims = jwtTokenService.extrairClaims(token);

            if (!TOKEN_TYPE_ACCESS.equals(claims.get(CLAIM_TOKEN_TYPE))) {
                throw new JwtException("token invalido para autenticacao");
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                    String.valueOf(claims.get("sub")), null, extrairAuthorities(claims));
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
            LOGGER.warn("event=jwt_authentication_failed path={} message={}", request.getRequestURI(),
                    ex.getMessage());
        }
    }

    private Collection<GrantedAuthority> extrairAuthorities(Map<String, Object> claims) {
        Object roles = claims.get(CLAIM_ROLES);

        if (!(roles instanceof Collection<?> valores)) {
            return List.of();
        }

        return valores.stream()
                .map(valor -> (GrantedAuthority) new SimpleGrantedAuthority(String.valueOf(valor)))
                .toList();
    }
}
