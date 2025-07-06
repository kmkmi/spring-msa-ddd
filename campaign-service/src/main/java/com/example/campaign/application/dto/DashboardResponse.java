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
public class DashboardResponse {
    private String period;
    private Map<String, Object> summary;
    private List<Map<String, Object>> topCampaigns;
    private List<Map<String, Object>> performanceTrends;
    private Map<String, Object> spendBreakdown;
    private List<Map<String, Object>> recentActivity;
    private Map<String, Object> alerts;
    
    // Summary metrics
    private Long totalCampaigns;
    private Long activeCampaigns;
    private BigDecimal totalSpend;
    private Long totalImpressions;
    private Long totalClicks;
    private BigDecimal overallCTR;
} 