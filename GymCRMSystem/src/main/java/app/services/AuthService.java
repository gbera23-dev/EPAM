package app.services;

/**
 * Handles user authentication, session management, and credential changes.
 * All operations are username-scoped.
 */
public interface AuthService {

    /** @return true if the username/password pair matches a stored profile */
    boolean validateUserProfile(String username, String password);

    /** @return true if an active session exists for the given username */
    boolean validateUserSession(String username);

    /** Replaces the current password for the given user. */
    void changeUserProfilePassword(String username, String newPassword);

    /** Authenticates the user and opens a session. */
    void loginUserProfile(String username, String password);

    /** Terminates the active session for the given user. */
    void logoutUserProfile(String username);

    /** Removes all sessions that have passed their expiry time. */
    void cleanUpExpiredSessions();
}