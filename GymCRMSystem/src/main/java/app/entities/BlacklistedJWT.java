package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "JWTBlacklist", indexes = {
        @Index(name = "idx_jti", columnList = "jti")
})
public class BlacklistedJWT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="Jti")
    private String jti;

    @Column(name="expiration_date")
    private Instant expirationDate;
}
