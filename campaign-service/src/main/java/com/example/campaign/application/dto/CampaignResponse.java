package com.example.campaign.application.dto;

import com.example.campaign.domain.Campaign;
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
public class CampaignResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long publisherId;
    private Campaign.CampaignStatus status;
    private Campaign.CampaignType campaignType;
    private BigDecimal budgetAmount;
    private BigDecimal dailyBudget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String targetAudience;
    private String targetLocations;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static CampaignResponse from(Campaign campaign) {
        return CampaignResponse.builder()
                .id(campaign.getId())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .publisherId(campaign.getPublisherId())
                .status(campaign.getStatus())
                .campaignType(campaign.getCampaignType())
                .budgetAmount(campaign.getBudgetAmount())
                .dailyBudget(campaign.getDailyBudget())
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .targetAudience(campaign.getTargetAudience())
                .targetLocations(campaign.getTargetLocations())
                .createdAt(campaign.getCreatedAt())
                .updatedAt(campaign.getUpdatedAt())
                .build();
    }
} 