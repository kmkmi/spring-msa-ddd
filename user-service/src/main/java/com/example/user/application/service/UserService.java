package com.example.user.application.service;

import com.example.user.application.dto.CreateUserRequest;
import com.example.user.domain.User;
import com.example.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.user.common.exception.AuthenticationFailedException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User.UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(User.UserStatus.ACTIVE)
                .role(request.getRole())
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());
        
        return convertToUserResponse(savedUser);
    }
    
    public User.UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        return convertToUserResponse(user);
    }
    
    public List<User.UserResponse> getAllUsers() {
        log.info("Getting all users");
        
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    public boolean authenticateUser(String email, String password) {
        try {
            User user = userRepository.findByEmailAndStatus(email, User.UserStatus.ACTIVE)
                    .orElseThrow(() -> new AuthenticationFailedException("User not found with email: " + email));
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new AuthenticationFailedException("Invalid password");
            }
            return true;
        } catch (AuthenticationFailedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", email, e);
            throw new AuthenticationFailedException("Authentication failed");
        }
    }
    
    public User.UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        
        User user = userRepository.findByEmailAndStatus(email, User.UserStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        return convertToUserResponse(user);
    }
    
    @Transactional
    public User.UserResponse updateUserStatus(Long id, User.UserStatus status) {
        log.info("Updating user status for id: {} to: {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        
        return convertToUserResponse(updatedUser);
    }
    
    public boolean isOwner(Long id, String email) {
        return userRepository.findById(id)
                .map(user -> user.getEmail().equals(email))
                .orElse(false);
    }
    
    public List<String> getRolesByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, User.UserStatus.ACTIVE)
                .map(user -> List.of("ROLE_" + user.getRole()))
                .orElse(List.of());
    }
    
    private User.UserResponse convertToUserResponse(User user) {
        return user.toUserResponse();
    }
} 