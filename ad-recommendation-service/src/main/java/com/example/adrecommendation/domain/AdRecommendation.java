package com.example.adrecommendation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdRecommendation {
    private String requestId;
    private String userId;
    private String sessionId;
    private String deviceType;
    private String location;
    private String placement;
    private List<RecommendedAd> recommendedAds;
    private LocalDateTime timestamp;
    private long responseTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedAd {
        private Long advertisementId;
        private Double score;
        private String reason;
        private String creativeType;
        private String placement;
    }
} 