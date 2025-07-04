package com.example.shared.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String privateKeyPath = "src/test/resources/keys/private_key.pem";
        String publicKeyPath = "src/test/resources/keys/public_key.pem";
        jwtTokenProvider = new JwtTokenProvider(privateKeyPath, publicKeyPath);
        // secret 설정 제거 (RS256은 키 파일 기반)
        jwtTokenProvider.jwtExpirationMs = 1000 * 60 * 60; // 1시간
    }

    @Test
    void generateAndValidateToken() {
        UserDetails user = User.withUsername("testuser").password("pw").roles("USER").build();
        List<String> roles = user.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toList());
        String token = jwtTokenProvider.generateToken(user.getUsername(), roles);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void expiredTokenShouldBeInvalid() throws InterruptedException {
        jwtTokenProvider.jwtExpirationMs = 1; // 1ms
        UserDetails user = User.withUsername("expired").password("pw").roles("USER").build();
        List<String> expiredRoles = user.getAuthorities().stream().map(a -> a.getAuthority()).collect(java.util.stream.Collectors.toList());
        String token = jwtTokenProvider.generateToken(user.getUsername(), expiredRoles);
        Thread.sleep(5);
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void invalidTokenShouldBeInvalid() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.value"));
    }
} 