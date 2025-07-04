package com.example.campaign.application.service;

import com.example.campaign.application.dto.CreateCampaignRequest;
import com.example.campaign.application.dto.CampaignResponse;
import com.example.campaign.domain.Campaign;
import com.example.campaign.domain.service.CampaignDomainService;
import com.example.campaign.infrastructure.repository.CampaignRepository;
import com.example.campaign.common.exception.CampaignNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CampaignService {
    
    private final CampaignRepository campaignRepository;
    private final CampaignDomainService campaignDomainService;
    
    @Transactional
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        log.info("Creating campaign: {}", request.getName());
        
        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .publisherId(request.getPublisherId())
                .status(Campaign.CampaignStatus.DRAFT)
                .campaignType(request.getCampaignType())
                .budgetAmount(request.getBudgetAmount())
                .dailyBudget(request.getDailyBudget())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .targetAudience(request.getTargetAudience())
                .targetLocations(request.getTargetLocations())
                .build();
        
        Campaign savedCampaign = campaignDomainService.createCampaign(campaign);
        log.info("Campaign created with id: {}", savedCampaign.getId());
        
        return CampaignResponse.from(savedCampaign);
    }
    
    public CampaignResponse getCampaignById(Long id) {
        log.info("Getting campaign by id: {}", id);
        
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new CampaignNotFoundException(id));
        
        return CampaignResponse.from(campaign);
    }
    
    public List<CampaignResponse> getAllCampaigns() {
        log.info("Getting all campaigns");
        
        return campaignRepository.findAll().stream()
                .map(CampaignResponse::from)
                .collect(Collectors.toList());
    }
    
    public List<CampaignResponse> getCampaignsByPublisher(Long publisherId) {
        log.info("Getting campaigns for publisher: {}", publisherId);
        
        return campaignRepository.findByPublisherId(publisherId).stream()
                .map(CampaignResponse::from)
                .collect(Collectors.toList());
    }
    
    public List<CampaignResponse> getCampaignsByStatus(Campaign.CampaignStatus status) {
        log.info("Getting campaigns by status: {}", status);
        
        return campaignRepository.findByStatus(status).stream()
                .map(CampaignResponse::from)
                .collect(Collectors.toList());
    }
    
    public List<CampaignResponse> getActiveCampaigns() {
        log.info("Getting active campaigns");
        
        return campaignRepository.findActiveCampaigns(LocalDateTime.now()).stream()
                .map(CampaignResponse::from)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CampaignResponse updateCampaignStatus(Long id, Campaign.CampaignStatus status) {
        log.info("Updating campaign status for id: {} to: {}", id, status);
        
        Campaign updatedCampaign = campaignDomainService.updateCampaignStatus(id, status);
        
        return CampaignResponse.from(updatedCampaign);
    }
    
    @Transactional
    public CampaignResponse updateCampaignBudget(Long id, BigDecimal budgetAmount, BigDecimal dailyBudget) {
        log.info("Updating campaign budget for id: {} to budget: {}, daily: {}", id, budgetAmount, dailyBudget);
        
        Campaign updatedCampaign = campaignDomainService.updateCampaignBudget(id, budgetAmount, dailyBudget);
        
        return CampaignResponse.from(updatedCampaign);
    }
} 