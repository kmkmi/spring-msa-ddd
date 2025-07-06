package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {
    private String webhookId;
    private String url;
    private String status; // ACTIVE, INACTIVE, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime lastTriggered;
    private Integer successCount;
    private Integer failureCount;
    private String message;
} 