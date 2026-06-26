package com.sireesha.userservice.service;

import com.sireesha.userservice.dto.request.ChangePasswordRequest;
import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.exception.PasswordException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PasswordPolicyServiceImpl implements PasswordPolicyService {
    private final PasswordEncoder passwordEncoder;

    @Override
    public void validateOldPassword(User user, String oldPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordException("Old password is not correct");
        }
    }

    @Override
    public void validateNewPassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new PasswordException("New password should not be the same as the old password");
        }
    }

    @Override
    public void validateConfirmNewPassword(String confirmPassword, String newPassword) {
        if (!confirmPassword.equals(newPassword)) {
            throw new PasswordException(("confirm password should be the same as the new password"));
        }
    }

    public void validatePassword(User user, ChangePasswordRequest changePasswordRequest) {
        validateOldPassword(user, changePasswordRequest.getOldPassword());
        validateNewPassword(user, changePasswordRequest.getNewPassword());
        validateConfirmNewPassword(changePasswordRequest.getConfirmNewPassword(), changePasswordRequest.getNewPassword());
    }
}
