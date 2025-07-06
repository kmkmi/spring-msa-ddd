package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRequest {
    @NotBlank(message = "알림 이름은 필수입니다")
    private String name;
    
    @NotNull(message = "알림 타입은 필수입니다")
    private String alertType; // BUDGET_THRESHOLD, PERFORMANCE_DROP, CTR_ANOMALY
    
    private String condition; // GREATER_THAN, LESS_THAN, EQUALS
    private BigDecimal threshold;
    private String metric; // SPEND, CTR, IMPRESSIONS, CLICKS
    private List<String> notificationChannels; // EMAIL, SMS, WEBHOOK
    private String message;
    private Boolean isActive;
} 