package com.example.campaign.common.util;

import com.example.campaign.common.exception.CampaignValidationException;
import com.example.campaign.domain.Campaign;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CampaignValidator {
    
    /**
     * 캠페인 생성 시 기본 검증
     */
    public static void validateCampaignCreation(Campaign campaign) {
        if (campaign.getName() == null || campaign.getName().trim().isEmpty()) {
            throw new CampaignValidationException("Campaign name is required");
        }
        
        if (campaign.getPublisherId() == null) {
            throw new CampaignValidationException("Publisher ID is required");
        }
        
        if (campaign.getStartDate() == null || campaign.getEndDate() == null) {
            throw new CampaignValidationException("Start date and end date are required");
        }
        
        if (campaign.getStartDate().isAfter(campaign.getEndDate())) {
            throw new CampaignValidationException("Start date cannot be after end date");
        }
        
        if (campaign.getStartDate().isBefore(LocalDateTime.now())) {
            throw new CampaignValidationException("Start date cannot be in the past");
        }
        
        validateBudget(campaign.getBudgetAmount(), campaign.getDailyBudget());
    }
    
    /**
     * 예산 검증
     */
    public static void validateBudget(BigDecimal budgetAmount, BigDecimal dailyBudget) {
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampaignValidationException("Budget amount must be greater than zero");
        }
        
        if (dailyBudget == null || dailyBudget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampaignValidationException("Daily budget must be greater than zero");
        }
        
        if (dailyBudget.compareTo(budgetAmount) > 0) {
            throw new CampaignValidationException("Daily budget cannot exceed total budget");
        }
    }
    
    /**
     * 상태 변경 검증
     */
    public static void validateStatusTransition(Campaign.CampaignStatus oldStatus, Campaign.CampaignStatus newStatus) {
        if (oldStatus == Campaign.CampaignStatus.COMPLETED && newStatus != Campaign.CampaignStatus.COMPLETED) {
            throw new CampaignValidationException("Cannot change status of completed campaign");
        }
        
        if (oldStatus == Campaign.CampaignStatus.CANCELLED && newStatus != Campaign.CampaignStatus.CANCELLED) {
            throw new CampaignValidationException("Cannot change status of cancelled campaign");
        }
    }
    
    /**
     * 캠페인 활성 상태 확인
     */
    public static boolean isCampaignActive(Campaign campaign) {
        LocalDateTime now = LocalDateTime.now();
        return campaign.getStatus() == Campaign.CampaignStatus.ACTIVE &&
               campaign.getStartDate().isBefore(now) &&
               campaign.getEndDate().isAfter(now);
    }
} 