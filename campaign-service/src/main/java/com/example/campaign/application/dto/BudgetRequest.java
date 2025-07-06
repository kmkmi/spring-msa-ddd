package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    @NotNull(message = "일일 예산은 필수입니다")
    @Positive(message = "일일 예산은 양수여야 합니다")
    private BigDecimal dailyBudget;
    
    @NotNull(message = "총 예산은 필수입니다")
    @Positive(message = "총 예산은 양수여야 합니다")
    private BigDecimal totalBudget;
    
    private String budgetType; // DAILY, LIFETIME
    private BigDecimal bidAmount;
    private String bidStrategy; // MANUAL, AUTO
    private Boolean enableBudgetOptimization;
} 