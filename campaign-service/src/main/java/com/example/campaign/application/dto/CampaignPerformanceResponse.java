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
public class CampaignPerformanceResponse {
    private Long campaignId;
    private String campaignName;
    private List<PerformanceMetric> metrics;
    private Map<String, Object> summary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetric {
        private String date;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal ctr;
        private BigDecimal cvr;
        private BigDecimal spend;
        private BigDecimal cpc;
        private BigDecimal cpm;
    }
} 