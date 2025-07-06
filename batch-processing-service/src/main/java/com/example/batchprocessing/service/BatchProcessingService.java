package com.example.batchprocessing.service;

import com.example.batchprocessing.domain.BatchJobResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProcessingService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    // 작업 상태 추적을 위한 메모리 캐시
    private final Map<String, BatchJobResult> jobStatusCache = new ConcurrentHashMap<>();

    private static final String BATCH_RESULTS_TOPIC = "batch-results";

    /**
     * 일별 광고 메트릭 집계 배치 작업
     */
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    public void runDailyMetricsAggregation() {
        String jobId = UUID.randomUUID().toString();
        BatchJobResult jobResult = BatchJobResult.builder()
            .jobId(jobId)
            .jobName("Daily Metrics Aggregation")
            .jobType("DAILY_REPORT")
            .status("RUNNING")
            .startTime(LocalDateTime.now())
            .dataSource("Redis")
            .dataTarget("Data Warehouse")
            .build();
        
        jobStatusCache.put(jobId, jobResult);
        
        try {
            log.info("Starting daily metrics aggregation job: {}", jobId);
            
            // 일별 메트릭 집계 로직
            aggregateDailyMetrics();
            
            // 작업 완료 상태 업데이트
            jobResult.setStatus("COMPLETED");
            jobResult.setEndTime(LocalDateTime.now());
            jobResult.setExecutionTimeMs(
                java.time.Duration.between(jobResult.getStartTime(), jobResult.getEndTime()).toMillis()
            );
            
            // 결과를 Kafka로 전송
            publishBatchResult(jobResult);
            
            log.info("Daily metrics aggregation completed: {}", jobId);
            
        } catch (Exception e) {
            log.error("Daily metrics aggregation failed: {}", e.getMessage(), e);
            jobResult.setStatus("FAILED");
            jobResult.setErrorMessage(e.getMessage());
            jobResult.setEndTime(LocalDateTime.now());
        }
    }

    /**
     * 시간별 광고 메트릭 집계 배치 작업
     */
    @Scheduled(cron = "0 0 * * * ?") // 매시간 실행
    public void runHourlyMetricsAggregation() {
        String jobId = UUID.randomUUID().toString();
        BatchJobResult jobResult = BatchJobResult.builder()
            .jobId(jobId)
            .jobName("Hourly Metrics Aggregation")
            .jobType("HOURLY_AGGREGATION")
            .status("RUNNING")
            .startTime(LocalDateTime.now())
            .dataSource("Redis")
            .dataTarget("Data Warehouse")
            .build();
        
        jobStatusCache.put(jobId, jobResult);
        
        try {
            log.info("Starting hourly metrics aggregation job: {}", jobId);
            
            // 시간별 메트릭 집계 로직
            aggregateHourlyMetrics();
            
            // 작업 완료 상태 업데이트
            jobResult.setStatus("COMPLETED");
            jobResult.setEndTime(LocalDateTime.now());
            jobResult.setExecutionTimeMs(
                java.time.Duration.between(jobResult.getStartTime(), jobResult.getEndTime()).toMillis()
            );
            
            // 결과를 Kafka로 전송
            publishBatchResult(jobResult);
            
            log.info("Hourly metrics aggregation completed: {}", jobId);
            
        } catch (Exception e) {
            log.error("Hourly metrics aggregation failed: {}", e.getMessage(), e);
            jobResult.setStatus("FAILED");
            jobResult.setErrorMessage(e.getMessage());
            jobResult.setEndTime(LocalDateTime.now());
        }
    }

    /**
     * 데이터 정리 배치 작업
     */
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
    public void runDataCleanupJob() {
        String jobId = UUID.randomUUID().toString();
        BatchJobResult jobResult = BatchJobResult.builder()
            .jobId(jobId)
            .jobName("Data Cleanup")
            .jobType("DATA_CLEANUP")
            .status("RUNNING")
            .startTime(LocalDateTime.now())
            .dataSource("Redis")
            .dataTarget("Archive")
            .build();
        
        jobStatusCache.put(jobId, jobResult);
        
        try {
            log.info("Starting data cleanup job: {}", jobId);
            
            // 오래된 데이터 정리 로직
            cleanupOldData();
            
            // 작업 완료 상태 업데이트
            jobResult.setStatus("COMPLETED");
            jobResult.setEndTime(LocalDateTime.now());
            jobResult.setExecutionTimeMs(
                java.time.Duration.between(jobResult.getStartTime(), jobResult.getEndTime()).toMillis()
            );
            
            // 결과를 Kafka로 전송
            publishBatchResult(jobResult);
            
            log.info("Data cleanup completed: {}", jobId);
            
        } catch (Exception e) {
            log.error("Data cleanup failed: {}", e.getMessage(), e);
            jobResult.setStatus("FAILED");
            jobResult.setErrorMessage(e.getMessage());
            jobResult.setEndTime(LocalDateTime.now());
        }
    }

    /**
     * 일별 메트릭 집계
     */
    private void aggregateDailyMetrics() {
        String yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Redis에서 일별 데이터 수집
        // 실제 구현에서는 Redis에서 데이터를 읽어와서 집계 처리
        log.info("Aggregating daily metrics for date: {}", yesterday);
        
        // 예시: 광고별 일별 집계
        // 실제로는 Redis에서 모든 광고의 일별 데이터를 읽어와서 집계
    }

    /**
     * 시간별 메트릭 집계
     */
    private void aggregateHourlyMetrics() {
        String currentHour = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));
        
        // Redis에서 시간별 데이터 수집
        log.info("Aggregating hourly metrics for hour: {}", currentHour);
        
        // 예시: 광고별 시간별 집계
        // 실제로는 Redis에서 모든 광고의 시간별 데이터를 읽어와서 집계
    }

    /**
     * 오래된 데이터 정리
     */
    private void cleanupOldData() {
        // 30일 이상 된 데이터 정리
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        log.info("Cleaning up data older than: {}", cutoffDate);
        
        // Redis에서 오래된 키들을 찾아서 삭제
        // 실제 구현에서는 Redis의 TTL 기능을 활용하거나 직접 삭제
    }

    /**
     * 배치 작업 결과를 Kafka로 전송
     */
    private void publishBatchResult(BatchJobResult jobResult) {
        try {
            kafkaTemplate.send(BATCH_RESULTS_TOPIC, jobResult.getJobId(), jobResult);
            log.info("Batch result published to Kafka: {}", jobResult.getJobId());
        } catch (Exception e) {
            log.error("Failed to publish batch result to Kafka: {}", e.getMessage(), e);
        }
    }

    /**
     * 배치 작업 시작
     */
    public BatchJobResult startBatchJob(String jobName) {
        log.info("Starting batch job: {}", jobName);
        
        String jobId = UUID.randomUUID().toString();
        
        BatchJobResult jobResult = BatchJobResult.builder()
            .jobId(jobId)
            .jobName(jobName)
            .status("RUNNING")
            .startTime(LocalDateTime.now())
            .recordsProcessed(0L)
            .build();
        
        jobStatusCache.put(jobId, jobResult);
        
        return jobResult;
    }
    
    /**
     * 배치 작업 상태 조회
     */
    public BatchJobResult getJobStatus(String jobId) {
        log.info("Getting status for job: {}", jobId);
        
        BatchJobResult jobResult = jobStatusCache.get(jobId);
        if (jobResult == null) {
            // 캐시에 없으면 기본값 반환
            return BatchJobResult.builder()
                .jobId(jobId)
                .jobName("Unknown Job")
                .status("NOT_FOUND")
                .build();
        }
        
        return jobResult;
    }
} 