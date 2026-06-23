package app.services;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DDOSProtectionServiceImpl implements DDOSProtectionService {

    private static final Integer NUM_LOGIN_ATTEMPTS = 3;
    private static final Integer LOCK_DURATION = 5;

    private final Map<String, LocalTime> lockedUsers = new ConcurrentHashMap<>();
    private final Map<String, Integer> userAttemptCounts = new ConcurrentHashMap<>();

    @Override
    public void blockUser(String userIdentifier) {
        lockedUsers.put(userIdentifier, LocalTime.now().plusMinutes(LOCK_DURATION));
        userAttemptCounts.remove(userIdentifier);
    }

    @Override
    public void releaseUsersWithExpiredLocks() {
        lockedUsers.entrySet()
                .removeIf(e -> !e.getValue().isAfter(LocalTime.now()));
    }

    @Override
    public void recordUserAttempt(String userIdentifier) {
        if(!userAttemptCounts.containsKey(userIdentifier)) {
            userAttemptCounts.put(userIdentifier, 0);
        }
        userAttemptCounts.put(userIdentifier, userAttemptCounts.get(userIdentifier) + 1);
        System.out.println("user attempt count: " + userAttemptCounts.get(userIdentifier));
    }

    @Override
    public boolean userShouldBeBlocked(String userIdentifier) {

        if(lockedUsers.containsKey(userIdentifier)){
            return false;
        }

        return numAttemptsExceedLimit(userIdentifier);
    }

    @Override
    public boolean userIsBlocked(String userIdentifier) {
        return lockedUsers.containsKey(userIdentifier);
    }

    @Override
    public long timeLeftBeforeLockIsReleased(String userIdentifier) {

        long timeLeft = 0;

        if(lockedUsers.containsKey(userIdentifier)) {
            LocalTime lockExpirationTime = lockedUsers.get(userIdentifier);
            timeLeft = ChronoUnit.SECONDS.between(LocalTime.now(), lockExpirationTime);
        }

        return timeLeft;
    }

    @Override
    public void reloadUserLoginAttempts(String userIdentifier) {
        userAttemptCounts.remove(userIdentifier);
    }

    private boolean numAttemptsExceedLimit(String userIdentifier) {
        System.out.println("wtttf user aattemmpt? : " + userAttemptCounts.get(userIdentifier));
        return userAttemptCounts.containsKey(userIdentifier) &&
                userAttemptCounts.get(userIdentifier) >= NUM_LOGIN_ATTEMPTS;
    }

}
