package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertResponse {
    private String alertId;
    private String name;
    private String alertType;
    private String condition;
    private BigDecimal threshold;
    private String metric;
    private List<String> notificationChannels;
    private String message;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastTriggered;
    private Integer triggerCount;
} 