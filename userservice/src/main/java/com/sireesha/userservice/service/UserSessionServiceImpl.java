package com.sireesha.userservice.service;

import com.sireesha.userservice.config.JwtProperties;
import com.sireesha.userservice.dto.response.UserSessionResponse;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserSession;
import com.sireesha.userservice.exception.AuthenticationException;
import com.sireesha.userservice.repository.UserSessionRepository;
import com.sireesha.userservice.utility.TokenGenerator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final JwtProperties jwtProperties;
    private final TokenGenerator tokenGenerator;

    @Override
    public UserSession createSession(User user) {
        UserSession session = UserSession.builder()
                .user(user)
                .refreshToken(tokenGenerator.generateToken(user))
                .expiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpiration() / 1000))
                .loginAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .revoked(false)
                .build();
        return userSessionRepository.save(session);
    }

    @Override
    public UserSession rotateRefreshToken(UserSession oldSession) {
        oldSession.setRevoked(true);
        oldSession.setLastUsedAt(LocalDateTime.now());
        userSessionRepository.save(oldSession);
        return createSession(oldSession.getUser());
    }

    @Override
    public void revokeSession(String refreshToken) {
        UserSession userSession = userSessionRepository
                .findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() ->
                        new AuthenticationException("Invalid refresh token."));
        userSession.setRevoked(true);
        userSessionRepository.save(userSession);
    }

    @Override
    public List<UserSessionResponse> getActiveSessions(User user) {
        return userSessionRepository
                .findByUserAndRevokedFalseOrderByLoginAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserSessionResponse mapToResponse(UserSession session) {

        return UserSessionResponse.builder()
                .id(session.getId())
                .browser(session.getBrowser())
                .loginAt(session.getLoginAt())
                .lastUsedAt(session.getLastUsedAt())
                .build();
    }

    @Override
    public UserSession validateUserSession(String refreshToken) {
        UserSession session = userSessionRepository
                .findByRefreshTokenAndRevokedFalse(refreshToken)
                .orElseThrow(() ->
                        new AuthenticationException("Invalid refresh token."));
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("Refresh token has expired.");
        }
        session.setLastUsedAt(LocalDateTime.now());
        session.setRevoked(true);
        userSessionRepository.save(session);
        return createSession(session.getUser());
    }
}
