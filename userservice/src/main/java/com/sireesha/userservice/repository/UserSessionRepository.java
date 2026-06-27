package com.sireesha.userservice.repository;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserSession;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByRefreshTokenAndRevokedFalse(String refreshToken);
    List<UserSession> findByUserAndRevokedFalse(User user);

    void deleteByUser(User user);

    @Modifying
    @Query("UPDATE UserSession us SET us.revoked = true WHERE us.user = :user")
    void revokeAllSessions(User user);

    Optional<UserSession> findByRefreshToken(String refreshToken);
    List<UserSession> findByUserAndRevokedFalseOrderByLoginAtDesc(User user);
}
