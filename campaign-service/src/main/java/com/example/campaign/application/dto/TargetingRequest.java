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
public class TargetingRequest {
    private String targetingType; // BEHAVIORAL, CONTEXTUAL, RETARGETING
    private List<String> ageGroups;
    private List<String> genders;
    private List<String> locations;
    private List<String> interests;
    private List<String> deviceTypes;
    private Map<String, Object> customCriteria;
    private Boolean excludeExistingCustomers;
    private List<String> excludedAudiences;
} 