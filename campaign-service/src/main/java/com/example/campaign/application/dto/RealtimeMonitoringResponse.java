package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeMonitoringResponse {
    private Long campaignId;
    private String campaignName;
    private String status;
    private LocalDateTime lastUpdated;
    private Map<String, Object> currentMetrics;
    private Map<String, Object> hourlyTrends;
    private Map<String, Object> alerts;
    private BigDecimal spendToday;
    private BigDecimal spendThisHour;
    private Long impressionsToday;
    private Long clicksToday;
    private BigDecimal ctrToday;
} 