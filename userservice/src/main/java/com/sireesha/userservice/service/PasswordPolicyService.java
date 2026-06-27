package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;

import java.time.LocalDateTime;

public interface PasswordPolicyService {
    void validateOldPassword(User user, String oldPassword);
    void validateNewPassword(User user, String newPassword);
    void validateConfirmNewPassword(String confirmNewPassword, String newPassword);

    void validateTokenUsage(boolean isUsed);

    void validateTokenExpiry(LocalDateTime userToken);

    void validateActiveUserToken(User user);
}
