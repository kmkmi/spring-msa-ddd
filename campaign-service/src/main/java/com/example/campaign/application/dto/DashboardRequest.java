package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequest {
    private String period; // today, week, month, quarter, year
    private String userId;
    private List<String> metrics;
    private Boolean includeCharts;
} 