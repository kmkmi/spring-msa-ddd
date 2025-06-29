package com.example.user.application.service;

import com.example.user.application.dto.CreateUserRequest;
import com.example.user.application.dto.UserResponse;
import com.example.user.domain.User;
import com.example.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(request.getPassword()) // In real app, this should be encrypted
                .status(User.UserStatus.ACTIVE)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User created with id: {}", savedUser.getId());
        
        return UserResponse.from(savedUser);
    }
    
    public UserResponse getUserById(Long id) {
        log.info("Getting user by id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        return UserResponse.from(user);
    }
    
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        return UserResponse.from(user);
    }
    
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");
        
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateUserStatus(Long id, User.UserStatus status) {
        log.info("Updating user status for id: {} to: {}", id, status);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setStatus(status);
        User updatedUser = userRepository.save(user);
        
        return UserResponse.from(updatedUser);
    }
} 