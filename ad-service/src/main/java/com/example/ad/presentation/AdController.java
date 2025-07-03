package com.example.ad.presentation;

import com.example.ad.application.dto.AdvertisementRequest;
import com.example.ad.application.dto.AdvertisementResponse;
import com.example.ad.application.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Tag(name = "Advertisement", description = "광고 관리 API")
public class AdController {
    private final AdvertisementService service;

    @PostMapping
    @Operation(summary = "광고 생성", description = "새로운 광고를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "광고 생성 성공",
            content = @Content(schema = @Schema(implementation = AdvertisementResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<AdvertisementResponse> create(
            @Parameter(description = "광고 생성 요청 데이터", required = true)
            @Valid @RequestBody AdvertisementRequest req) {
        return ResponseEntity.ok(service.createAd(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "광고 조회", description = "ID로 특정 광고를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "광고 조회 성공",
            content = @Content(schema = @Schema(implementation = AdvertisementResponse.class))),
        @ApiResponse(responseCode = "404", description = "광고를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<AdvertisementResponse> get(
            @Parameter(description = "광고 ID", required = true)
            @PathVariable Long id) {
        AdvertisementResponse res = service.getAd(id);
        return res != null ? ResponseEntity.ok(res) : ResponseEntity.notFound().build();
    }

    @GetMapping("/campaign/{campaignId}")
    @Operation(summary = "캠페인별 광고 조회", description = "특정 캠페인의 광고 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "광고 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<AdvertisementResponse>> getByCampaign(
            @Parameter(description = "캠페인 ID", required = true)
            @PathVariable Long campaignId) {
        return ResponseEntity.ok(service.getAdsByCampaign(campaignId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "광고 상태 변경", description = "광고의 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "광고 상태 변경 성공",
            content = @Content(schema = @Schema(implementation = AdvertisementResponse.class))),
        @ApiResponse(responseCode = "404", description = "광고를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<AdvertisementResponse> updateStatus(
            @Parameter(description = "광고 ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "변경할 광고 상태", required = true)
            @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PutMapping("/{id}/metrics")
    @Operation(summary = "광고 메트릭 업데이트", description = "광고의 성과 지표를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메트릭 업데이트 성공",
            content = @Content(schema = @Schema(implementation = AdvertisementResponse.class))),
        @ApiResponse(responseCode = "404", description = "광고를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<AdvertisementResponse> updateMetrics(
            @Parameter(description = "광고 ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "노출수", required = true)
            @RequestParam Long impressions, 
            @Parameter(description = "클릭수", required = true)
            @RequestParam Long clicks, 
            @Parameter(description = "클릭률 (선택사항)")
            @RequestParam(required = false) Double ctr) {
        return ResponseEntity.ok(service.updateMetrics(id, impressions, clicks, ctr));
    }

    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "서비스 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "서비스 정상 동작")
    })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Ad Service is healthy");
    }
} 