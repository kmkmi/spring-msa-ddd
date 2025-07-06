package com.example.adrecommendation.domain;

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
public class UserProfile {
    private String userId;
    private String deviceType;
    private String location;
    private List<String> interests;
    private Map<String, Double> categoryPreferences;
    private Double averageCTR;
    private Integer totalClicks;
    private Integer totalImpressions;
} 