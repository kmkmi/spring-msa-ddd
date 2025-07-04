package com.example.publisher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/publishers/health는 인증 없이 200 OK 반환")
    void testHealthPermitAll() throws Exception {
        mockMvc.perform(get("/publishers/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("JWT 미포함시 인증 필요한 /publishers는 401 Unauthorized")
    void testNoJwtUnauthorized() throws Exception {
        mockMvc.perform(get("/publishers"))
                .andExpect(status().isUnauthorized());
    }

    // JWT 유효성 실패, 인가 실패 등은 실제 JWT 발급/조작이 필요하므로, 통합 환경에서 추가 가능
} 