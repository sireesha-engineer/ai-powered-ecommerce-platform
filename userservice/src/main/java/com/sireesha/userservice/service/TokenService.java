package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.*;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
    String generateAccessToken(User user);

    Claims extractAllClaims(String token);

    String extractEmail(String token);

    Long extractUserId(String token);

    Role extractRole(String token);

    boolean isTokenExpired(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
