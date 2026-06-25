package com.sireesha.userservice.service;

import com.sireesha.userservice.service.token.TokenService;
import com.sireesha.userservice.dto.response.AuthenticationResponse;
import com.sireesha.userservice.dto.request.LoginRequest;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserStatus;
import com.sireesha.userservice.exception.AuthenticationException;
import com.sireesha.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticationResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (user.getUserStatus().contains(UserStatus.DELETED.name())) {
            throw new AuthenticationException("Invalid email or password");
        }

        boolean isValidPassword = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!isValidPassword) {
            throw new AuthenticationException("Invalid email or password");
        }

        return tokenService.createAuthenticationResponse(user);
    }
}

