import app.entities.User;
import app.exceptions.UserCannotBeAuthorizedException;
import app.exceptions.UserNotFoundException;
import app.services.DDOSProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.persistence.UserRepository;
import app.services.AuthServiceImpl;
import app.services.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private DDOSProtectionService ddosProtectionService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john.doe");
        user.setPassword("pass123");
    }

    @Test
    void testAuthenticateUserReturnsToken() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken("john.doe")).thenReturn("jwt-token");

        String token = authService.authenticateUser("john.doe", "pass123", null);

        assertEquals("jwt-token", token);
    }

    @Test
    void testAuthenticateUserThrowsWhenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThrows(UserCannotBeAuthorizedException.class, () -> authService.authenticateUser
                ("john.doe", "pass123", null));
    }

    @Test
    void testChangeUserProfilePasswordUpdatesPassword() {
        when(userRepository.findByUsername("john.doe")).thenReturn(user);
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedNewPass");

        authService.changeUserProfilePassword("john.doe", "newPass");

        assertEquals("encodedNewPass", user.getPassword());
    }

    @Test
    void testChangeUserProfilePasswordThrowsWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> authService.changeUserProfilePassword("ghost", "newPass"));
    }
}