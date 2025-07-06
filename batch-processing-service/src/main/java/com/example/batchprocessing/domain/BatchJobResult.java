package com.example.batchprocessing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobResult {
    
    private String jobId;
    private String jobName;
    private String status; // RUNNING, COMPLETED, FAILED, CANCELLED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long recordsProcessed;
    private Long recordsFailed;
    private String errorMessage;
    private String jobType; // DAILY_REPORT, HOURLY_AGGREGATION, DATA_CLEANUP, ETL
    private String parameters; // JSON 형태의 작업 파라미터
    private Long executionTimeMs;
    private String outputLocation;
    private String dataSource;
    private String dataTarget;
    
    // 성능 메트릭
    private Long memoryUsedMb;
    private Long cpuUsagePercent;
    private Long networkIoMb;
    private Long diskIoMb;
    
    // 데이터 품질 메트릭
    private Long duplicateRecords;
    private Long nullRecords;
    private Long invalidRecords;
    private Double dataQualityScore;
} 