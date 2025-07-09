package com.example.batchprocessing.controller;

import com.example.batchprocessing.domain.BatchJobResult;
import com.example.batchprocessing.service.BatchProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Batch Processing", description = "배치 처리 작업 관리 API")
public class BatchProcessingController {
    
    private final BatchProcessingService batchProcessingService;
    
    @PostMapping("/start")
    @Operation(summary = "배치 작업 시작", description = "새로운 배치 작업을 시작합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "배치 작업 시작 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<BatchJobResult> startBatchJob(
            @Parameter(description = "배치 작업 요청", required = true)
            @RequestBody Map<String, String> request) {
        
        String jobName = request.get("jobName");
        String jobType = request.get("jobType");
        
        log.info("Starting batch job: {} of type: {}", jobName, jobType);
        
        BatchJobResult result = batchProcessingService.startBatchJob(jobName);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/status/{jobId}")
    @Operation(summary = "배치 작업 상태 조회", description = "배치 작업의 현재 상태를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상태 조회 성공"),
        @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음")
    })
    public ResponseEntity<BatchJobResult> getJobStatus(
            @Parameter(description = "작업 ID", required = true)
            @PathVariable String jobId) {
        
        log.info("Getting status for job: {}", jobId);
        
        BatchJobResult result = batchProcessingService.getJobStatus(jobId);
        
        if ("NOT_FOUND".equals(result.getStatus())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/daily-aggregation")
    @Operation(summary = "일별 집계 작업 실행", description = "일별 광고 메트릭 집계 작업을 실행합니다.")
    public ResponseEntity<String> runDailyAggregation() {
        log.info("Triggering daily aggregation job");
        
        // 스케줄된 작업을 수동으로 실행
        batchProcessingService.runDailyMetricsAggregation();
        
        return ResponseEntity.ok("Daily aggregation job triggered successfully");
    }
    
    @PostMapping("/hourly-aggregation")
    @Operation(summary = "시간별 집계 작업 실행", description = "시간별 광고 메트릭 집계 작업을 실행합니다.")
    public ResponseEntity<String> runHourlyAggregation() {
        log.info("Triggering hourly aggregation job");
        
        // 스케줄된 작업을 수동으로 실행
        batchProcessingService.runHourlyMetricsAggregation();
        
        return ResponseEntity.ok("Hourly aggregation job triggered successfully");
    }
    
    @PostMapping("/data-cleanup")
    @Operation(summary = "데이터 정리 작업 실행", description = "오래된 데이터 정리 작업을 실행합니다.")
    public ResponseEntity<String> runDataCleanup() {
        log.info("Triggering data cleanup job");
        
        // 스케줄된 작업을 수동으로 실행
        batchProcessingService.runDataCleanupJob();
        
        return ResponseEntity.ok("Data cleanup job triggered successfully");
    }
    
    @DeleteMapping("/cancel/{jobId}")
    @Operation(summary = "배치 작업 취소", description = "실행 중인 배치 작업을 취소합니다.")
    public ResponseEntity<String> cancelJob(
            @Parameter(description = "작업 ID", required = true)
            @PathVariable String jobId) {
        
        log.info("Cancelling job: {}", jobId);
        
        // 실제 구현에서는 작업 취소 로직 구현
        // 현재는 간단한 응답만 반환
        
        return ResponseEntity.ok("Job cancellation requested for: " + jobId);
    }
    
    @GetMapping("/jobs")
    @Operation(summary = "모든 배치 작업 조회", description = "모든 배치 작업의 목록을 조회합니다.")
    public ResponseEntity<List<BatchJobResult>> getAllJobs() {
        log.info("Getting all batch jobs");
        
        // 실제 구현에서는 모든 작업 목록 반환
        // 현재는 빈 리스트 반환
        
        return ResponseEntity.ok(List.of());
    }
    
    @GetMapping("/health")
    @Operation(summary = "헬스 체크", description = "배치 처리 서비스의 상태를 확인합니다.")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Batch Processing Service is healthy");
    }
} 