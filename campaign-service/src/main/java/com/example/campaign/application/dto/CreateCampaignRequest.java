package com.example.campaign.application.dto;

import com.example.campaign.domain.Campaign;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {
    
    @NotBlank(message = "Campaign name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Publisher ID is required")
    @Positive(message = "Publisher ID must be positive")
    private Long publisherId;
    
    @NotNull(message = "Campaign type is required")
    private Campaign.CampaignType campaignType;
    
    @Positive(message = "Budget amount must be positive")
    private BigDecimal budgetAmount;
    
    @Positive(message = "Daily budget must be positive")
    private BigDecimal dailyBudget;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String targetAudience;
    
    private String targetLocations;
} 