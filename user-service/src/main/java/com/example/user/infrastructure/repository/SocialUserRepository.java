package com.example.user.infrastructure.repository;

import com.example.user.domain.SocialUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialUserRepository extends JpaRepository<SocialUser, Long> {
    
    Optional<SocialUser> findByEmail(String email);
    
    Optional<SocialUser> findByEmailAndStatus(String email, SocialUser.UserStatus status);
    
    Optional<SocialUser> findByProviderAndProviderId(SocialUser.SocialProvider provider, String providerId);
    
    Optional<SocialUser> findByEmailAndProvider(String email, SocialUser.SocialProvider provider);
    
    boolean existsByEmail(String email);
    
    boolean existsByProviderAndProviderId(SocialUser.SocialProvider provider, String providerId);
} 