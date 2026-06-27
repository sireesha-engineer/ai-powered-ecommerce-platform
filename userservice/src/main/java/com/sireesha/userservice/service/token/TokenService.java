package com.sireesha.userservice.service.token;

import com.sireesha.userservice.dto.request.LogoutRequest;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.Role;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;
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
    AuthenticationResponse createAuthenticationResponse(User user);
    AuthenticationResponse createAuthenticationResponse(User user, UserToken refreshToken);
    UserToken createRefreshToken(User user);
    UserToken validateRefreshToken(String token);
    UserToken rotateRefreshToken(UserToken refreshToken);
    void revokeRefreshToken(LogoutRequest refreshToken);
    void revokeAllByUser(User user);

}
