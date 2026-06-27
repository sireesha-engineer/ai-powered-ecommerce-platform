package com.sireesha.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tokens")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;
}
