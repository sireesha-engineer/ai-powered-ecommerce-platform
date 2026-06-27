package com.sireesha.userservice.dto.request;

import com.sireesha.userservice.entity.Role;
import com.sireesha.userservice.entity.UserStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Invalid phone number")
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private Role role;
}
