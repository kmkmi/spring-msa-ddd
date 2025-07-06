package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportResponse {
    private String exportId;
    private String status; // PROCESSING, COMPLETED, FAILED
    private String downloadUrl;
    private LocalDateTime completedAt;
    private String fileName;
    private Long fileSize;
    private String message;
} 