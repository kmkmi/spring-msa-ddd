package com.example.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("로컬 로그인 성공")
    void testLocalLoginSuccess() throws Exception {
        // given: 선행 유저 생성
        String email = "loginuser@example.com";
        String password = "password123";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+email+"\",\"name\":\"Login User\",\"password\":\""+password+"\",\"role\":\"USER\"}"));
        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+email+"\",\"password\":\""+password+"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("로컬 로그인 실패(잘못된 비밀번호)")
    void testLocalLoginFail() throws Exception {
        String email = "failuser@example.com";
        String password = "password123";
        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+email+"\",\"name\":\"Fail User\",\"password\":\""+password+"\",\"role\":\"USER\"}"));
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\""+email+"\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("소셜 로그인 실패(잘못된 provider token)")
    void testSocialLoginFail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/social")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"provider\":\"google\",\"providerToken\":\"invalidtoken\",\"clientId\":\"test-client-id\"}"))
                .andExpect(status().isUnauthorized());
    }

    // 실제 GoogleTokenVerifier mocking이 필요하므로, 성공 케이스는 별도 환경에서 추가 가능
} 