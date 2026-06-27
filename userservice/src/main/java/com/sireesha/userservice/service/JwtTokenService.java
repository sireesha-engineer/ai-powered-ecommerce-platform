package com.sireesha.userservice.service;

import com.sireesha.userservice.config.JwtProperties;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.*;
import com.sireesha.userservice.repository.UserSessionRepository;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.utility.AppProperties;
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

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {
    private final JwtProperties jwtProperties;
    private final UserTokenRepository userTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final TokenGenerator tokenGenerator;
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
}
