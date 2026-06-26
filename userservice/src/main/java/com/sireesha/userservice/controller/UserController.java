package com.sireesha.userservice.controller;

import com.sireesha.userservice.dto.request.RegisterUserRequest;
import com.sireesha.userservice.dto.request.ChangePasswordRequest;
import com.sireesha.userservice.dto.response.ApiResponse;
import com.sireesha.userservice.dto.response.SuccessResponse;
import com.sireesha.userservice.dto.request.UpdateUserRequest;
import com.sireesha.userservice.dto.response.UserResponse;
import com.sireesha.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> registerUser(@Valid @RequestBody RegisterUserRequest registerUserRequest) {
        String response = userService.register(registerUserRequest);
        return ResponseEntity.ok(new SuccessResponse(response));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfileUser() {
        UserResponse userResponse = userService.getProfileUser();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("me")
    public ResponseEntity<UserResponse> updateProfileUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateProfileUser(updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserStatus(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(userResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("me/change-password")
    public ResponseEntity<ApiResponse> changeUserPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

}
