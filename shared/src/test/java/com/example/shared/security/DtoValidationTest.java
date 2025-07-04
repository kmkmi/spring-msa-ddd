package com.example.shared.security;

import com.example.shared.security.dto.AuthRequest;
import com.example.shared.security.dto.AuthResponse;
import com.example.shared.security.dto.SocialLoginRequest;
import com.example.shared.security.dto.RefreshTokenRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Validator validator;
    public DtoValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AuthRequest 유효성 검사")
    void testAuthRequestValidation() {
        AuthRequest req = new AuthRequest();
        req.setEmail("invalid");
        req.setPassword("");
        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("AuthResponse 직렬화/역직렬화")
    void testAuthResponseSerialization() throws Exception {
        AuthResponse resp = AuthResponse.of("token", "refresh", "user@example.com", java.util.List.of("USER"), 3600L);
        String json = objectMapper.writeValueAsString(resp);
        AuthResponse deserialized = objectMapper.readValue(json, AuthResponse.class);
        assertEquals(resp.getAccessToken(), deserialized.getAccessToken());
    }

    @Test
    @DisplayName("SocialLoginRequest 유효성 검사")
    void testSocialLoginRequestValidation() {
        SocialLoginRequest req = new SocialLoginRequest();
        req.setProvider(null);
        req.setProviderToken("");
        Set<ConstraintViolation<SocialLoginRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("RefreshTokenRequest 유효성 검사")
    void testRefreshTokenRequestValidation() {
        RefreshTokenRequest req = new RefreshTokenRequest();
        req.setRefreshToken("");
        Set<ConstraintViolation<RefreshTokenRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }
} 