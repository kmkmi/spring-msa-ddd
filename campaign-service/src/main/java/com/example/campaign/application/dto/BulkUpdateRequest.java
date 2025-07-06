package com.example.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateRequest {
    @NotEmpty(message = "캠페인 ID 목록은 필수입니다")
    private List<Long> campaignIds;
    
    @NotNull(message = "수정할 필드는 필수입니다")
    private Map<String, Object> fieldsToUpdate;
    
    private String updateReason;
    private Boolean validateBeforeUpdate;
} 