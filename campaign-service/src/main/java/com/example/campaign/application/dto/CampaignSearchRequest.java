package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSearchRequest {
    private String keyword;
    private String status;
    private String startDate;
    private String endDate;
    private Double minBudget;
    private Double maxBudget;
} 