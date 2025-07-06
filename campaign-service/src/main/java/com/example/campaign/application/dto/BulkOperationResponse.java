package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationResponse {
    private String operationType;
    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<Long> successfulIds;
    private List<Map<String, Object>> failures;
    private String message;
} 