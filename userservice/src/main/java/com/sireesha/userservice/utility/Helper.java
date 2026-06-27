package com.sireesha.userservice.utility;

import com.sireesha.userservice.dto.request.LoginRequest;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserStatus;
import com.sireesha.userservice.exception.AuthenticationException;
import com.sireesha.userservice.exception.BusinessException;
import com.sireesha.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Helper {
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final UserRepository userRepository;
    public void validateUserStatus(User user) {
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new BusinessException("User account has been deleted.");
        }

        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new BusinessException("User account has been suspended.");
        }

        if (!user.isVerified()) {
            throw new BusinessException("Please verify your email before logging in.");
        }
    }

    public void validateAccountLock(User user) {
        LocalDateTime lockedUntil = user.getAccountLockedUntil();
        if (lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now())) {
            throw new AuthenticationException("Account is temporarily locked. Please try again later.");
        }
    }

    public void validateAccountVerification(User user) {
        if (user.isVerified()) {
            throw new AuthenticationException("Email already verified");
        }
    }

    public void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= appProperties.getMaxFailedLoginAttempts()) {
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(appProperties.getLockDurationInMinutes()));
            user.setFailedLoginAttempts(0);
        }
        user.setFailedLoginAttempts(attempts);
        userRepository.save(user);
        throw new AuthenticationException("Invalid email or password");
    }

    public void resetFailedLoginAttempts(User user) {
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }
}
