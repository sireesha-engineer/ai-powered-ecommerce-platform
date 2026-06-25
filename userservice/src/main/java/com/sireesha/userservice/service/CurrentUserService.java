package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.Role;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.exception.UserNotFoundException;
import com.sireesha.userservice.repository.UserRepository;
import com.sireesha.userservice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepository;
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findById(customUserDetails.getUser().getId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Role getCurrentUserRole() {
        return getCurrentUser().getRole();
    }
}
