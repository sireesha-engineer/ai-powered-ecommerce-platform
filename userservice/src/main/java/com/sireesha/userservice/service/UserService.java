package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.request.RegisterUserRequest;
import com.sireesha.userservice.dto.request.UpdateUserRequest;
import com.sireesha.userservice.dto.request.ChangePasswordRequest;
import com.sireesha.userservice.dto.response.UserResponse;
import com.sireesha.userservice.entity.*;
import com.sireesha.userservice.exception.UserAlreadyExistException;
import com.sireesha.userservice.exception.UserNotFoundException;
import com.sireesha.userservice.repository.UserRepository;
import com.sireesha.userservice.service.token.TokenService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final PasswordPolicyService passwordService;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Transactional
    public void register(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.getEmail())) {
            throw new UserAlreadyExistException("Email already exists");
        }
        User user = new User();
        user.setFirstName(registerUserRequest.getFirstName());
        user.setLastName(registerUserRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerUserRequest.getPassword()));
        user.setEmail(registerUserRequest.getEmail());
        user.setPhoneNumber(registerUserRequest.getPhoneNumber());
        user.setRole(Role.USER);
        user.setIsVerified(false);

        userRepository.save(user);

        UserToken userToken = tokenService.createEmailVerificationToken(user, TokenType.EMAIL_VERIFICATION);
        emailService.sendEmailVerificationToken(userToken);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findByUserStatus(UserStatus.ACTIVE);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse userResponse = convertToResponse(user);
            userResponses.add(userResponse);
        }
        return userResponses;
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        return userResponse;
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToResponse(user);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (updateUserRequest.getFirstName() != null) user.setFirstName(updateUserRequest.getFirstName());
        if (updateUserRequest.getLastName() != null) user.setLastName(updateUserRequest.getLastName());
        if (updateUserRequest.getPhoneNumber() != null) user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        if (updateUserRequest.getUserStatus() != null) user.setUserStatus(updateUserRequest.getUserStatus());

        userRepository.save(user);

        return convertToResponse(user);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public UserResponse getProfileUser() {
       return convertToResponse(currentUserService.getCurrentUser());
    }

    public UserResponse updateProfileUser(UpdateUserRequest updateUserRequest) {
        User user = currentUserService.getCurrentUser();

        if (updateUserRequest.getFirstName() != null) user.setFirstName(updateUserRequest.getFirstName());
        if (updateUserRequest.getLastName() != null) user.setLastName(updateUserRequest.getLastName());
        if (updateUserRequest.getPhoneNumber() != null) user.setPhoneNumber(updateUserRequest.getPhoneNumber());

        userRepository.save(user);

        return convertToResponse(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = currentUserService.getCurrentUser();
        passwordService.validateOldPassword(user, changePasswordRequest.getOldPassword());
        passwordService.validateNewPassword(user, changePasswordRequest.getNewPassword());
        passwordService.validateConfirmNewPassword(changePasswordRequest.getConfirmNewPassword(), changePasswordRequest.getNewPassword());
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        tokenService.revokeAllByUser(user);
    }

    public void updateUserStatus(Long id, @Valid UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (updateUserRequest.getUserStatus() != null) user.setUserStatus(updateUserRequest.getUserStatus());
        userRepository.save(user);
    }

    public void updateUserRole(Long id, @Valid UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (updateUserRequest.getRole() != null) user.setRole(updateUserRequest.getRole());
        userRepository.save(user);
    }
}
