package app.services;

/**
 * Handles user authentication, session management, and credential changes.
 * All operations are username-scoped.
 */
public interface AuthService {

    /** Replaces the current password for the given user. */
    void changeUserProfilePassword(String username, String newPassword);

    /** Authenticates the user and opens new JWT session. */
    String authenticateUser(String username, String password);

    /** Terminates the active session for the given user. */
    void logoutUserProfile(String username);
}