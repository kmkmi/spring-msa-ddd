package com.example.adrecommendation;

import com.example.adrecommendation.dto.AdRecommendationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdRecommendationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/recommendations/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ad Recommendation Service is running!"));
    }

    @Test
    void testGetRecommendedAds() throws Exception {
        AdRecommendationRequest request = AdRecommendationRequest.builder()
                .userId("user123")
                .sessionId("session456")
                .deviceType("MOBILE")
                .location("Seoul")
                .placement("TOP_BANNER")
                .limit(3)
                .build();

        mockMvc.perform(post("/recommendations/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void testGetRecommendedAdsValidation() throws Exception {
        AdRecommendationRequest request = AdRecommendationRequest.builder()
                .userId("") // 빈 userId로 유효성 검사 테스트
                .sessionId("session456")
                .build();

        mockMvc.perform(post("/recommendations/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRecommendedAdsForUser() throws Exception {
        mockMvc.perform(get("/recommendations/ads/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void testGetRecommendedAdsForUserWithDifferentDevice() throws Exception {
        mockMvc.perform(get("/recommendations/ads/user456")
                        .param("deviceType", "DESKTOP")
                        .param("placement", "SIDEBAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user456"))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void testGetRecommendedAdsForUserWithLocation() throws Exception {
        mockMvc.perform(get("/recommendations/ads/user789")
                        .param("location", "Busan")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user789"))
                .andExpect(jsonPath("$.recommendations").isArray());
    }

    @Test
    void testGetRecommendedAdsForNonExistentUser() throws Exception {
        mockMvc.perform(get("/recommendations/ads/nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("nonexistent"))
                .andExpect(jsonPath("$.recommendations").isArray());
    }
} 