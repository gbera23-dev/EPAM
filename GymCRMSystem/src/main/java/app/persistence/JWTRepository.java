package app.persistence;

import app.entities.BlacklistedJWT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface JWTRepository extends JpaRepository<BlacklistedJWT, Long> {

    boolean existsByJti(String jti);

    @Modifying
    @Query("DELETE FROM BlacklistedJWT jwt WHERE jwt.expirationDate < :exp_date")
    void cleanUpExpiredTokens(@Param("exp_date") Instant nowTime);
}
