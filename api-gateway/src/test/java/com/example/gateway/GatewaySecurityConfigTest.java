package com.example.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class GatewaySecurityConfigTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("JWT 미포함시 401 Unauthorized")
    void testNoJwtUnauthorized() {
        webTestClient.get().uri("/api/anything")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // JWT 유효성 실패, 인가 실패 등은 실제 JWT 발급/조작이 필요하므로, 통합 환경에서 추가 가능
} 