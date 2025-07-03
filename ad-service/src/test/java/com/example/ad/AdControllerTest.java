package com.example.ad;

import com.example.ad.application.dto.AdvertisementRequest;
import com.example.ad.application.dto.AdvertisementResponse;
import com.example.ad.application.service.AdvertisementService;
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
public class AdControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/ads/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ad Service is healthy"));
    }

    @Test
    void testCreateAdvertisement() throws Exception {
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("Test Advertisement")
                .description("Test Description")
                .campaignId(1L)
                .imageUrl("https://example.com/test.jpg")
                .videoUrl("https://example.com/test.mp4")
                .landingPageUrl("https://example.com/test")
                .adType("BANNER")
                .status("DRAFT")
                .bidAmount(15.5)
                .dailyBudget(200.0)
                .build();

        mockMvc.perform(post("/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Advertisement"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    void testCreateAdvertisementValidation() throws Exception {
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("")
                .description("Test Description")
                .campaignId(1L)
                .adType("BANNER")
                .status("DRAFT")
                .build();

        mockMvc.perform(post("/ads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAdvertisementById() throws Exception {
        // First create an advertisement
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("Get By ID Advertisement")
                .description("Test Description")
                .campaignId(1L)
                .imageUrl("https://example.com/test.jpg")
                .videoUrl("https://example.com/test.mp4")
                .landingPageUrl("https://example.com/test")
                .adType("BANNER")
                .status("DRAFT")
                .bidAmount(15.5)
                .dailyBudget(200.0)
                .build();

        AdvertisementResponse createdAd = advertisementService.createAd(request);

        mockMvc.perform(get("/ads/{id}", createdAd.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdAd.getId()))
                .andExpect(jsonPath("$.title").value("Get By ID Advertisement"));
    }

    @Test
    void testGetAdvertisementsByCampaign() throws Exception {
        // First create an advertisement
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("Campaign Advertisement")
                .description("Test Description")
                .campaignId(1L)
                .imageUrl("https://example.com/test.jpg")
                .videoUrl("https://example.com/test.mp4")
                .landingPageUrl("https://example.com/test")
                .adType("BANNER")
                .status("DRAFT")
                .bidAmount(15.5)
                .dailyBudget(200.0)
                .build();

        advertisementService.createAd(request);

        mockMvc.perform(get("/ads/campaign/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateAdvertisementStatus() throws Exception {
        // First create an advertisement
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("Update Status Advertisement")
                .description("Test Description")
                .campaignId(1L)
                .imageUrl("https://example.com/test.jpg")
                .videoUrl("https://example.com/test.mp4")
                .landingPageUrl("https://example.com/test")
                .adType("BANNER")
                .status("DRAFT")
                .bidAmount(15.5)
                .dailyBudget(200.0)
                .build();

        AdvertisementResponse createdAd = advertisementService.createAd(request);

        mockMvc.perform(put("/ads/{id}/status", createdAd.getId())
                        .param("status", "PAUSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }

    @Test
    void testUpdateAdvertisementMetrics() throws Exception {
        // First create an advertisement
        AdvertisementRequest request = AdvertisementRequest.builder()
                .title("Update Metrics Advertisement")
                .description("Test Description")
                .campaignId(1L)
                .imageUrl("https://example.com/test.jpg")
                .videoUrl("https://example.com/test.mp4")
                .landingPageUrl("https://example.com/test")
                .adType("BANNER")
                .status("DRAFT")
                .bidAmount(15.5)
                .dailyBudget(200.0)
                .build();

        AdvertisementResponse createdAd = advertisementService.createAd(request);

        mockMvc.perform(put("/ads/{id}/metrics", createdAd.getId())
                        .param("impressions", "1000")
                        .param("clicks", "50")
                        .param("ctr", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.impressions").value(1000))
                .andExpect(jsonPath("$.clicks").value(50))
                .andExpect(jsonPath("$.ctr").value(5.0));
    }

    @Test
    void testGetNonExistentAdvertisement() throws Exception {
        mockMvc.perform(get("/ads/999"))
                .andExpect(status().isNotFound());
    }
} 