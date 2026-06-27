package com.sireesha.userservice.service.token;

import com.sireesha.userservice.config.JwtProperties;
import com.sireesha.userservice.dto.request.LogoutRequest;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.*;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.service.PasswordPolicyService;
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
    private final UserTokenRepository userTokenRepository;
    private final TokenGenerator tokenGenerator;
    private final PasswordPolicyService passwordPolicyService;
    private final AppProperties appProperties;

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
    public AuthenticationResponse createAuthenticationResponse(User user, UserToken refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(rotateRefreshToken(refreshToken).getToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Override
    @Transactional
    public UserToken createRefreshToken(User user) {
        UserToken refreshToken = new UserToken();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenGenerator.generateToken(user));
        refreshToken.setTokenType(TokenType.REFRESH_TOKEN.name());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpiration()/1000));
        return userTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional()
    public UserToken validateRefreshToken(String token) {
        UserToken refreshToken = userTokenRepository.findByTokenAndTokenType(token, TokenType.REFRESH_TOKEN.name())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        passwordPolicyService.validateTokenUsage(refreshToken);
        passwordPolicyService.validateTokenExpiry(refreshToken);
        passwordPolicyService.validateActiveUserToken(refreshToken);
        return refreshToken;
    }

    @Override
    @Transactional()
    public UserToken rotateRefreshToken(UserToken oldRefreshToken) {
        oldRefreshToken.setUsed(true);
        userTokenRepository.save(oldRefreshToken);
        return createRefreshToken(oldRefreshToken.getUser());
    }

    @Override
    public void revokeRefreshToken(LogoutRequest logoutRequest) {
        UserToken refreshToken = validateRefreshToken(logoutRequest.getRefreshToken());
        refreshToken.setUsed(true);
        userTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional()
    public void revokeAllByUser(User user) {
        userTokenRepository.revokeAllByUser(user);
    }

    @Override
    public void createPasswordResetToken(User user) {
        userTokenRepository.deleteByUser(user, TokenType.RESET_TOKEN);
        String token = tokenGenerator.generateToken(user);
        UserToken passwordResetToken = UserToken.builder()
                .token(token)
                .tokenType(TokenType.RESET_TOKEN.name())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        userTokenRepository.save(passwordResetToken);
    }

    @Override
    public UserToken validateToken(String token, String tokenType) {
        UserToken userToken = userTokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        passwordPolicyService.validateTokenUsage(userToken);
        passwordPolicyService.validateTokenExpiry(userToken);
        return userToken;
    }

    @Override
    public UserToken createEmailVerificationToken(User user, String tokenType) {
        return null;
    }
}
