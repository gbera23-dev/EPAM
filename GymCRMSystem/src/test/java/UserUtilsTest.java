import entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserUtilsTest {

    private User newUser;
    private List<User> existingUsers;

    @BeforeEach
    public void setUp() {
        newUser = new User(1L, "John", "Doe", null, null, true);
        existingUsers = new ArrayList<>();
    }

    @Test
    public void testGenerateUserCredentialsForNewUser() {
        existingUsers.add(new User(2L, "Jane", "Smith", "jane.smith", "pass123", true));

        UserUtils.generateUserCredentials(newUser, existingUsers);

        assertEquals("John.Doe", newUser.getUsername());
        assertNotNull(newUser.getPassword());
        assertFalse(newUser.getPassword().isEmpty());
    }

    @Test
    public void testGenerateUserCredentialsWhenUsernameExists() {
        existingUsers.add(new User(2L, "Existing", "User", "John.Doe", "pass1", true));
        existingUsers.add(new User(3L, "Existing", "User", "John.Doe1", "pass2", true));

        UserUtils.generateUserCredentials(newUser, existingUsers);

        assertEquals("John.Doe2", newUser.getUsername());
    }

    @Test
    public void testGenerateUserCredentialsCollisionWithSimilarNames() {
        existingUsers.add(new User(2L, "John", "Doer", "John.Doer", "pass1", true));

        UserUtils.generateUserCredentials(newUser, existingUsers);

        assertEquals("John.Doe", newUser.getUsername());
    }

    @Test
    public void testGenerateUserCredentialsHandlesGaps() {
        existingUsers.add(new User(2L, "Old", "User", "John.Doe", "pass1", true));
        existingUsers.add(new User(3L, "Old", "User", "John.Doe5", "pass2", true));

        UserUtils.generateUserCredentials(newUser, existingUsers);

        assertEquals("John.Doe6", newUser.getUsername());
    }

    @Test
    public void testGenerateUserCredentialsUpdatesExistingObjectState() {
        UserUtils.generateUserCredentials(newUser, existingUsers);

        assertNotNull(newUser.getUsername());
        assertNotNull(newUser.getPassword());
        assertTrue(newUser.isActive());
        assertEquals(1L, newUser.getUserId());
    }

    @Test
    public void testThrowsExceptionWhenFirstNameContainsDigit() {
        User invalidUser = new User(4L, "J0hn", "Doe", null, null, true);

        assertThrows(IllegalArgumentException.class, () -> {
            UserUtils.generateUserCredentials(invalidUser, existingUsers);
        });
    }

    @Test
    public void testThrowsExceptionWhenLastNameContainsDigit() {
        User invalidUser = new User(5L, "John", "D0e", null, null, true);

        assertThrows(IllegalArgumentException.class, () -> {
            UserUtils.generateUserCredentials(invalidUser, existingUsers);
        });
    }

    @Test
    public void testGenerateUserCredentialsWithEmptyList() {
        UserUtils.generateUserCredentials(newUser, new ArrayList<>());

        assertEquals("John.Doe", newUser.getUsername());
        assertNotNull(newUser.getPassword());
    }
}