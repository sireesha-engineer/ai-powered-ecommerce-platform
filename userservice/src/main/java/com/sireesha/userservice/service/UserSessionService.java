package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.response.UserSessionResponse;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserSession;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public interface UserSessionService {
    UserSession createSession(User user);
    UserSession rotateRefreshToken(UserSession userSession);

    void revokeSession(String userSession);

    List<UserSessionResponse> getActiveSessions(User user);


    UserSession validateUserSession(String refreshToken);
}
