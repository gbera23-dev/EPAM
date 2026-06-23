package app.services;

/**
 * Service is for tracking and maintaining blocked users after some unsuccessful attempts to log in
 */
public interface DDOSProtectionService {

    void blockUser(String userIdentifier);

    void releaseUsersWithExpiredLocks();

    void recordUserAttempt(String userIdentifier);

    boolean userShouldBeBlocked(String userIdentifier);

    boolean userIsBlocked(String userIdentifier);

    long timeLeftBeforeLockIsReleased(String userIdentifier);

}
