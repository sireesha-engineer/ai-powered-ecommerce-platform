package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;

public interface PasswordService {
    void validateOldPassword(User user, String oldPassword);
    void validateNewPassword(User user, String newPassword);
    void validateConfirmNewPassword(String confirmNewPassword, String newPassword);
}
