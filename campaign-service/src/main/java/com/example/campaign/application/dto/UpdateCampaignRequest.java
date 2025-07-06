package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignRequest {
    @NotBlank(message = "캠페인 이름은 필수입니다")
    private String name;
    
    private String description;
    
    @NotNull(message = "예산은 필수입니다")
    private BigDecimal budget;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String targetingType;
} 