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
public class CampaignReportResponse {
    private String reportId;
    private String reportType;
    private LocalDateTime generatedAt;
    private String downloadUrl;
    private Map<String, Object> summary;
    private List<Map<String, Object>> detailedData;
    private String status; // GENERATING, COMPLETED, FAILED
    private String message;
} 