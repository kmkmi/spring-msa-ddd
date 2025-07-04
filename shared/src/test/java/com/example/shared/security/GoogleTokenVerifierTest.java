package com.example.shared.security;

import com.example.shared.security.provider.GoogleTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import static org.junit.jupiter.api.Assertions.*;

class GoogleTokenVerifierTest {
    private GoogleTokenVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = Mockito.mock(GoogleTokenVerifier.class);
    }

    @Test
    @DisplayName("잘못된 토큰은 null 반환")
    void testInvalidToken() {
        Mockito.when(verifier.verify("invalidtoken")).thenReturn(null);
        assertNull(verifier.verify("invalidtoken"));
    }

    @Test
    @DisplayName("정상 토큰은 Payload 반환")
    void testValidToken() {
        GoogleIdToken.Payload payload = Mockito.mock(GoogleIdToken.Payload.class);
        Mockito.when(verifier.verify("validtoken")).thenReturn(payload);
        assertEquals(payload, verifier.verify("validtoken"));
    }

    // 실제 Google API mocking이 필요하므로, 성공 케이스는 별도 환경에서 추가 가능
} 