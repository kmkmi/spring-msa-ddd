package com.example.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 사용자 엔티티들의 공통 인터페이스
 * 일반 사용자와 소셜 사용자가 공통으로 구현해야 하는 메서드들을 정의
 */
public interface UserBase {
    
    /**
     * 사용자 ID 반환
     */
    Long getId();
    
    /**
     * 사용자 이메일 반환
     */
    String getEmail();
    
    /**
     * 사용자 상태 반환
     */
    String getStatus();
    
    /**
     * 사용자 역할 반환
     */
    String getRole();
    
    /**
     * 생성 시간 반환
     */
    LocalDateTime getCreatedAt();
    
    /**
     * 수정 시간 반환
     */
    LocalDateTime getUpdatedAt();
    
    /**
     * 사용자가 활성 상태인지 확인
     */
    default boolean isActive() {
        return "ACTIVE".equals(getStatus());
    }
    
    /**
     * 사용자가 특정 역할을 가지고 있는지 확인
     */
    default boolean hasRole(String role) {
        return role.equals(getRole());
    }
    
    /**
     * 사용자가 관리자인지 확인
     */
    default boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * 사용자 타입 반환 (일반/소셜)
     */
    UserType getUserType();
    
    /**
     * 사용자 정보를 Response DTO로 변환
     */
    Object toUserResponse();
    
    /**
     * 사용자 역할을 Spring Security 형식으로 반환
     */
    default String getSpringSecurityRole() {
        return "ROLE_" + getRole();
    }
    
    /**
     * JSON 직렬화를 위한 userType 필드
     */
    @JsonProperty("userType")
    default String getUserTypeAsString() {
        return getUserType().name();
    }
    
    /**
     * 사용자 타입 열거형
     */
    enum UserType {
        REGULAR, SOCIAL
    }
    
    /**
     * 공통 사용자 응답 DTO
     */
    interface UserResponse {
        Long getId();
        String getEmail();
        String getStatus();
        String getRole();
        String getUserType();
        LocalDateTime getCreatedAt();
    }
} 