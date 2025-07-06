package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignPerformanceRequest {
    private Long campaignId;
    private String startDate;
    private String endDate;
    private String groupBy; // hourly, daily, weekly, monthly
} 