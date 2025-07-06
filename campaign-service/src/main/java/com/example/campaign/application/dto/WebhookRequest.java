package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequest {
    @NotBlank(message = "웹훅 URL은 필수입니다")
    private String url;
    
    @NotNull(message = "이벤트 타입은 필수입니다")
    private List<String> events; // CAMPAIGN_CREATED, CAMPAIGN_UPDATED, CAMPAIGN_PAUSED, etc.
    
    private String secret;
    private Boolean isActive;
    private String description;
} 