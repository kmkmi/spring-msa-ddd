package com.example.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Override
    public String getStatus() {
        return this.status.name();
    }
    
    @Override
    public String getRole() {
        return this.role.name();
    }
    
    @Override
    public UserType getUserType() {
        return UserType.REGULAR;
    }
    
    @Override
    public UserResponse toUserResponse() {
        return UserResponse.builder()
                .id(this.id)
                .email(this.email)
                .status(this.getStatus())
                .role(this.getRole())
                .userType(this.getUserType().name())
                .createdAt(this.createdAt)
                .build();
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
    
    public enum UserRole {
        ADMIN, PUBLISHER, ADVERTISER, USER
    }
    
    // UserResponse 내부 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String email;
        private String status;
        private String role;
        private String userType;
        private LocalDateTime createdAt;
    }
} 