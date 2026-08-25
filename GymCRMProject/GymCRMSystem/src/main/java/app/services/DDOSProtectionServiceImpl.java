package app.services;

import app.annotations.ServiceLayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@ServiceLayer
public class DDOSProtectionServiceImpl implements DDOSProtectionService {

    private final Integer NUM_LOGIN_ATTEMPTS;
    private final Integer LOCK_DURATION;

    private final Map<String, LocalTime> lockedUsers = new ConcurrentHashMap<>();
    private final Map<String, Integer> userAttemptCounts = new ConcurrentHashMap<>();

    public DDOSProtectionServiceImpl(@Value("${ddos-protection.num-login-attempts}")
                                     Integer numLoginAttempts,
                                     @Value("${ddos-protection.lock-duration}")
                                     Integer lockDuration
                                     ) {
        NUM_LOGIN_ATTEMPTS=numLoginAttempts;
        LOCK_DURATION=lockDuration;
    }

    @Override
    public void blockUser(String userIdentifier) {
        log.info("lock duration: {}", LOCK_DURATION);
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
        log.info("num login attempts: {}", NUM_LOGIN_ATTEMPTS);
        return userAttemptCounts.containsKey(userIdentifier) &&
                userAttemptCounts.get(userIdentifier) >= NUM_LOGIN_ATTEMPTS;
    }

}
