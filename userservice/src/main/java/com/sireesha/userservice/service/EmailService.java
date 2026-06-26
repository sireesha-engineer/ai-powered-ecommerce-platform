package com.sireesha.userservice.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String token);
}
