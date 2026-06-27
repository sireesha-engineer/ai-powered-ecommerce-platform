package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.UserToken;

public interface EmailService {
    void sendPasswordResetEmail(UserToken userToken);

    void sendEmailVerificationToken(UserToken userToken);
}
