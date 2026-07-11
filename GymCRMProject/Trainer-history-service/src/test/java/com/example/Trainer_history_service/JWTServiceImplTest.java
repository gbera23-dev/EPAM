package com.example.Trainer_history_service;

import com.example.Trainer_history_service.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.JWTServiceImpl;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceImplTest {

    private JWTServiceImpl jwtService;
    private SecretKey secretKey;
    private String encodedSecretKey;

    @BeforeEach
    void setUp() {
        jwtService = new JWTServiceImpl();
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        encodedSecretKey = Encoders.BASE64.encode(secretKey.getEncoded());
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", encodedSecretKey);
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    private String buildToken(String subject, Date expirationDate) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    @Test
    void testExtractUsernameFromTokenReturnsUsername() {
        String token = buildToken("john", new Date(System.currentTimeMillis() + 100000));

        String username = jwtService.extractUsernameFromToken(token);

        assertEquals("john", username);
    }

    @Test
    void testExtractUsernameFromTokenThrowsUserCannotBeAuthorizedExceptionWhenTokenInvalid() {
        assertThrows(UserCannotBeAuthorizedException.class,
                () -> jwtService.extractUsernameFromToken("invalid.token.value"));
    }

    @Test
    void testExtractUsernameFromTokenThrowsJwtExceptionWhenSubjectMissing() {
        String token = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(secretKey)
                .compact();

        assertThrows(JwtException.class, () -> jwtService.extractUsernameFromToken(token));
    }

    @Test
    void testTokenIsValidReturnsTrueWhenNotExpired() {
        String token = buildToken("john", new Date(System.currentTimeMillis() + 100000));

        assertTrue(jwtService.tokenIsValid(token));
    }

    @Test
    void testTokenIsValidThrowsUserCannotBeAuthorizedExceptionWhenExpired() {
        String token = buildToken("john", new Date(System.currentTimeMillis() - 100000));

        assertThrows(UserCannotBeAuthorizedException.class, () -> jwtService.tokenIsValid(token));
    }

    @Test
    void testTokenIsValidThrowsUserCannotBeAuthorizedExceptionWhenSignedWithWrongKey() {
        SecretKey otherKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts.builder()
                .setSubject("john")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(otherKey)
                .compact();

        assertThrows(UserCannotBeAuthorizedException.class, () -> jwtService.tokenIsValid(token));
    }
}
