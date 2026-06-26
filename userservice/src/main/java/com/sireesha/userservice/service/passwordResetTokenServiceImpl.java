package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.AppProperties;
import com.sireesha.userservice.entity.PasswordResetToken;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.PasswordResetTokenRepository;
import com.sireesha.userservice.utility.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class passwordResetTokenServiceImpl implements passwordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AppProperties appProperties;
    private final TokenGenerator tokenGenerator;

    @Override
    public void createPasswordResetToken(User user) {
        passwordResetTokenRepository.deleteByUser(user);
        String token = tokenGenerator.generateToken(user);
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken validateToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        if (passwordResetToken.isUsed()) {
            throw new InvalidTokenException("Token has been used");
        }
        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token has expired");
        }
        return passwordResetToken;
    }
}
