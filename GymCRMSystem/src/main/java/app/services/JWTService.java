package app.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;

/**
 * Service interface defining operations necessary to generate, extract and validate JWT Tokens
 */
public interface JWTService {

    /**
     * Method takes in username of the user and generates appropriate JWT token, which then will serve as a way to
     * authenticate the user
     * @param username username of the user who is trying to authenticate
     * @return generated JWT Token
     */
    String generateToken(String username);

    /**
     * Method takes in jwt token and extracts username out of the token
     * @param jwtToken JWT token to be used to extract the username
     * @return username extracted from JWT Token
     */
    String extractUsernameFromToken(String jwtToken);

    /**
     * Method looks at user's details and validates whether passed JWT token matches the details of the user
     * @param jwtToken JWT token to be used to extract the username
     * @param userDetails Details of the user
     * @return true, if user is validated, false otherwise
     */
    Boolean tokenIsValid(String jwtToken, UserDetails userDetails);

    /**
     * Method checks whether jwt token is blacklisted
     * @param jwtToken to be checked
     * @return true, if jwt token is black listed, false otherwise
     */
    boolean tokenIsBlacklisted(String jwtToken);

    /**
     * Method looks through blacklist of JWT's and deletes all JWT's that are expired
     */
    void cleanUpBlacklist();

    /**
     * Method adds jwt token of user to blackList
     * @param jwtToken JWT token to be added to blacklist
     */
    void addJWTTokenToBlacklist(String jwtToken);
}
