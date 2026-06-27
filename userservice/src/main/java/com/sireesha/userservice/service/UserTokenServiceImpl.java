package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.TokenType;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.utility.AppProperties;
import com.sireesha.userservice.utility.TokenGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {
    private final UserTokenRepository userTokenRepository;
    private final TokenGenerator tokenGenerator;
    private final AppProperties appProperties;

    @Override
    public void createPasswordResetToken(User user) {
        userTokenRepository.deleteByUser(user, TokenType.RESET_TOKEN);
        String token = tokenGenerator.generateToken(user);
        UserToken passwordResetToken = UserToken.builder()
                .token(token)
                .tokenType(TokenType.RESET_TOKEN)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        userTokenRepository.save(passwordResetToken);
    }

    @Override
    public UserToken createEmailVerificationToken(User user, TokenType tokenType) {
        userTokenRepository.deleteByUser(user, tokenType);
        String token = tokenGenerator.generateToken(user);
        UserToken emailVerifyToken = UserToken.builder()
                .token(token)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(appProperties.getPasswordResetExpiryMinutes()))
                .build();
        userTokenRepository.save(emailVerifyToken);
        return emailVerifyToken;
    }
}
