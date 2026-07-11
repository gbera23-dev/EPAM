package com.example.Trainer_history_service.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

/**
 * Service interface defining operations necessary to generate, extract and validate JWT Tokens
 */
public interface JWTService {

    /**
     * Method takes in jwt token and extracts username out of the token
     * @param jwtToken JWT token to be used to extract the username
     * @return username extracted from JWT Token
     */
    String extractUsernameFromToken(String jwtToken);

    /**
     * Method looks at JWT Token and determines whether it is valid or not
     * @param jwtToken JWT token to be used to extract the username
     * @return true, if user is validated, false otherwise
     */
    Boolean tokenIsValid(String jwtToken);
}
