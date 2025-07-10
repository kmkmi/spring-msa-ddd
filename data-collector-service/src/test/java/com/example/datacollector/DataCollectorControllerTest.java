package com.example.datacollector;

import com.example.datacollector.application.dto.AdEventRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=localhost:9092",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
})
public class DataCollectorControllerTest {

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
        mockMvc.perform(get("/events/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data Collector Service is healthy"));
    }

    @Test
    void testCollectAdEvent() throws Exception {
        AdEventRequest request = AdEventRequest.builder()
                .eventType("IMPRESSION")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .sessionId("session456")
                .deviceType("MOBILE")
                .location("Seoul")
                .userAgent("Mozilla/5.0")
                .ipAddress("192.168.1.1")
                .build();

        mockMvc.perform(post("/events/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCollectAdEventValidation() throws Exception {
        AdEventRequest request = AdEventRequest.builder()
                .eventType("") // 빈 eventType으로 유효성 검사 테스트
                .advertisementId("1")
                .build();

        mockMvc.perform(post("/events/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCollectClickEvent() throws Exception {
        AdEventRequest request = AdEventRequest.builder()
                .eventType("CLICK")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .sessionId("session456")
                .deviceType("DESKTOP")
                .location("Busan")
                .userAgent("Mozilla/5.0")
                .ipAddress("192.168.1.2")
                .build();

        mockMvc.perform(post("/events/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCollectConversionEvent() throws Exception {
        AdEventRequest request = AdEventRequest.builder()
                .eventType("CONVERSION")
                .advertisementId("1")
                .campaignId("1")
                .userId("user123")
                .sessionId("session456")
                .deviceType("MOBILE")
                .location("Incheon")
                .userAgent("Mozilla/5.0")
                .ipAddress("192.168.1.3")
                .conversionValue("100.0")
                .build();

        mockMvc.perform(post("/events/collect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetEventStats() throws Exception {
        mockMvc.perform(get("/events/stats")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .param("eventType", "IMPRESSION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetEventStatsByCampaign() throws Exception {
        mockMvc.perform(get("/events/stats/campaign/1")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 