package com.example.user.application.service;

import com.example.user.domain.SocialUser;
import com.example.user.infrastructure.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SocialUserService {
    
    private final SocialUserRepository socialUserRepository;
    
    @Transactional
    public SocialUser.SocialUserResponse createSocialUser(String email, String name, 
                                                        SocialUser.SocialProvider provider, 
                                                        String providerId, 
                                                        String profileImageUrl) {
        log.info("Creating social user with email: {}, provider: {}", email, provider);
        
        SocialUser socialUser = SocialUser.builder()
                .email(email)
                .name(name)
                .provider(provider)
                .providerId(providerId)
                .profileImageUrl(profileImageUrl)
                .status(SocialUser.UserStatus.ACTIVE)
                .role(SocialUser.UserRole.USER)
                .lastLoginAt(LocalDateTime.now())
                .build();
        
        SocialUser savedSocialUser = socialUserRepository.save(socialUser);
        log.info("Social user created with id: {}", savedSocialUser.getId());
        
        return convertToSocialUserResponse(savedSocialUser);
    }
    
    public Optional<SocialUser.SocialUserResponse> getSocialUserByEmail(String email) {
        log.info("Getting social user by email: {}", email);
        
        return socialUserRepository.findByEmailAndStatus(email, SocialUser.UserStatus.ACTIVE)
                .map(this::convertToSocialUserResponse);
    }
    
    public Optional<SocialUser.SocialUserResponse> getSocialUserByProviderAndProviderId(
            SocialUser.SocialProvider provider, String providerId) {
        log.info("Getting social user by provider: {} and providerId: {}", provider, providerId);
        
        return socialUserRepository.findByProviderAndProviderId(provider, providerId)
                .map(this::convertToSocialUserResponse);
    }
    
    @Transactional
    public SocialUser.SocialUserResponse updateLastLogin(String email) {
        log.info("Updating last login for social user: {}", email);
        
        SocialUser socialUser = socialUserRepository.findByEmailAndStatus(email, SocialUser.UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Social user not found with email: " + email));
        
        socialUser.setLastLoginAt(LocalDateTime.now());
        SocialUser updatedSocialUser = socialUserRepository.save(socialUser);
        
        return convertToSocialUserResponse(updatedSocialUser);
    }
    
    public List<String> getRolesByEmail(String email) {
        return socialUserRepository.findByEmailAndStatus(email, SocialUser.UserStatus.ACTIVE)
                .map(socialUser -> List.of("ROLE_" + socialUser.getRole()))
                .orElse(List.of());
    }
    
    public boolean existsByEmail(String email) {
        return socialUserRepository.existsByEmail(email);
    }
    
    public boolean existsByProviderAndProviderId(SocialUser.SocialProvider provider, String providerId) {
        return socialUserRepository.existsByProviderAndProviderId(provider, providerId);
    }
    
    public SocialUser.SocialUserResponse getSocialUserById(Long id) {
        log.info("Getting social user by id: {}", id);
        
        SocialUser socialUser = socialUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social user not found with id: " + id));
        
        return convertToSocialUserResponse(socialUser);
    }
    
    public List<SocialUser.SocialUserResponse> getAllSocialUsers() {
        log.info("Getting all social users");
        
        return socialUserRepository.findAll().stream()
                .map(this::convertToSocialUserResponse)
                .collect(Collectors.toList());
    }
    
    private SocialUser.SocialUserResponse convertToSocialUserResponse(SocialUser socialUser) {
        return SocialUser.SocialUserResponse.builder()
                .id(socialUser.getId())
                .email(socialUser.getEmail())
                .name(socialUser.getName())
                .provider(socialUser.getProvider().name())
                .profileImageUrl(socialUser.getProfileImageUrl())
                .status(socialUser.getStatus())
                .role(socialUser.getRole())
                .lastLoginAt(socialUser.getLastLoginAt())
                .createdAt(socialUser.getCreatedAt())
                .build();
    }
} 