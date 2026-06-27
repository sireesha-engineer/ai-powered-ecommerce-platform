package com.sireesha.userservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSessionResponse {
    private Long id;
    private String browser;
    private LocalDateTime loginAt;
    private LocalDateTime lastUsedAt;
}
