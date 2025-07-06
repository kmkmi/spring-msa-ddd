package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignReportRequest {
    @NotNull(message = "리포트 타입은 필수입니다")
    private String reportType; // PERFORMANCE, ANALYTICS, AUDIENCE
    
    private List<Long> campaignIds;
    private String startDate;
    private String endDate;
    private String groupBy; // daily, weekly, monthly
    private List<String> metrics;
    private String format; // PDF, EXCEL, CSV
    private Boolean includeCharts;
} 