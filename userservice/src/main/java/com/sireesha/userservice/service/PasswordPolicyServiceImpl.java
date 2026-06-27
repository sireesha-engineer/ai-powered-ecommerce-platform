package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserStatus;
import com.sireesha.userservice.entity.UserToken;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.exception.PasswordException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PasswordPolicyServiceImpl implements PasswordPolicyService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public void validateOldPassword(User user, String oldPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordException("Old password is not correct");
        }
    }

    @Override
    public void validateNewPassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordException("New password should not be the same as the old password");
        }
    }

    @Override
    public void validateConfirmNewPassword(String confirmPassword, String newPassword) {
        if (!confirmPassword.equals(newPassword)) {
            throw new PasswordException(("confirm password should be the same as the new password"));
        }
    }

    @Override
    public void validateTokenUsage(boolean isUsed) {
        if (isUsed) {
            throw new InvalidTokenException("Token has been used");
        }
    }

    @Override
    public void validateTokenExpiry(LocalDateTime expiryTime) {
        if (expiryTime.isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token has expired");
        }
    }

    @Override
    public void validateActiveUserToken(User user) {
        if (!UserStatus.ACTIVE.equals(user.getUserStatus())) {
            throw new InvalidTokenException("User is not active");
        }
    }
}
