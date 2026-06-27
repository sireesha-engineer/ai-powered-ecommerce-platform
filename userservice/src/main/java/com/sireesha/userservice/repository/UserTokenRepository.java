package com.sireesha.userservice.repository;

import com.sireesha.userservice.entity.TokenType;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByTokenAndTokenType(String token, String tokenType);
    List<UserToken> findByUser(User user);
    void deleteByUser(User user, TokenType resetToken);
    void deleteByToken(String token);

    @Modifying
    @Query("UPDATE UserToken ut SET ut.used = true WHERE ut.user = :user")
    void revokeAllByUser(User user);
}
