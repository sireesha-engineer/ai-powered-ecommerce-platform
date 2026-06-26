package com.sireesha.userservice.utility;

import com.sireesha.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenGenerator {
    public String generateToken(User user) {
        return  UUID.randomUUID().toString();
    }
}
