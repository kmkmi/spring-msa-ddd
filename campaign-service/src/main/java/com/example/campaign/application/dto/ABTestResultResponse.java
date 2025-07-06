package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTestResultResponse {
    private String testId;
    private String testName;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<VariantResult> variantResults;
    private String winner;
    private BigDecimal confidenceLevel;
    private Boolean isStatisticallySignificant;
    private Map<String, Object> insights;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResult {
        private String variantName;
        private Long impressions;
        private Long clicks;
        private Long conversions;
        private BigDecimal ctr;
        private BigDecimal cvr;
        private BigDecimal cpc;
        private BigDecimal spend;
        private BigDecimal improvement;
        private String status; // WINNER, LOSER, NO_DIFFERENCE
    }
} 