package com.sireesha.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String refreshToken;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private LocalDateTime loginAt;
    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastUsedAt = LocalDateTime.now();
    private String browser;
    private String ipAddress;

}
