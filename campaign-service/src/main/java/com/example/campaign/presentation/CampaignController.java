package com.example.campaign.presentation;

import com.example.campaign.application.dto.CreateCampaignRequest;
import com.example.campaign.application.dto.CampaignResponse;
import com.example.campaign.application.service.CampaignService;
import com.example.campaign.domain.Campaign;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Campaign Management", description = "캠페인 관리 API")
public class CampaignController {
    
    private final CampaignService campaignService;
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Campaign Service is healthy");
    }
    
    @PostMapping
    @Operation(summary = "캠페인 생성", description = "새로운 캠페인을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "캠페인 생성 성공",
            content = @Content(schema = @Schema(implementation = CampaignResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "퍼블리셔를 찾을 수 없음")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<CampaignResponse> createCampaign(
            @Parameter(description = "캠페인 생성 요청", required = true)
            @Valid @RequestBody CreateCampaignRequest request) {
        log.info("Creating campaign: {}", request.getName());
        CampaignResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "캠페인 조회", description = "ID로 캠페인을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캠페인 조회 성공",
            content = @Content(schema = @Schema(implementation = CampaignResponse.class))),
        @ApiResponse(responseCode = "404", description = "캠페인을 찾을 수 없음")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER')")
    public ResponseEntity<CampaignResponse> getCampaignById(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long id) {
        log.info("Getting campaign by id: {}", id);
        CampaignResponse response = campaignService.getCampaignById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "모든 캠페인 조회", description = "모든 캠페인 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캠페인 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = CampaignResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER')")
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
        log.info("Getting all campaigns");
        List<CampaignResponse> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }
    
    @GetMapping("/publisher/{publisherId}")
    @Operation(summary = "퍼블리셔별 캠페인 조회", description = "특정 퍼블리셔의 캠페인 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캠페인 목록 조회 성공",
            content = @Content(schema = @Schema(implementation = CampaignResponse.class)))
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<List<CampaignResponse>> getCampaignsByPublisher(
            @Parameter(description = "퍼블리셔 ID", required = true)
            @PathVariable Long publisherId) {
        log.info("Getting campaigns for publisher: {}", publisherId);
        List<CampaignResponse> campaigns = campaignService.getCampaignsByPublisher(publisherId);
        return ResponseEntity.ok(campaigns);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "상태별 캠페인 조회", description = "특정 상태의 캠페인 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캠페인 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER')")
    public ResponseEntity<List<CampaignResponse>> getCampaignsByStatus(
            @Parameter(description = "캠페인 상태", required = true)
            @PathVariable Campaign.CampaignStatus status) {
        log.info("Received request to get campaigns by status: {}", status);
        List<CampaignResponse> response = campaignService.getCampaignsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/active")
    @Operation(summary = "활성 캠페인 조회", description = "현재 활성 상태인 캠페인 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "활성 캠페인 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'ADVERTISER', 'USER')")
    public ResponseEntity<List<CampaignResponse>> getActiveCampaigns() {
        log.info("Received request to get active campaigns");
        List<CampaignResponse> response = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "캠페인 상태 변경", description = "캠페인의 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
        @ApiResponse(responseCode = "404", description = "캠페인을 찾을 수 없음")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<CampaignResponse> updateCampaignStatus(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "새로운 상태", required = true)
            @RequestParam Campaign.CampaignStatus status) {
        log.info("Updating campaign status for id: {} to: {}", id, status);
        CampaignResponse response = campaignService.updateCampaignStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/budget")
    @Operation(summary = "캠페인 예산 변경", description = "캠페인의 예산을 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캠페인 예산 변경 성공",
            content = @Content(schema = @Schema(implementation = CampaignResponse.class))),
        @ApiResponse(responseCode = "404", description = "캠페인을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    public ResponseEntity<CampaignResponse> updateCampaignBudget(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "총 예산", required = true)
            @RequestParam BigDecimal budgetAmount,
            @Parameter(description = "일일 예산", required = true)
            @RequestParam BigDecimal dailyBudget) {
        log.info("Received request to update campaign budget for id: {} to budget: {}, daily: {}", 
                id, budgetAmount, dailyBudget);
        CampaignResponse response = campaignService.updateCampaignBudget(id, budgetAmount, dailyBudget);
        return ResponseEntity.ok(response);
    }
} 