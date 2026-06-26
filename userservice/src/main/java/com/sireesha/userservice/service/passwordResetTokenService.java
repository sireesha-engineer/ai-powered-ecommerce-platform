package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.PasswordResetToken;
import com.sireesha.userservice.entity.User;

public interface passwordResetTokenService {
    void createPasswordResetToken(User user);
    PasswordResetToken validateToken(String token);
}
