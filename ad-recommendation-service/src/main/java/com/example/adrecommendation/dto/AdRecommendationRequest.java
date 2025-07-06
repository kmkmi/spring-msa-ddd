package com.example.adrecommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdRecommendationRequest {
    private String userId;
    private String sessionId;
    private String deviceType;
    private String location;
    private String placement;
    private Integer limit;
} 