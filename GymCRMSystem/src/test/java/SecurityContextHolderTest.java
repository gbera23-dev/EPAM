import auth.SecurityContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SecurityContextHolderTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clear();
    }

    @Test
    void testGetCurrentUserReturnsNullWhenNotSet() {
        assertNull(SecurityContextHolder.getCurrentUser());
    }

    @Test
    void testSetCurrentUserStoresUsername() {
        SecurityContextHolder.setCurrentUser("john.doe");

        assertEquals("john.doe", SecurityContextHolder.getCurrentUser());
    }

    @Test
    void testSetCurrentUserOverwritesPreviousValue() {
        SecurityContextHolder.setCurrentUser("john.doe");
        SecurityContextHolder.setCurrentUser("jane.doe");

        assertEquals("jane.doe", SecurityContextHolder.getCurrentUser());
    }

    @Test
    void testClearRemovesCurrentUser() {
        SecurityContextHolder.setCurrentUser("john.doe");

        SecurityContextHolder.clear();

        assertNull(SecurityContextHolder.getCurrentUser());
    }

    @Test
    void testClearOnEmptyContextDoesNotThrow() {
        assertDoesNotThrow(SecurityContextHolder::clear);
    }

    @Test
    void testThreadIsolationUserNotVisibleInOtherThread() throws InterruptedException {
        SecurityContextHolder.setCurrentUser("john.doe");

        AtomicReference<String> otherThreadUser = new AtomicReference<>();
        Thread thread = new Thread(() -> otherThreadUser.set(SecurityContextHolder.getCurrentUser()));
        thread.start();
        thread.join();

        assertNull(otherThreadUser.get());
    }

    @Test
    void testThreadIsolationEachThreadHasOwnUser() throws InterruptedException {
        AtomicReference<String> threadAUser = new AtomicReference<>();
        AtomicReference<String> threadBUser = new AtomicReference<>();

        Thread threadA = new Thread(() -> {
            SecurityContextHolder.setCurrentUser("user.a");
            threadAUser.set(SecurityContextHolder.getCurrentUser());
        });
        Thread threadB = new Thread(() -> {
            SecurityContextHolder.setCurrentUser("user.b");
            threadBUser.set(SecurityContextHolder.getCurrentUser());
        });

        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        assertEquals("user.a", threadAUser.get());
        assertEquals("user.b", threadBUser.get());
    }

    @Test
    void testThreadIsolationClearInOneThreadDoesNotAffectAnother() throws InterruptedException {
        SecurityContextHolder.setCurrentUser("john.doe");

        Thread thread = new Thread(() -> {
            SecurityContextHolder.setCurrentUser("other.user");
            SecurityContextHolder.clear();
        });
        thread.start();
        thread.join();

        assertEquals("john.doe", SecurityContextHolder.getCurrentUser());
    }
}