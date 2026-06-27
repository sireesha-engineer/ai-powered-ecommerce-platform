package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;

public interface passwordResetTokenService {
    void createPasswordResetToken(User user);
    UserToken validateToken(String token);
}
