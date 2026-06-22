import app. entities.User;
import app. exceptions.PasswordDoesNotMatchException;
import app. exceptions.SessionNotFoundException;
import app. exceptions.UserAlreadyLoggedInException;
import app. exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app. persistence.UserRepository;
import app. services.AuthServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john.doe");
        user.setPassword("pass123");
    }

    private Map<String, LocalDateTime> getSessions() throws Exception {
        Field field = AuthServiceImpl.class.getDeclaredField("sessions");
        field.setAccessible(true);
        return (Map<String, LocalDateTime>) field.get(authService);
    }

    private void putSession(String username, LocalDateTime time) throws Exception {
        getSessions().put(username, time);
    }

    @Test
    void testValidateUserProfileReturnsTrueOnCorrectPassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);

        assertTrue(authService.validateUserProfile("john.doe", "pass123"));
    }

    @Test
    void testValidateUserProfileReturnsFalseOnWrongPassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);

        assertFalse(authService.validateUserProfile("john.doe", "wrong"));
    }

    @Test
    void testValidateUserProfileThrowsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authService.validateUserProfile("ghost", "pass"));
    }

    @Test
    void testValidateUserSessionReturnsTrueWhenSessionExists() throws Exception {
        putSession("john.doe", LocalDateTime.now());

        assertTrue(authService.validateUserSession("john.doe"));
    }

    @Test
    void testValidateUserSessionReturnsFalseWhenNoSession() {
        assertFalse(authService.validateUserSession("john.doe"));
    }

    @Test
    void testLoginUserProfileCreatesSession() throws Exception {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);

        authService.loginUserProfile("john.doe", "pass123");

        assertTrue(getSessions().containsKey("john.doe"));
    }

    @Test
    void testLoginUserProfileThrowsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authService.loginUserProfile("ghost", "pass123"));
    }

    @Test
    void testLoginUserProfileThrowsOnWrongPassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);

        assertThrows(PasswordDoesNotMatchException.class, () -> authService.loginUserProfile("john.doe", "wrong"));
    }

    @Test
    void testLoginUserProfileThrowsWhenAlreadyLoggedIn() throws Exception {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);
        putSession("john.doe", LocalDateTime.now());

        assertThrows(UserAlreadyLoggedInException.class, () -> authService.loginUserProfile("john.doe", "pass123"));
    }

    @Test
    void testLogoutUserProfileRemovesSession() throws Exception {
        putSession("john.doe", LocalDateTime.now());

        authService.logoutUserProfile("john.doe");

        assertFalse(getSessions().containsKey("john.doe"));
    }

    @Test
    void testLogoutUserProfileThrowsWhenNoSession() {
        assertThrows(SessionNotFoundException.class, () -> authService.logoutUserProfile("john.doe"));
    }

    @Test
    void testChangeUserProfilePasswordUpdatesPassword() throws Exception {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("newPass");

        putSession("john.doe", LocalDateTime.now());

        authService.changeUserProfilePassword("john.doe", "newPass");

        assertEquals("newPass", user.getPassword());
    }

    @Test
    void testChangeUserProfilePasswordThrowsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authService.changeUserProfilePassword("ghost", "newPass"));
    }

    @Test
    void testChangeUserProfilePasswordThrowsWhenNoSession() {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);

        assertThrows(SessionNotFoundException.class, () -> authService.changeUserProfilePassword("john.doe", "newPass"));
    }

    @Test
    void testCleanUpExpiredSessionsRemovesExpiredSession() throws Exception {
        putSession("john.doe", LocalDateTime.now().minusSeconds(1801));

        authService.cleanUpExpiredSessions();

        assertFalse(getSessions().containsKey("john.doe"));
    }

    @Test
    void testCleanUpExpiredSessionsKeepsActiveSession() throws Exception {
        putSession("john.doe", LocalDateTime.now().minusSeconds(100));

        authService.cleanUpExpiredSessions();

        assertTrue(getSessions().containsKey("john.doe"));
    }

    @Test
    void testCleanUpExpiredSessionsRemovesOnlyExpired() throws Exception {
        putSession("john.doe", LocalDateTime.now().minusSeconds(1801));
        putSession("jane.doe", LocalDateTime.now().minusSeconds(100));

        authService.cleanUpExpiredSessions();

        assertFalse(getSessions().containsKey("john.doe"));
        assertTrue(getSessions().containsKey("jane.doe"));
    }
}