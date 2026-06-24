package com.sireesha.userservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @Pattern(regexp = "^[A-Za-z0-9+-._]+@[A-Za-z0-9+-]+\\.[A-Za-z]{2,}", message = "Invalid email domain")
    private String email;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,20}$", message = "Password must contain at least one letter and one digit")
    private String password;
}
