package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAnalyticsResponse {
    private Long campaignId;
    private String campaignName;
    private Map<String, Object> demographics;
    private Map<String, Object> deviceBreakdown;
    private Map<String, Object> geographicData;
    private List<AudienceSegment> audienceSegments;
    private Map<String, Object> performanceTrends;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AudienceSegment {
        private String segmentName;
        private Long impressions;
        private Long clicks;
        private BigDecimal ctr;
        private BigDecimal conversionRate;
        private BigDecimal costPerClick;
    }
} 