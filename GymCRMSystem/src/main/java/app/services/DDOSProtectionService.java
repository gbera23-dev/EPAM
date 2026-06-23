package app.services;

/**
 * Service is for tracking and maintaining blocked users after some unsuccessful attempts to log in
 */
public interface DDOSProtectionService {

    /**
     * Method blocks the user, when number of failed logging requests reach the limit
     * @param userIdentifier unique identifier of user
     */
    void blockUser(String userIdentifier);

    /**
     * Method looks through current list of blocked users and releases temporary lock, when it expires
     */
    void releaseUsersWithExpiredLocks();

    /**
     * Method records that user has requested to log in, but failed due to wrong credentials
     * @param userIdentifier unique identifier of user
     */
    void recordUserAttempt(String userIdentifier);

    /**
     * Method determines whether user must be blocked or not. That is, whether number of failing login requests
     * hit the limit
     * @param userIdentifier unique identifier of user
     * @return true, if user should be blocked, false otherwise
     */
    boolean userShouldBeBlocked(String userIdentifier);

    /**
     * Method determins whether user is currently blocked or not, that is he is blocked temporarily from making login
     * requests
     * @param userIdentifier unique identifier of user
     * @return true, if user is blocked, false otherwise
     */
    boolean userIsBlocked(String userIdentifier);

    /**
     * Method determines time left for user until he is able to send requests to login endpoint again
     * @param userIdentifier unique identifier of user
     * @return number of seconds left till user's lock is released
     */
    long timeLeftBeforeLockIsReleased(String userIdentifier);

    /**
     * Method reloads number of attempts for the user. It is used when user logs in successfully.
     * @param userIdentifier unique identifier of user
     */
    void reloadUserLoginAttempts(String userIdentifier);
}
