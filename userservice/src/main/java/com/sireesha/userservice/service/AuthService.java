package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.request.*;
import com.sireesha.userservice.entity.TokenType;
import com.sireesha.userservice.entity.UserToken;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.service.token.TokenService;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.exception.AuthenticationException;
import com.sireesha.userservice.repository.UserRepository;
import com.sireesha.userservice.utility.Helper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CurrentUserService currentUserService;
    private final PasswordPolicyService passwordService;
    private final UserTokenRepository userTokenRepository;
    private final Helper helper;

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        helper.validateAccountStatus(user);
        helper.validateAccountNotVerification(user);
        helper.validateAccountLock(user);
        helper.validateAccountLogin(loginRequest, user);
        return tokenService.createAuthenticationResponse(user);
    }

    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        UserToken refreshToken = userTokenRepository.findByTokenAndTokenType(refreshTokenRequest.getRefreshToken(), TokenType.REFRESH_TOKEN)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        passwordService.validateTokenUsage(refreshToken);
        passwordService.validateTokenExpiry(refreshToken);
        passwordService.validateActiveUserToken(refreshToken);
        User user = refreshToken.getUser();
        return tokenService.createAuthenticationResponse(user, refreshToken);
    }

    @Transactional
    public void logout(@Valid LogoutRequest logoutRequest) {
        UserToken refreshToken = userTokenRepository.findByTokenAndTokenType(logoutRequest.getRefreshToken(), TokenType.REFRESH_TOKEN)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        passwordService.validateTokenUsage(refreshToken);
        passwordService.validateTokenExpiry(refreshToken);
        passwordService.validateActiveUserToken(refreshToken);
        refreshToken.setUsed(true);
        userTokenRepository.save(refreshToken);
    }

    @Transactional
    public void logoutAll() {
        User user = currentUserService.getCurrentUser();
        tokenService.revokeAllByUser(user);
    }

    @Transactional
    public void forgotPassword(@Valid AccountRecoveryRequest accountRecoveryRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(accountRecoveryRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        tokenService.createPasswordResetToken(user);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UserToken userToken = userTokenRepository.findByTokenAndTokenType(resetPasswordRequest.getToken(), TokenType.RESET_TOKEN)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        passwordService.validateTokenUsage(userToken);
        passwordService.validateTokenExpiry(userToken);
        passwordService.validateConfirmNewPassword(resetPasswordRequest.getConfirmNewPassword(), resetPasswordRequest.getNewPassword());
        User user = userToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
        tokenService.revokeAllByUser(user);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest verifyEmailRequest) {
        UserToken userToken = userTokenRepository.findByTokenAndTokenType(verifyEmailRequest.getToken(), TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new AuthenticationException("Token not found"));
        User user = userToken.getUser();
        helper.validateAccountVerification(user);
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
        user.setVerified(true);
        userRepository.save(user);
    }

    @Transactional
    public void verifyResentEmail(@Valid AccountRecoveryRequest accountRecoveryRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(accountRecoveryRequest.getEmail());
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        helper.validateAccountVerification(user);
        tokenService.createEmailVerificationToken(user, TokenType.EMAIL_VERIFICATION);
    }
}

