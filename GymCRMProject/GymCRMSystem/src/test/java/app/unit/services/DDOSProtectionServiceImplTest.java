package app.unit.services;

import app.services.DDOSProtectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DDOSProtectionServiceImplTest {

    private DDOSProtectionServiceImpl service;

    @BeforeEach
    public void setup() {
        service = new DDOSProtectionServiceImpl(3, 5);
    }

    @Test
    public void testBlockUserAddsLockAndRemovesAttempts() throws Exception {
        service.recordUserAttempt("id");
        service.blockUser("id");
        Field attemptsField = DDOSProtectionServiceImpl.class.getDeclaredField("userAttemptCounts");
        attemptsField.setAccessible(true);
        Map<String, Integer> attempts = (Map<String, Integer>) attemptsField.get(service);
        assertFalse(attempts.containsKey("id"));
        assertTrue(service.userIsBlocked("id"));
    }

    @Test
    public void testRecordUserAttemptIncrementsCountAndMayTriggerBlock() {
        service.recordUserAttempt("u");
        service.recordUserAttempt("u");
        service.recordUserAttempt("u");
        assertTrue(service.userShouldBeBlocked("u"));
    }

    @Test
    public void testUserShouldBeBlockedWhenAttemptsExceedLimit() {
        service.recordUserAttempt("x");
        service.recordUserAttempt("x");
        service.recordUserAttempt("x");
        assertTrue(service.userShouldBeBlocked("x"));
    }

    @Test
    public void testUserIsBlockedChecksLockedUsers() {
        service.blockUser("user1");
        assertTrue(service.userIsBlocked("user1"));
        assertFalse(service.userIsBlocked("unknown"));
    }

    @Test
    public void testTimeLeftBeforeLockIsReleasedReturnsPositiveOrZero() {
        service.blockUser("u1");
        long left = service.timeLeftBeforeLockIsReleased("u1");
        assertTrue(left > 0);
        long zeroLeft = service.timeLeftBeforeLockIsReleased("nonexistent");
        assertEquals(0L, zeroLeft);
    }
}