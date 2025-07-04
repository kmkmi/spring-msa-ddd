package com.example.user.application.service;

import com.example.user.domain.UserBase;
import com.example.user.domain.User;
import com.example.user.domain.SocialUser;
import com.example.user.infrastructure.repository.UserRepository;
import com.example.user.infrastructure.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 일반 사용자와 소셜 사용자를 통합 관리하는 서비스
 * UserBase 인터페이스를 활용하여 다형성으로 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommonUserService {
    
    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    
    /**
     * 이메일로 사용자 조회 (일반 사용자와 소셜 사용자 모두 검색)
     */
    public Optional<UserBase> findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);
        
        // 1. 일반 사용자 테이블에서 검색
        Optional<User> regularUser = userRepository.findByEmailAndStatus(email, User.UserStatus.ACTIVE);
        if (regularUser.isPresent()) {
            log.info("Found regular user: {}", email);
            return Optional.of(regularUser.get());
        }
        
        // 2. 소셜 사용자 테이블에서 검색
        Optional<SocialUser> socialUser = socialUserRepository.findByEmailAndStatus(email, SocialUser.UserStatus.ACTIVE);
        if (socialUser.isPresent()) {
            log.info("Found social user: {}", email);
            return Optional.of(socialUser.get());
        }
        
        log.info("User not found: {}", email);
        return Optional.empty();
    }
    
    /**
     * 사용자 역할 목록 조회 (일반 사용자와 소셜 사용자 모두 검색)
     */
    public List<String> getRolesByEmail(String email) {
        Optional<UserBase> userOpt = findUserByEmail(email);
        if (userOpt.isPresent()) {
            UserBase user = userOpt.get();
            return List.of(user.getSpringSecurityRole());
        }
        return List.of();
    }
    
    /**
     * 사용자가 활성 상태인지 확인
     */
    public boolean isUserActive(String email) {
        Optional<UserBase> userOpt = findUserByEmail(email);
        return userOpt.map(UserBase::isActive).orElse(false);
    }
    
    /**
     * 사용자가 특정 역할을 가지고 있는지 확인
     */
    public boolean hasUserRole(String email, String role) {
        Optional<UserBase> userOpt = findUserByEmail(email);
        return userOpt.map(user -> user.hasRole(role)).orElse(false);
    }
    
    /**
     * 사용자가 관리자인지 확인
     */
    public boolean isUserAdmin(String email) {
        Optional<UserBase> userOpt = findUserByEmail(email);
        return userOpt.map(UserBase::isAdmin).orElse(false);
    }
    
    /**
     * 사용자 정보를 Response DTO로 변환
     */
    public Optional<Object> getUserResponseByEmail(String email) {
        Optional<UserBase> userOpt = findUserByEmail(email);
        return userOpt.map(UserBase::toUserResponse);
    }
    
    /**
     * 모든 활성 사용자 조회 (일반 사용자와 소셜 사용자 모두)
     */
    public List<UserBase> getAllActiveUsers() {
        List<UserBase> allUsers = new java.util.ArrayList<>();
        
        // 일반 사용자 추가
        userRepository.findAll().stream()
                .filter(user -> user.isActive())
                .forEach(allUsers::add);
        
        // 소셜 사용자 추가
        socialUserRepository.findAll().stream()
                .filter(user -> user.isActive())
                .forEach(allUsers::add);
        
        return allUsers;
    }
    
    /**
     * 사용자 타입별 통계
     */
    public UserStatistics getUserStatistics() {
        long regularUserCount = userRepository.findAll().stream()
                .filter(user -> user.isActive())
                .count();
        
        long socialUserCount = socialUserRepository.findAll().stream()
                .filter(user -> user.isActive())
                .count();
        
        return UserStatistics.builder()
                .regularUserCount(regularUserCount)
                .socialUserCount(socialUserCount)
                .totalUserCount(regularUserCount + socialUserCount)
                .build();
    }
    
    /**
     * 사용자 통계 DTO
     */
    public static class UserStatistics {
        private final long regularUserCount;
        private final long socialUserCount;
        private final long totalUserCount;
        
        public UserStatistics(long regularUserCount, long socialUserCount, long totalUserCount) {
            this.regularUserCount = regularUserCount;
            this.socialUserCount = socialUserCount;
            this.totalUserCount = totalUserCount;
        }
        
        public long getRegularUserCount() { return regularUserCount; }
        public long getSocialUserCount() { return socialUserCount; }
        public long getTotalUserCount() { return totalUserCount; }
        
        public static UserStatisticsBuilder builder() {
            return new UserStatisticsBuilder();
        }
        
        public static class UserStatisticsBuilder {
            private long regularUserCount;
            private long socialUserCount;
            private long totalUserCount;
            
            public UserStatisticsBuilder regularUserCount(long regularUserCount) {
                this.regularUserCount = regularUserCount;
                return this;
            }
            
            public UserStatisticsBuilder socialUserCount(long socialUserCount) {
                this.socialUserCount = socialUserCount;
                return this;
            }
            
            public UserStatisticsBuilder totalUserCount(long totalUserCount) {
                this.totalUserCount = totalUserCount;
                return this;
            }
            
            public UserStatistics build() {
                return new UserStatistics(regularUserCount, socialUserCount, totalUserCount);
            }
        }
    }
} 