package services;

public interface AuthService {

    boolean validateUserProfile(String username, String password);

    boolean validateUserSession(String username);

    void changeUserProfilePassword(String username, String newPassword);

    void loginUserProfile(String username, String password);

    void logoutUserProfile(String username);

    void cleanUpExpiredSessions();
}
