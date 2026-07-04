package app.services;

import app.entities.BlacklistedJWT;
import app.exceptions.UserCannotBeAuthorizedException;
import app.persistence.JWTRepository;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JWTServiceImplTest {

    private JWTServiceImpl jwtService;
    private JWTRepository jwtRepository;

    @BeforeEach
    public void setup() throws Exception {
        jwtRepository = mock(JWTRepository.class);
        jwtService = new JWTServiceImpl(jwtRepository);
        String rawKey = "01234567890123456789012345678901";
        String base64Key = Base64.getEncoder().encodeToString(rawKey.getBytes());
        Field secretField = JWTServiceImpl.class.getDeclaredField("SECRET_KEY");
        secretField.setAccessible(true);
        secretField.set(jwtService, base64Key);
        Field expField = JWTServiceImpl.class.getDeclaredField("expiration");
        expField.setAccessible(true);
        expField.set(jwtService, 1000L * 60L * 10L);
        reset(jwtRepository);
    }

    @Test
    public void testGenerateTokenAndExtractUsername() {
        String token = jwtService.generateToken("alice");
        assertNotNull(token);
        String username = jwtService.extractUsernameFromToken(token);
        assertEquals("alice", username);
    }

    @Test
    public void testTokenIsValidReturnsTrueForMatchingUser() {
        String token = jwtService.generateToken("bob");
        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("bob");
        assertTrue(jwtService.tokenIsValid(token, ud));
    }

    @Test
    public void testTokenIsValidReturnsFalseForNonMatchingUser() {
        String token = jwtService.generateToken("bob2");
        UserDetails ud = mock(UserDetails.class);
        when(ud.getUsername()).thenReturn("other");
        assertFalse(jwtService.tokenIsValid(token, ud));
    }

    @Test
    public void testTokenIsBlacklistedAndAddJWTTokenToBlacklistAndCleanUpBlacklist() {
        String token = jwtService.generateToken("z");
        when(jwtRepository.existsByJti(anyString())).thenReturn(true);
        assertTrue(jwtService.tokenIsBlacklisted(token));
        ArgumentCaptor<BlacklistedJWT> captor = ArgumentCaptor.forClass(BlacklistedJWT.class);
        jwtService.addJWTTokenToBlacklist(token);
        verify(jwtRepository).save(captor.capture());
        BlacklistedJWT saved = captor.getValue();
        assertNotNull(saved.getJti());
        assertNotNull(saved.getExpirationDate());
        jwtService.cleanUpBlacklist();
        verify(jwtRepository).cleanUpExpiredTokens(any(Instant.class));
    }

    @Test
    public void testExtractUsernameFromTokenThrowsWhenInvalid() {
        assertThrows(UserCannotBeAuthorizedException.class, () -> jwtService.extractUsernameFromToken("invalid.token.here"));
    }
}