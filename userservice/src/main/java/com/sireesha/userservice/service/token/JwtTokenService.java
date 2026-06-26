package com.sireesha.userservice.service.token;

import com.sireesha.userservice.config.JwtProperties;
import com.sireesha.userservice.dto.request.LogoutRequest;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.RefreshToken;
import com.sireesha.userservice.entity.Role;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserStatus;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.RefreshTokenRepository;
import com.sireesha.userservice.utility.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenGenerator tokenGenerator;

    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public Role extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(role);
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            return userDetails.getUsername().equals(extractEmail(token))
                    && extractAllClaims(token).getIssuer().equals(jwtProperties.getIssuer());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public AuthenticationResponse createAuthenticationResponse(User user) {
        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(createRefreshToken(user).getToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Override
    public AuthenticationResponse createAuthenticationResponse(User user, RefreshToken refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(rotateRefreshToken(refreshToken).getToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenGenerator.generateToken(user));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpiration()/1000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional()
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("refresh token has been revoked");
        }
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("refresh token has expired");
        }
        User user = refreshToken.getUser();
        if (!Objects.equals(user.getUserStatus(), UserStatus.ACTIVE.name())) {
            throw new InvalidTokenException("User is not active");
        }
        return refreshToken;
    }

    @Override
    @Transactional()
    public RefreshToken rotateRefreshToken(RefreshToken oldRefreshToken) {
        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);
        return createRefreshToken(oldRefreshToken.getUser());
    }

    @Override
    public void revokeRefreshToken(LogoutRequest logoutRequest) {
        RefreshToken refreshToken = validateRefreshToken(logoutRequest.getRefreshToken());
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional()
    public void revokeAllByUser(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
