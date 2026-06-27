package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;
import com.sireesha.userservice.entity.UserToken;

public interface PasswordPolicyService {
    void validateOldPassword(User user, String oldPassword);
    void validateNewPassword(User user, String newPassword);
    void validateConfirmNewPassword(String confirmNewPassword, String newPassword);

    void validateTokenUsage(UserToken userToken);

    void validateTokenExpiry(UserToken userToken);

    void validateActiveUserToken(UserToken refreshToken);
}
