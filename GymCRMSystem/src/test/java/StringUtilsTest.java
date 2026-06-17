import app.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {

    @Test
    public void testGenerateUsernameSameUsernames() {
        String currentUsername = "john.doe";
        List<String> usernames = List.of("john.doe", "john.doe1", "john.doe2", "john.doe3", "john.doe4");
        assertEquals("john.doe5", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testGenerateUsernameNewUsername() {
        String currentUsername = "linus.torvalds";
        List<String> usernames = List.of("john.doe", "john.doe1", "john.doe2");
        assertEquals("linus.torvalds", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testGenerateUsernameSecondTime() {
        String currentUsername = "linus.torvalds";
        List<String> usernames = List.of("linus.torvalds");
        assertEquals("linus.torvalds1", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testGenerateUsernameWithGaps() {
        String currentUsername = "alice";
        List<String> usernames = List.of("alice", "alice10");
        assertEquals("alice11", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testGenerateUsernameEmptyList() {
        String currentUsername = "bob";
        List<String> usernames = Collections.emptyList();
        assertEquals("bob", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testThrowsExceptionForNumbersInInput() {
        String currentUsername = "agent007";
        List<String> usernames = List.of("agent");
        assertThrows(IllegalArgumentException.class, () -> {
            StringUtils.generateUsername(currentUsername, usernames);
        });
    }

    @Test
    public void testDoesNotMatchOverlappingNames() {
        String currentUsername = "mark";
        List<String> usernames = List.of("mark", "markus", "mark1");
        assertEquals("mark2", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testHandlesLargeSuffix() {
        String currentUsername = "test.user";
        List<String> usernames = List.of("test.user999999");
        assertEquals("test.user1000000", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testCaseSensitivity() {
        String currentUsername = "Alice";
        List<String> usernames = List.of("alice");
        assertEquals("Alice", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testSpecialCharactersInBaseName() {
        String currentUsername = "user_name.test";
        List<String> usernames = List.of("user_name.test", "user_name.test1");
        assertEquals("user_name.test2", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testUnorderedList() {
        String currentUsername = "dev";
        List<String> usernames = List.of("dev2", "dev", "dev1");
        assertEquals("dev3", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testSimilarPrefixNotMatching() {
        String currentUsername = "admin";
        List<String> usernames = List.of("administrator", "admin_1");
        assertEquals("admin", StringUtils.generateUsername(currentUsername, usernames));
    }

    @Test
    public void testNullOrEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            StringUtils.generateUsername("", List.of());
        });
    }


    @Test
    public void testGenerateRandomPassword() {
        String generatedPassword = StringUtils.generateRandomPassword();
        String secondPassword = StringUtils.generateRandomPassword();

        //size is equal to 10
        assertEquals(10, generatedPassword.length());

        //generated passwords should be random
        assertNotEquals(generatedPassword, secondPassword);
    }

}
