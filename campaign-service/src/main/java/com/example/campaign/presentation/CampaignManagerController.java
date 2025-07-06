package com.example.campaign.presentation;

import com.example.campaign.application.dto.*;
import com.example.campaign.application.service.CampaignManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Campaign Manager", description = "캠페인 관리 API")
public class CampaignManagerController {
    
    private final CampaignManagerService campaignManagerService;
    
    // ===== 기본 CRUD 작업 =====
    
    @PostMapping
    @Operation(summary = "캠페인 생성", description = "새로운 광고 캠페인을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "캠페인 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "409", description = "캠페인 이름 중복")
    })
    public ResponseEntity<CampaignResponse> createCampaign(
            @Parameter(description = "캠페인 생성 요청", required = true)
            @Valid @RequestBody CreateCampaignRequest request) {
        
        log.info("Creating campaign: {}", request.getName());
        CampaignResponse response = campaignManagerService.createCampaign(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/{campaignId}")
    @Operation(summary = "캠페인 조회", description = "특정 캠페인의 상세 정보를 조회합니다.")
    public ResponseEntity<CampaignResponse> getCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        CampaignResponse response = campaignManagerService.getCampaign(campaignId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{campaignId}")
    @Operation(summary = "캠페인 수정", description = "기존 캠페인 정보를 수정합니다.")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "캠페인 수정 요청", required = true)
            @Valid @RequestBody UpdateCampaignRequest request) {
        
        log.info("Updating campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.updateCampaign(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{campaignId}")
    @Operation(summary = "캠페인 삭제", description = "캠페인을 삭제합니다.")
    public ResponseEntity<Void> deleteCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        log.info("Deleting campaign: {}", campaignId);
        campaignManagerService.deleteCampaign(campaignId);
        return ResponseEntity.noContent().build();
    }
    
    // ===== 고급 검색 및 필터링 =====
    
    @GetMapping
    @Operation(summary = "캠페인 목록 조회", description = "다양한 조건으로 캠페인 목록을 조회합니다.")
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(
            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "상태 필터")
            @RequestParam(required = false) String status,
            @Parameter(description = "시작 날짜")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "종료 날짜")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "예산 범위 (최소)")
            @RequestParam(required = false) Double minBudget,
            @Parameter(description = "예산 범위 (최대)")
            @RequestParam(required = false) Double maxBudget,
            @Parameter(description = "페이지 정보")
            Pageable pageable) {
        
        CampaignSearchRequest searchRequest = CampaignSearchRequest.builder()
            .keyword(keyword)
            .status(status)
            .startDate(startDate)
            .endDate(endDate)
            .minBudget(minBudget)
            .maxBudget(maxBudget)
            .build();
        
        Page<CampaignResponse> campaigns = campaignManagerService.searchCampaigns(searchRequest, pageable);
        return ResponseEntity.ok(campaigns);
    }
    
    // ===== 캠페인 상태 관리 =====
    
    @PostMapping("/{campaignId}/activate")
    @Operation(summary = "캠페인 활성화", description = "캠페인을 활성화합니다.")
    public ResponseEntity<CampaignResponse> activateCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        log.info("Activating campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.activateCampaign(campaignId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{campaignId}/pause")
    @Operation(summary = "캠페인 일시정지", description = "캠페인을 일시정지합니다.")
    public ResponseEntity<CampaignResponse> pauseCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        log.info("Pausing campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.pauseCampaign(campaignId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{campaignId}/stop")
    @Operation(summary = "캠페인 중지", description = "캠페인을 중지합니다.")
    public ResponseEntity<CampaignResponse> stopCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        log.info("Stopping campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.stopCampaign(campaignId);
        return ResponseEntity.ok(response);
    }
    
    // ===== 캠페인 성과 분석 =====
    
    @GetMapping("/{campaignId}/performance")
    @Operation(summary = "캠페인 성과 조회", description = "캠페인의 성과 메트릭을 조회합니다.")
    public ResponseEntity<CampaignPerformanceResponse> getCampaignPerformance(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "시작 날짜")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "종료 날짜")
            @RequestParam(required = false) String endDate,
            @Parameter(description = "그룹화 단위 (hourly, daily, weekly, monthly)")
            @RequestParam(defaultValue = "daily") String groupBy) {
        
        CampaignPerformanceRequest request = CampaignPerformanceRequest.builder()
            .campaignId(campaignId)
            .startDate(startDate)
            .endDate(endDate)
            .groupBy(groupBy)
            .build();
        
        CampaignPerformanceResponse response = campaignManagerService.getCampaignPerformance(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{campaignId}/analytics")
    @Operation(summary = "캠페인 분석", description = "캠페인의 상세 분석 데이터를 조회합니다.")
    public ResponseEntity<CampaignAnalyticsResponse> getCampaignAnalytics(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        CampaignAnalyticsResponse response = campaignManagerService.getCampaignAnalytics(campaignId);
        return ResponseEntity.ok(response);
    }
    
    // ===== 타겟팅 및 예산 관리 =====
    
    @PostMapping("/{campaignId}/targeting")
    @Operation(summary = "타겟팅 설정", description = "캠페인의 타겟팅 설정을 업데이트합니다.")
    public ResponseEntity<CampaignResponse> updateTargeting(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "타겟팅 설정", required = true)
            @Valid @RequestBody TargetingRequest request) {
        
        log.info("Updating targeting for campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.updateTargeting(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{campaignId}/budget")
    @Operation(summary = "예산 설정", description = "캠페인의 예산 설정을 업데이트합니다.")
    public ResponseEntity<CampaignResponse> updateBudget(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "예산 설정", required = true)
            @Valid @RequestBody BudgetRequest request) {
        
        log.info("Updating budget for campaign: {}", campaignId);
        CampaignResponse response = campaignManagerService.updateBudget(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    // ===== 일괄 작업 =====
    
    @PostMapping("/bulk/activate")
    @Operation(summary = "일괄 활성화", description = "여러 캠페인을 일괄 활성화합니다.")
    public ResponseEntity<BulkOperationResponse> bulkActivate(
            @Parameter(description = "캠페인 ID 목록", required = true)
            @RequestBody List<Long> campaignIds) {
        
        log.info("Bulk activating {} campaigns", campaignIds.size());
        BulkOperationResponse response = campaignManagerService.bulkActivate(campaignIds);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/bulk/pause")
    @Operation(summary = "일괄 일시정지", description = "여러 캠페인을 일괄 일시정지합니다.")
    public ResponseEntity<BulkOperationResponse> bulkPause(
            @Parameter(description = "캠페인 ID 목록", required = true)
            @RequestBody List<Long> campaignIds) {
        
        log.info("Bulk pausing {} campaigns", campaignIds.size());
        BulkOperationResponse response = campaignManagerService.bulkPause(campaignIds);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/bulk/update")
    @Operation(summary = "일괄 수정", description = "여러 캠페인의 공통 필드를 일괄 수정합니다.")
    public ResponseEntity<BulkOperationResponse> bulkUpdate(
            @Parameter(description = "일괄 수정 요청", required = true)
            @Valid @RequestBody BulkUpdateRequest request) {
        
        log.info("Bulk updating {} campaigns", request.getCampaignIds().size());
        BulkOperationResponse response = campaignManagerService.bulkUpdate(request);
        return ResponseEntity.ok(response);
    }
    
    // ===== 리포트 및 내보내기 =====
    
    @GetMapping("/report")
    @Operation(summary = "캠페인 리포트", description = "캠페인 리포트를 생성합니다.")
    public ResponseEntity<CampaignReportResponse> generateReport(
            @Parameter(description = "리포트 요청", required = true)
            @Valid @RequestBody CampaignReportRequest request) {
        
        log.info("Generating campaign report");
        CampaignReportResponse response = campaignManagerService.generateReport(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/export")
    @Operation(summary = "캠페인 내보내기", description = "캠페인 데이터를 다양한 형식으로 내보냅니다.")
    public ResponseEntity<ExportResponse> exportCampaigns(
            @Parameter(description = "내보내기 요청", required = true)
            @Valid @RequestBody ExportRequest request) {
        
        log.info("Exporting campaigns in {} format", request.getFormat());
        ExportResponse response = campaignManagerService.exportCampaigns(request);
        return ResponseEntity.ok(response);
    }
    
    // ===== 실시간 모니터링 =====
    
    @GetMapping("/{campaignId}/realtime")
    @Operation(summary = "실시간 모니터링", description = "캠페인의 실시간 상태를 모니터링합니다.")
    public ResponseEntity<RealtimeMonitoringResponse> getRealtimeMonitoring(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        RealtimeMonitoringResponse response = campaignManagerService.getRealtimeMonitoring(campaignId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/dashboard")
    @Operation(summary = "대시보드", description = "전체 캠페인 대시보드 데이터를 조회합니다.")
    public ResponseEntity<DashboardResponse> getDashboard(
            @Parameter(description = "대시보드 요청")
            @RequestParam(required = false) String period) {
        
        DashboardRequest request = DashboardRequest.builder()
            .period(period != null ? period : "today")
            .build();
        
        DashboardResponse response = campaignManagerService.getDashboard(request);
        return ResponseEntity.ok(response);
    }
    
    // ===== 알림 및 웹훅 =====
    
    @PostMapping("/{campaignId}/webhooks")
    @Operation(summary = "웹훅 설정", description = "캠페인 이벤트에 대한 웹훅을 설정합니다.")
    public ResponseEntity<WebhookResponse> setWebhook(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "웹훅 설정", required = true)
            @Valid @RequestBody WebhookRequest request) {
        
        log.info("Setting webhook for campaign: {}", campaignId);
        WebhookResponse response = campaignManagerService.setWebhook(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{campaignId}/alerts")
    @Operation(summary = "알림 설정 조회", description = "캠페인의 알림 설정을 조회합니다.")
    public ResponseEntity<List<AlertResponse>> getAlerts(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        List<AlertResponse> alerts = campaignManagerService.getAlerts(campaignId);
        return ResponseEntity.ok(alerts);
    }
    
    @PostMapping("/{campaignId}/alerts")
    @Operation(summary = "알림 설정", description = "캠페인에 대한 알림을 설정합니다.")
    public ResponseEntity<AlertResponse> createAlert(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "알림 설정", required = true)
            @Valid @RequestBody AlertRequest request) {
        
        log.info("Creating alert for campaign: {}", campaignId);
        AlertResponse response = campaignManagerService.createAlert(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    // ===== A/B 테스트 =====
    
    @PostMapping("/{campaignId}/ab-test")
    @Operation(summary = "A/B 테스트 생성", description = "캠페인에 대한 A/B 테스트를 생성합니다.")
    public ResponseEntity<ABTestResponse> createABTest(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId,
            @Parameter(description = "A/B 테스트 설정", required = true)
            @Valid @RequestBody ABTestRequest request) {
        
        log.info("Creating A/B test for campaign: {}", campaignId);
        ABTestResponse response = campaignManagerService.createABTest(campaignId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{campaignId}/ab-test")
    @Operation(summary = "A/B 테스트 결과", description = "A/B 테스트 결과를 조회합니다.")
    public ResponseEntity<ABTestResultResponse> getABTestResult(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        
        ABTestResultResponse response = campaignManagerService.getABTestResult(campaignId);
        return ResponseEntity.ok(response);
    }
    
    // ===== 헬스 체크 =====
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "Campaign Manager",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(health);
    }
} 