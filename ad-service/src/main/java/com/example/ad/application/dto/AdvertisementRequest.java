package com.example.ad.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementRequest {
    @NotNull(message = "Campaign ID is required")
    private Long campaignId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String landingPageUrl;
    private String adType;
    private String status;
    private Double bidAmount;
    private Double dailyBudget;
    private Double totalSpent;
    private Long impressions;
    private Long clicks;
    private Double ctr;
    private String startDate;
    private String endDate;
} 