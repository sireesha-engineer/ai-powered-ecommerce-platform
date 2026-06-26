package com.sireesha.userservice.service;

import com.sireesha.userservice.entity.User;

public interface passwordResetTokenService {
    void createPasswordResetToken(User user);
}
