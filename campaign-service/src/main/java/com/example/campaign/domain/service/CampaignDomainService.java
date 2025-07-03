package com.example.campaign.domain.service;

import com.example.campaign.domain.Campaign;
import com.example.campaign.domain.event.CampaignCreatedEvent;
import com.example.campaign.domain.event.CampaignStatusChangedEvent;
import com.example.campaign.infrastructure.repository.CampaignRepository;
import com.example.campaign.common.exception.CampaignNotFoundException;
import com.example.campaign.common.exception.CampaignValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampaignDomainService {
    
    private final CampaignRepository campaignRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 캠페인 생성 시 도메인 로직 처리
     */
    public Campaign createCampaign(Campaign campaign) {
        // 비즈니스 규칙 검증
        validateCampaignCreation(campaign);
        
        // 캠페인 저장
        Campaign savedCampaign = campaignRepository.save(campaign);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(new CampaignCreatedEvent(savedCampaign));
        
        log.info("Campaign created with ID: {}", savedCampaign.getId());
        return savedCampaign;
    }
    
    /**
     * 캠페인 상태 변경 시 도메인 로직 처리
     */
    public Campaign updateCampaignStatus(Long campaignId, Campaign.CampaignStatus newStatus) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));
        
        Campaign.CampaignStatus oldStatus = campaign.getStatus();
        
        // 상태 변경 검증
        validateStatusTransition(oldStatus, newStatus);
        
        // 상태 변경
        campaign.updateStatus(newStatus);
        Campaign updatedCampaign = campaignRepository.save(campaign);
        
        // 도메인 이벤트 발행
        eventPublisher.publishEvent(new CampaignStatusChangedEvent(updatedCampaign, oldStatus, newStatus));
        
        log.info("Campaign status changed from {} to {} for campaign ID: {}", 
                oldStatus, newStatus, campaignId);
        return updatedCampaign;
    }
    
    /**
     * 캠페인 예산 변경 시 도메인 로직 처리
     */
    public Campaign updateCampaignBudget(Long campaignId, BigDecimal budgetAmount, BigDecimal dailyBudget) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(campaignId));
        
        // 예산 검증
        validateBudgetUpdate(campaign, budgetAmount, dailyBudget);
        
        // 예산 변경
        campaign.updateBudget(budgetAmount, dailyBudget);
        Campaign updatedCampaign = campaignRepository.save(campaign);
        
        log.info("Campaign budget updated for campaign ID: {} - Total: {}, Daily: {}", 
                campaignId, budgetAmount, dailyBudget);
        return updatedCampaign;
    }
    
    /**
     * 활성 캠페인 조회 (도메인 로직)
     */
    @Transactional(readOnly = true)
    public List<Campaign> getActiveCampaigns() {
        return campaignRepository.findActiveCampaigns(LocalDateTime.now());
    }
    
    /**
     * 캠페인 생성 검증
     */
    private void validateCampaignCreation(Campaign campaign) {
        if (campaign.getStartDate().isAfter(campaign.getEndDate())) {
            throw new CampaignValidationException("Start date cannot be after end date");
        }
        
        if (campaign.getBudgetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampaignValidationException("Budget amount must be greater than zero");
        }
        
        if (campaign.getDailyBudget().compareTo(campaign.getBudgetAmount()) > 0) {
            throw new CampaignValidationException("Daily budget cannot exceed total budget");
        }
    }
    
    /**
     * 상태 변경 검증
     */
    private void validateStatusTransition(Campaign.CampaignStatus oldStatus, Campaign.CampaignStatus newStatus) {
        if (oldStatus == Campaign.CampaignStatus.COMPLETED && newStatus != Campaign.CampaignStatus.COMPLETED) {
            throw new CampaignValidationException("Cannot change status of completed campaign");
        }
        
        if (oldStatus == Campaign.CampaignStatus.CANCELLED && newStatus != Campaign.CampaignStatus.CANCELLED) {
            throw new CampaignValidationException("Cannot change status of cancelled campaign");
        }
    }
    
    /**
     * 예산 변경 검증
     */
    private void validateBudgetUpdate(Campaign campaign, BigDecimal budgetAmount, BigDecimal dailyBudget) {
        if (budgetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampaignValidationException("Budget amount must be greater than zero");
        }
        
        if (dailyBudget.compareTo(budgetAmount) > 0) {
            throw new CampaignValidationException("Daily budget cannot exceed total budget");
        }
        
        // 이미 지출된 금액보다 예산을 줄일 수 없음
        if (campaign.getSpentAmount() != null && budgetAmount.compareTo(campaign.getSpentAmount()) < 0) {
            throw new CampaignValidationException("Budget cannot be less than already spent amount");
        }
    }
} 