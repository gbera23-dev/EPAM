package com.example.Trainer_history_service.integration.cucumber;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenFactory {

    private static final String FOREIGN_KEY =
            "1f2e3d4c5b6a798877665544332211ffeeddccbbaa99887766554433221100ff";

    @Value("${security.jwt_secret_key}")
    private String secretKey;

    public String configuredKey() {
        return secretKey;
    }

    public String validToken(String subject, Duration validFor) {
        return build(subject, validFor, signingKey());
    }

    public String tokenWithoutSubject() {
        Date now = new Date();
        return Jwts.builder()
                .claim("scope", "workload")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis()))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String tokenOfKind(String kind) {
        return switch (kind) {
            case "empty" -> "";
            case "malformed" -> "not-a-jwt-token";
            case "expired" -> build("john.doe", Duration.ofMinutes(-5), signingKey());
            case "signed with a different key" -> build("john.doe", Duration.ofMinutes(30), foreignKey());
            case "with a tampered payload" -> tamper(build("john.doe", Duration.ofMinutes(30), signingKey()));
            default -> throw new IllegalArgumentException("Unsupported token kind: " + kind);
        };
    }

    private String build(String subject, Duration validFor, SecretKey key) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validFor.toMillis()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String tamper(String token) {
        String[] parts = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        String altered = payload.replace("john.doe", "mallory");
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(altered.getBytes());
        return parts[0] + "." + encoded + "." + parts[2];
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private SecretKey foreignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(FOREIGN_KEY));
    }
}