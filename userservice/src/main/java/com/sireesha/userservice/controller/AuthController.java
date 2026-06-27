package com.sireesha.userservice.controller;

import com.sireesha.userservice.dto.request.*;
import com.sireesha.userservice.dto.response.ApiResponse;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResponse authenticationResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthenticationResponse authenticationResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logout(logoutRequest);
        return ResponseEntity.ok(ApiResponse.success("Logout successfully"));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse> logoutAll() {
        authService.logoutAll();
        return ResponseEntity.ok(ApiResponse.success("Logout from all devices"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody AccountRecoveryRequest accountRecoveryRequest) {
        authService.forgotPassword(accountRecoveryRequest);
        return ResponseEntity.ok(ApiResponse.success("if account exists, a password reset link has been sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest verifyEmailRequest) {
        authService.verifyEmail(verifyEmailRequest);
        return ResponseEntity.ok(ApiResponse.success("Email verification successfully"));
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<ApiResponse> verifyEmail(@Valid @RequestBody AccountRecoveryRequest accountRecoveryRequest) {
        authService.verifyResentEmail(accountRecoveryRequest);
        return ResponseEntity.ok(ApiResponse.success("If account exists, a verification email has been sent."));
    }
}
