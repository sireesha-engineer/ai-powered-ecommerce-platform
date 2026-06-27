package com.sireesha.userservice.service;

import com.sireesha.userservice.config.JwtProperties;
import com.sireesha.userservice.dto.request.*;
import com.sireesha.userservice.dto.response.RefreshResponse;
import com.sireesha.userservice.dto.response.UserSessionResponse;
import com.sireesha.userservice.entity.*;
import com.sireesha.userservice.exception.InvalidTokenException;
import com.sireesha.userservice.repository.UserSessionRepository;
import com.sireesha.userservice.repository.UserTokenRepository;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.exception.AuthenticationException;
import com.sireesha.userservice.repository.UserRepository;
import com.sireesha.userservice.utility.Helper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserTokenService userTokenService;
    private final UserSessionService userSessionService;
    private final CurrentUserService currentUserService;
    private final PasswordPolicyService passwordService;
    private final UserTokenRepository userTokenRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtProperties jwtProperties;
    private final Helper helper;

    @Transactional
    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
        helper.validateUserStatus(user);
        helper.validateAccountLock(user);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            helper.handleFailedLogin(user);
        }
        helper.resetFailedLoginAttempts(user);
        String accessToken = tokenService.generateAccessToken(user);
        UserSession userSession = userSessionService.createSession(user);
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(userSession.getRefreshToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    @Transactional
    public RefreshResponse refreshToken(UserSessionRequest userSessionRequest) {
        UserSession userSession = userSessionService.validateUserSession(userSessionRequest.getRefreshToken());
        String accessToken = tokenService.generateAccessToken(userSession.getUser());
        return RefreshResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Transactional
    public void logout(LogoutRequest logoutRequest) {
       userSessionService.revokeSession(logoutRequest.getRefreshToken());
    }

    @Transactional
    public void logoutAll() {
        User user = currentUserService.getCurrentUser();
        userSessionRepository.revokeAllSessions(user);
    }

    @Transactional
    public void forgotPassword(@Valid AccountRecoveryRequest accountRecoveryRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(accountRecoveryRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        userTokenService.createPasswordResetToken(user);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        UserToken userToken = userTokenRepository.findByTokenAndTokenType(resetPasswordRequest.getToken(), TokenType.RESET_TOKEN)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        passwordService.validateTokenUsage(userToken.isUsed());
        passwordService.validateTokenExpiry(userToken.getExpiresAt());
        passwordService.validateConfirmNewPassword(resetPasswordRequest.getConfirmNewPassword(), resetPasswordRequest.getNewPassword());
        User user = userToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
        userTokenRepository.revokeAllUsers(user);
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
        userTokenService.createEmailVerificationToken(user, TokenType.EMAIL_VERIFICATION);
    }

    @Transactional()
    public List<UserSessionResponse> getActiveSessions() {
        User currentUser = currentUserService.getCurrentUser();
        return userSessionService.getActiveSessions(currentUser);
    }
}

