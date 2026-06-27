package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendPasswordResetEmail(UserToken token) {
        log.info("Password Reset Link : http://localhost:8080/reset-password?token={}", token.getToken());
    }

    @Override
    public void sendEmailVerificationToken(UserToken token) {
        log.info("Email Verification Link : http://localhost:8080/verify-email?token={}", token.getToken());
    }
}
