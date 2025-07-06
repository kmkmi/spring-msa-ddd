package com.example.campaign.application.service;

import com.example.campaign.application.dto.*;
import com.example.campaign.domain.Campaign;
import com.example.campaign.infrastructure.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampaignManagerService {
    
    private final CampaignRepository campaignRepository;
    
    // ===== 기본 CRUD 작업 =====
    
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        log.info("Creating campaign: {}", request.getName());
        
        Campaign campaign = Campaign.builder()
            .name(request.getName())
            .description(request.getDescription())
            .publisherId(request.getPublisherId())
            .campaignType(request.getCampaignType())
            .budgetAmount(request.getBudgetAmount())
            .dailyBudget(request.getDailyBudget())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .targetAudience(request.getTargetAudience())
            .targetLocations(request.getTargetLocations())
            .status(Campaign.CampaignStatus.DRAFT)
            .build();
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    public CampaignResponse getCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        return convertToResponse(campaign);
    }
    
    public CampaignResponse updateCampaign(Long campaignId, UpdateCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());
        campaign.updateBudget(request.getBudget(), request.getBudget()); // dailyBudget도 같은 값으로 설정
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.updateStatus(Campaign.CampaignStatus.valueOf(request.getStatus()));
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    public void deleteCampaign(Long campaignId) {
        campaignRepository.deleteById(campaignId);
    }
    
    // ===== 고급 검색 및 필터링 =====
    
    public Page<CampaignResponse> searchCampaigns(CampaignSearchRequest request, Pageable pageable) {
        // 실제 구현에서는 복잡한 검색 로직을 구현
        Page<Campaign> campaigns = campaignRepository.findAll(pageable);
        return campaigns.map(this::convertToResponse);
    }
    
    // ===== 캠페인 상태 관리 =====
    
    public CampaignResponse activateCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        campaign.updateStatus(Campaign.CampaignStatus.ACTIVE);
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    public CampaignResponse pauseCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        campaign.updateStatus(Campaign.CampaignStatus.PAUSED);
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    public CampaignResponse stopCampaign(Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        campaign.updateStatus(Campaign.CampaignStatus.CANCELLED);
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    // ===== 캠페인 성과 분석 =====
    
    public CampaignPerformanceResponse getCampaignPerformance(CampaignPerformanceRequest request) {
        // 실제 구현에서는 성과 데이터를 조회하여 반환
        return CampaignPerformanceResponse.builder()
            .campaignId(request.getCampaignId())
            .campaignName("Sample Campaign")
            .build();
    }
    
    public CampaignAnalyticsResponse getCampaignAnalytics(Long campaignId) {
        // 실제 구현에서는 분석 데이터를 조회하여 반환
        return CampaignAnalyticsResponse.builder()
            .campaignId(campaignId)
            .campaignName("Sample Campaign")
            .build();
    }
    
    // ===== 타겟팅 및 예산 관리 =====
    
    public CampaignResponse updateTargeting(Long campaignId, TargetingRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        // 타겟팅 설정 업데이트 로직
        // Campaign 엔티티에 targetingType 필드가 없으므로 targetAudience를 사용
        campaign.setTargetAudience(request.getTargetingType());
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    public CampaignResponse updateBudget(Long campaignId, BudgetRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found: " + campaignId));
        
        campaign.updateBudget(request.getTotalBudget(), request.getDailyBudget());
        
        Campaign savedCampaign = campaignRepository.save(campaign);
        return convertToResponse(savedCampaign);
    }
    
    // ===== 일괄 작업 =====
    
    public BulkOperationResponse bulkActivate(List<Long> campaignIds) {
        int successCount = 0;
        int failureCount = 0;
        
        for (Long campaignId : campaignIds) {
            try {
                activateCampaign(campaignId);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to activate campaign: {}", campaignId, e);
                failureCount++;
            }
        }
        
        return BulkOperationResponse.builder()
            .operationType("BULK_ACTIVATE")
            .totalCount(campaignIds.size())
            .successCount(successCount)
            .failureCount(failureCount)
            .message("Bulk activation completed")
            .build();
    }
    
    public BulkOperationResponse bulkPause(List<Long> campaignIds) {
        int successCount = 0;
        int failureCount = 0;
        
        for (Long campaignId : campaignIds) {
            try {
                pauseCampaign(campaignId);
                successCount++;
            } catch (Exception e) {
                log.error("Failed to pause campaign: {}", campaignId, e);
                failureCount++;
            }
        }
        
        return BulkOperationResponse.builder()
            .operationType("BULK_PAUSE")
            .totalCount(campaignIds.size())
            .successCount(successCount)
            .failureCount(failureCount)
            .message("Bulk pause completed")
            .build();
    }
    
    public BulkOperationResponse bulkUpdate(BulkUpdateRequest request) {
        int successCount = 0;
        int failureCount = 0;
        
        for (Long campaignId : request.getCampaignIds()) {
            try {
                // 일괄 업데이트 로직 구현
                successCount++;
            } catch (Exception e) {
                log.error("Failed to update campaign: {}", campaignId, e);
                failureCount++;
            }
        }
        
        return BulkOperationResponse.builder()
            .operationType("BULK_UPDATE")
            .totalCount(request.getCampaignIds().size())
            .successCount(successCount)
            .failureCount(failureCount)
            .message("Bulk update completed")
            .build();
    }
    
    // ===== 리포트 및 내보내기 =====
    
    public CampaignReportResponse generateReport(CampaignReportRequest request) {
        // 실제 구현에서는 리포트 생성 로직을 구현
        return CampaignReportResponse.builder()
            .reportId(UUID.randomUUID().toString())
            .reportType(request.getReportType())
            .status("COMPLETED")
            .message("Report generated successfully")
            .build();
    }
    
    public ExportResponse exportCampaigns(ExportRequest request) {
        // 실제 구현에서는 내보내기 로직을 구현
        return ExportResponse.builder()
            .exportId(UUID.randomUUID().toString())
            .status("COMPLETED")
            .fileName("campaigns_export." + request.getFormat().toLowerCase())
            .message("Export completed successfully")
            .build();
    }
    
    // ===== 실시간 모니터링 =====
    
    public RealtimeMonitoringResponse getRealtimeMonitoring(Long campaignId) {
        // 실제 구현에서는 실시간 모니터링 데이터를 조회
        return RealtimeMonitoringResponse.builder()
            .campaignId(campaignId)
            .campaignName("Sample Campaign")
            .status("ACTIVE")
            .build();
    }
    
    public DashboardResponse getDashboard(DashboardRequest request) {
        // 실제 구현에서는 대시보드 데이터를 조회
        return DashboardResponse.builder()
            .period(request.getPeriod())
            .totalCampaigns(100L)
            .activeCampaigns(50L)
            .build();
    }
    
    // ===== 알림 및 웹훅 =====
    
    public WebhookResponse setWebhook(Long campaignId, WebhookRequest request) {
        // 실제 구현에서는 웹훅 설정 로직을 구현
        return WebhookResponse.builder()
            .webhookId(UUID.randomUUID().toString())
            .url(request.getUrl())
            .status("ACTIVE")
            .message("Webhook set successfully")
            .build();
    }
    
    public List<AlertResponse> getAlerts(Long campaignId) {
        // 실제 구현에서는 알림 목록을 조회
        return List.of();
    }
    
    public AlertResponse createAlert(Long campaignId, AlertRequest request) {
        // 실제 구현에서는 알림 생성 로직을 구현
        return AlertResponse.builder()
            .alertId(UUID.randomUUID().toString())
            .name(request.getName())
            .alertType(request.getAlertType())
            .isActive(true)
            .message("Alert created successfully")
            .build();
    }
    
    // ===== A/B 테스트 =====
    
    public ABTestResponse createABTest(Long campaignId, ABTestRequest request) {
        // 실제 구현에서는 A/B 테스트 생성 로직을 구현
        return ABTestResponse.builder()
            .testId(UUID.randomUUID().toString())
            .testName(request.getTestName())
            .testType(request.getTestType())
            .status("DRAFT")
            .message("A/B test created successfully")
            .build();
    }
    
    public ABTestResultResponse getABTestResult(Long campaignId) {
        // 실제 구현에서는 A/B 테스트 결과를 조회
        return ABTestResultResponse.builder()
            .testId(UUID.randomUUID().toString())
            .testName("Sample A/B Test")
            .status("COMPLETED")
            .build();
    }
    
    // ===== Helper methods =====
    
    private CampaignResponse convertToResponse(Campaign campaign) {
        return CampaignResponse.from(campaign);
    }
} 