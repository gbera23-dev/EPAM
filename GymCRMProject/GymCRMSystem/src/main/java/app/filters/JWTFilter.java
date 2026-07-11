package app.filters;

import app.exceptions.UserCannotBeAuthorizedException;
import app.services.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String JWT_TOKEN_PREFIX = "Bearer ";

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;


    public JWTFilter(JWTService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //get authorization header(where JWT Token is stored)
        String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
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
        }

        //Only proceed if we have a username AND no active session exists AND token is not blacklisted.
        // Otherwise, go to the next filter.
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null
        && !jwtService.tokenIsBlacklisted(jwtToken)) {

            //load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //check that token really is owned by the user(token has username in it and it is not expired)
            if(jwtService.tokenIsValid(jwtToken, userDetails)) {

                setUserSession(request, userDetails);
            }
        }
        // If user does not hold the session, JWT token is checked, if JWT token is expired, user cannot be
        // authenticated.
        filterChain.doFilter(request, response);
    }

    private void setUserSession(HttpServletRequest request, UserDetails userDetails) {
        //provide instance of authentication session token
        // (Allows us to maintain session, when user is logged in)
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //populate authentication token with request data and set it to context holder
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
