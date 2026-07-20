package com.example.Trainer_history_service.filters;

import com.example.Trainer_history_service.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String JWT_TOKEN_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JWTService jwtService;

    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //get authorization header(where JWT Token is stored)
        String authenticationHeader = request.getHeader(AUTHORIZATION_HEADER);
        String jwtToken = null;
        String username = null;

        //check that authorization header really does contain bearer token and extract it out
        if(authenticationHeader != null && authenticationHeader.startsWith(JWT_TOKEN_PREFIX)) {
            jwtToken = authenticationHeader.substring(JWT_TOKEN_PREFIX.length());

            try {
                username = jwtService.extractUsernameFromToken(jwtToken);
            } catch(UserCannotBeAuthorizedException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT Validation failed!");
                return;
            }

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }
}
