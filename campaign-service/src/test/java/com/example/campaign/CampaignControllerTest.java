package com.example.campaign;

import com.example.campaign.application.dto.CreateCampaignRequest;
import com.example.campaign.application.dto.CampaignResponse;
import com.example.campaign.application.service.CampaignService;
import com.example.campaign.domain.Campaign;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class CampaignControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/campaigns/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Campaign Service is healthy"));
    }

    @Test
    @WithMockUser(roles = "PUBLISHER")
    void testCreateCampaign() throws Exception {
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Test Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        mockMvc.perform(post("/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Campaign"))
                .andExpect(jsonPath("$.description").value("Test Description"));
    }

    @Test
    @WithMockUser(roles = "PUBLISHER")
    void testCreateCampaignValidation() throws Exception {
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("")
                .description("Test Description")
                .publisherId(1L)
                .build();

        mockMvc.perform(post("/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    void testGetCampaignById() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Get By ID Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        CampaignResponse createdCampaign = campaignService.createCampaign(request);

        mockMvc.perform(get("/campaigns/{id}", createdCampaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCampaign.getId()))
                .andExpect(jsonPath("$.name").value("Get By ID Campaign"));
    }

    @Test
    @WithMockUser(roles = "PUBLISHER")
    void testGetCampaignsByPublisherId() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Publisher Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        campaignService.createCampaign(request);

        mockMvc.perform(get("/campaigns/publisher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetCampaignsByStatus() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Status Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        campaignService.createCampaign(request);

        mockMvc.perform(get("/campaigns/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetActiveCampaigns() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Active Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        campaignService.createCampaign(request);

        mockMvc.perform(get("/campaigns/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "PUBLISHER")
    void testUpdateCampaignStatus() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Update Status Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        CampaignResponse createdCampaign = campaignService.createCampaign(request);

        mockMvc.perform(put("/campaigns/{id}/status", createdCampaign.getId())
                        .param("status", "PAUSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }

    @Test
    void testUpdateCampaignBudget() throws Exception {
        // First create a campaign
        CreateCampaignRequest request = CreateCampaignRequest.builder()
                .name("Update Budget Campaign")
                .description("Test Description")
                .publisherId(1L)
                .campaignType(Campaign.CampaignType.DISPLAY)
                .budgetAmount(new BigDecimal("1000.00"))
                .dailyBudget(new BigDecimal("100.00"))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .targetAudience("{\"location\": \"Korea\", \"age_range\": \"20-40\"}")
                .build();

        CampaignResponse createdCampaign = campaignService.createCampaign(request);

        mockMvc.perform(put("/campaigns/{id}/budget", createdCampaign.getId())
                        .param("budgetAmount", "2000.00")
                        .param("dailyBudget", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.budgetAmount").value(2000.00))
                .andExpect(jsonPath("$.dailyBudget").value(200.00));
    }

    @Test
    @WithMockUser(roles = "ADVERTISER")
    void testGetNonExistentCampaign() throws Exception {
        mockMvc.perform(get("/campaigns/999"))
                .andExpect(status().isNotFound());
    }
} 