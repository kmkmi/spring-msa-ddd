package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ABTestResponse {
    private String testId;
    private String testName;
    private String testType;
    private String status; // DRAFT, RUNNING, COMPLETED, PAUSED
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer trafficSplit;
    private List<Map<String, Object>> variants;
    private String primaryMetric;
    private String secondaryMetric;
    private Integer confidenceLevel;
    private LocalDateTime createdAt;
    private String message;
} 