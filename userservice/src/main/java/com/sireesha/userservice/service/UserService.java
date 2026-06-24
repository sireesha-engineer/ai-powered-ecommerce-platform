package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.RegisterUserRequest;
import com.sireesha.userservice.entity.Role;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String register(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.getEmail())) {
            return "Email already exists";
        }
        User user = new User();
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setPassword(registerUserRequest.getPassword());
        user.setEmail(registerUserRequest.getEmail());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        user.setRole(Role.USER);
        user.setIsVerified(false);

        userRepository.save(user);
        return "User registered successfully";
    }
}
