package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.TokenType;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;

public interface UserTokenService {
    void createPasswordResetToken(User user);
    UserToken createEmailVerificationToken(User user, TokenType tokenType);
}
