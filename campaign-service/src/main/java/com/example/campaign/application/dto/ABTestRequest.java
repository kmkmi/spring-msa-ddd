package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTestRequest {
    @NotBlank(message = "테스트 이름은 필수입니다")
    private String testName;
    
    @NotNull(message = "테스트 타입은 필수입니다")
    private String testType; // CREATIVE, TARGETING, BIDDING
    
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer trafficSplit; // 50 means 50-50 split
    private List<Map<String, Object>> variants;
    private String primaryMetric; // CTR, CVR, CPC
    private String secondaryMetric;
    private Integer confidenceLevel; // 95, 99
} 