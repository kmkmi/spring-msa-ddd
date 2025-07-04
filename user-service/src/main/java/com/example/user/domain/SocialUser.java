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
@Table(name = "social_users")
@EntityListeners(AuditingEntityListener.class)
public class SocialUser implements UserBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialProvider provider;
    
    @Column(name = "provider_id", nullable = false)
    private String providerId; // 소셜 제공자의 고유 ID (예: Google의 sub)
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
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
        return UserType.SOCIAL;
    }
    
    @Override
    public Object toUserResponse() {
        return SocialUserResponse.builder()
                .id(this.id)
                .email(this.email)
                .name(this.name)
                .provider(this.provider.name())
                .profileImageUrl(this.profileImageUrl)
                .status(this.getStatus())
                .role(this.getRole())
                .userType(this.getUserType().name())
                .lastLoginAt(this.lastLoginAt)
                .createdAt(this.createdAt)
                .build();
    }
    
    public enum SocialProvider {
        GOOGLE, NAVER, KAKAO, FACEBOOK, APPLE
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED, DELETED
    }
    
    public enum UserRole {
        ADMIN, PUBLISHER, ADVERTISER, USER
    }
    
    // SocialUserResponse 내부 클래스
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialUserResponse {
        private Long id;
        private String email;
        private String name;
        private String provider;
        private String profileImageUrl;
        private String status;
        private String role;
        private String userType;
        private LocalDateTime lastLoginAt;
        private LocalDateTime createdAt;
    }
} 