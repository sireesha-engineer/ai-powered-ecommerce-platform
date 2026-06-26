package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.AppProperties;
import com.sireesha.userservice.entity.PasswordResetToken;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class passwordResetTokenServiceImpl implements passwordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AppProperties appProperties;

    @Override
    public void createPasswordResetToken(User user) {
        passwordResetTokenRepository.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
