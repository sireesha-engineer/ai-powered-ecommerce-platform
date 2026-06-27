package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.AppProperties;
import com.sireesha.userservice.entity.TokenType;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.utility.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class passwordResetTokenServiceImpl implements passwordResetTokenService {
    private final UserTokenRepository userTokenRepository;
    private final AppProperties appProperties;
    private final TokenGenerator tokenGenerator;

    @Override
    public void createPasswordResetToken(User user) {
        userTokenRepository.deleteByUser(user, TokenType.RESET_TOKEN);
        String token = tokenGenerator.generateToken(user);
        UserToken passwordResetToken = UserToken.builder()
                .token(token)
                .tokenType(TokenType.RESET_TOKEN.name())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        userTokenRepository.save(passwordResetToken);
    }

    @Override
    public UserToken validateToken(String token) {
        UserToken passwordResetToken = userTokenRepository.findByTokenAndTokenType(token, TokenType.RESET_TOKEN.name())
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
