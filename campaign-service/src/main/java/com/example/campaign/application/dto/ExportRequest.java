package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequest {
    @NotEmpty(message = "캠페인 ID 목록은 필수입니다")
    private List<Long> campaignIds;
    
    @NotNull(message = "내보내기 형식은 필수입니다")
    private String format; // CSV, EXCEL, JSON, PDF
    
    private String startDate;
    private String endDate;
    private List<String> fields;
    private Boolean includeMetrics;
    private String fileName;
} 